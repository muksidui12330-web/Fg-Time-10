package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

class FirebaseChatService(
    private val db: FirebaseFirestore,
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        FirebaseAuth.getInstance()
    )

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    // Convert FirebaseAuth state changes into a reactive Kotlin Flow
    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    // Save or update user profile in Firestore
    suspend fun saveUserProfile(user: User) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = db.collection("users").document(uid)
        try {
            val data = mapOf(
                "id" to uid,
                "username" to user.username,
                "fullName" to user.fullName,
                "avatarUrl" to user.avatarUrl,
                "coverUrl" to user.coverUrl,
                "bio" to user.bio,
                "website" to user.website,
                "isVerified" to user.isVerified,
                "isPrivate" to user.isPrivate,
                "isOnline" to true,
                "lastActive" to "Online",
                "followersCount" to user.followersCount,
                "followingCount" to user.followingCount,
                "postsCount" to user.postsCount,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    // Fetch user profile
    suspend fun getUserProfile(userId: String): User? {
        val docRef = db.collection("users").document(userId)
        return try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                User(
                    id = snapshot.getString("id") ?: userId,
                    username = snapshot.getString("username") ?: "user",
                    fullName = snapshot.getString("fullName") ?: "FG Member",
                    avatarUrl = snapshot.getString("avatarUrl") ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                    coverUrl = snapshot.getString("coverUrl") ?: "",
                    bio = snapshot.getString("bio") ?: "",
                    website = snapshot.getString("website") ?: "",
                    isVerified = snapshot.getBoolean("isVerified") ?: false,
                    isPrivate = snapshot.getBoolean("isPrivate") ?: false,
                    isOnline = snapshot.getBoolean("isOnline") ?: true,
                    lastActive = snapshot.getString("lastActive") ?: "Recently",
                    followersCount = (snapshot.getLong("followersCount") ?: 0L).toInt(),
                    followingCount = (snapshot.getLong("followingCount") ?: 0L).toInt(),
                    postsCount = (snapshot.getLong("postsCount") ?: 0L).toInt(),
                    isFollowing = false
                )
            } else null
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            null
        }
    }

    // Real-time observation of all registered users (for user search & new chats)
    fun observeUsers(): Flow<List<User>> = callbackFlow {
        val currentUid = auth.currentUser?.uid ?: ""
        val listener = db.collection("users")
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "users")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val usersList = snapshot.documents.mapNotNull { doc ->
                        val id = doc.getString("id") ?: doc.id
                        if (id == currentUid) null
                        else User(
                            id = id,
                            username = doc.getString("username") ?: "user",
                            fullName = doc.getString("fullName") ?: "FG Member",
                            avatarUrl = doc.getString("avatarUrl") ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                            coverUrl = doc.getString("coverUrl") ?: "",
                            bio = doc.getString("bio") ?: "",
                            website = doc.getString("website") ?: "",
                            isVerified = doc.getBoolean("isVerified") ?: false,
                            isPrivate = doc.getBoolean("isPrivate") ?: false,
                            isOnline = doc.getBoolean("isOnline") ?: true,
                            lastActive = doc.getString("lastActive") ?: "Recently",
                            followersCount = (doc.getLong("followersCount") ?: 0L).toInt(),
                            followingCount = (doc.getLong("followingCount") ?: 0L).toInt(),
                            postsCount = (doc.getLong("postsCount") ?: 0L).toInt(),
                            isFollowing = false
                        )
                    }
                    trySend(usersList)
                }
            }
        awaitClose { listener.remove() }
    }

    // Generate deterministic conversation ID for 1-on-1 chats
    fun getConversationId(userId1: String, userId2: String): String {
        val sorted = listOf(userId1, userId2).sorted()
        return "direct_${sorted[0]}_${sorted[1]}"
    }

    // Real-time observation of current user's conversations
    fun observeConversations(currentUserId: String): Flow<List<Conversation>> = callbackFlow {
        val listener = db.collection("conversations")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "conversations")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val convList = snapshot.documents.mapNotNull { doc ->
                        val participants = (doc.get("participants") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                        val otherId = participants.firstOrNull { it != currentUserId } ?: return@mapNotNull null
                        val otherName = doc.getString("participantName_$otherId") ?: "FG Member"
                        val otherAvatar = doc.getString("participantAvatar_$otherId") ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400"
                        val otherUsername = doc.getString("participantUsername_$otherId") ?: "user"

                        Conversation(
                            id = doc.id,
                            participant = User(
                                id = otherId,
                                username = otherUsername,
                                fullName = otherName,
                                avatarUrl = otherAvatar,
                                isOnline = true
                            ),
                            lastMessage = doc.getString("lastMessage") ?: "No messages yet",
                            lastMessageTime = doc.getString("lastMessageTime") ?: "Recently",
                            unreadCount = 0,
                            isGroup = false,
                            groupName = null
                        )
                    }
                    trySend(convList)
                }
            }
        awaitClose { listener.remove() }
    }

    // Real-time observation of messages in a conversation
    fun observeMessages(conversationId: String, currentUserId: String): Flow<List<Message>> = callbackFlow {
        val listener = db.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "conversations/$conversationId/messages")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messageList = snapshot.documents.mapNotNull { doc ->
                        val senderId = doc.getString("senderId") ?: return@mapNotNull null
                        Message(
                            id = doc.id,
                            senderId = senderId,
                            text = doc.getString("text") ?: "",
                            timestamp = doc.getString("timestamp") ?: "Just now",
                            isMe = senderId == currentUserId,
                            status = when (doc.getString("status")) {
                                "READ" -> MessageStatus.READ
                                "DELIVERED" -> MessageStatus.DELIVERED
                                else -> MessageStatus.SENT
                            },
                            mediaUrl = doc.getString("mediaUrl"),
                            isVoiceNote = doc.getBoolean("isVoiceNote") ?: false,
                            voiceDurationSec = (doc.getLong("voiceDurationSec") ?: 0L).toInt()
                        )
                    }
                    trySend(messageList)
                }
            }
        awaitClose { listener.remove() }
    }

    // Send a message and update conversation document
    suspend fun sendMessage(
        recipientUser: User,
        currentUser: User,
        text: String,
        mediaUrl: String? = null,
        isVoice: Boolean = false,
        voiceDur: Int = 0
    ) {
        val currentUid = auth.currentUser?.uid ?: currentUser.id
        val conversationId = getConversationId(currentUid, recipientUser.id)
        val convRef = db.collection("conversations").document(conversationId)
        val timeString = timeFormat.format(Date())

        // Ensure conversation metadata document exists/updates
        val convData = mapOf(
            "id" to conversationId,
            "participants" to listOf(currentUid, recipientUser.id),
            "participantName_$currentUid" to currentUser.fullName,
            "participantUsername_$currentUid" to currentUser.username,
            "participantAvatar_$currentUid" to currentUser.avatarUrl,
            "participantName_${recipientUser.id}" to recipientUser.fullName,
            "participantUsername_${recipientUser.id}" to recipientUser.username,
            "participantAvatar_${recipientUser.id}" to recipientUser.avatarUrl,
            "lastMessage" to if (isVoice) "🎤 Voice message" else text.ifBlank { "📷 Photo" },
            "lastSenderId" to currentUid,
            "lastMessageTime" to timeString,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            convRef.set(convData).await()

            // Add message to sub-collection
            val messageId = "msg_${UUID.randomUUID().toString().take(12)}"
            val msgRef = convRef.collection("messages").document(messageId)
            val msgData = mapOf(
                "id" to messageId,
                "conversationId" to conversationId,
                "senderId" to currentUid,
                "recipientId" to recipientUser.id,
                "text" to text,
                "timestamp" to timeString,
                "status" to "DELIVERED",
                "mediaUrl" to mediaUrl,
                "isVoiceNote" to isVoice,
                "voiceDurationSec" to voiceDur,
                "createdAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            msgRef.set(msgData).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, convRef.path)
            throw e
        }
    }
}
