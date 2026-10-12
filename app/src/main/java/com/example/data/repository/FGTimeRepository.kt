package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FGTimeRepository {

    private val nowFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

    // Current Logged In User
    val currentUser = MutableStateFlow(
        User(
            id = "user_me",
            username = "leaf_creator",
            fullName = "Alex Rivera",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
            coverUrl = "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?w=800",
            bio = "🌱 Eco-photographer & digital designer | Connecting creators on FG TIME | Living the green leaf lifestyle 🌿",
            website = "fgtime.social/alex",
            isVerified = true,
            isPrivate = false,
            isOnline = true,
            lastActive = "Online",
            followersCount = 14,
            followingCount = 12,
            postsCount = 4,
            isAdmin = true
        )
    )

    // Auth status
    val isAuthenticated = MutableStateFlow(true)
    val isDemoMode = MutableStateFlow(false)

    // State flows
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val _reels = MutableStateFlow<List<Reel>>(emptyList())
    val reels: StateFlow<List<Reel>> = _reels.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _suggestedUsers = MutableStateFlow<List<User>>(emptyList())
    val suggestedUsers: StateFlow<List<User>> = _suggestedUsers.asStateFlow()

    private val _reports = MutableStateFlow<List<ReportItem>>(emptyList())
    val reports: StateFlow<List<ReportItem>> = _reports.asStateFlow()

    private val _betaFeedbacks = MutableStateFlow<List<BetaFeedback>>(emptyList())
    val betaFeedbacks: StateFlow<List<BetaFeedback>> = _betaFeedbacks.asStateFlow()

    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val blockedUserIds: StateFlow<Set<String>> = _blockedUserIds.asStateFlow()

    private val registeredAccounts = mutableMapOf<String, User>()

    private val _activeCall = MutableStateFlow<ActiveCall?>(null)
    val activeCall: StateFlow<ActiveCall?> = _activeCall.asStateFlow()

    // Preferences
    val isDarkMode = MutableStateFlow(false)
    val isDataSaver = MutableStateFlow(false)

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val users = listOf(
            User(
                id = "u1",
                username = "maya_nature",
                fullName = "Maya Chen",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
                coverUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
                bio = "Botanical illustrator 🌸 Exploring emerald valleys | FG TIME advocate",
                website = "mayachen.art",
                isVerified = true,
                followersCount = 34,
                followingCount = 20,
                postsCount = 8,
                isFollowing = true
            ),
            User(
                id = "u2",
                username = "green_architect",
                fullName = "Liam Vance",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                bio = "Sustainable cities & vertical forests 🏢🌿",
                isVerified = true,
                followersCount = 42,
                followingCount = 18,
                postsCount = 14,
                isFollowing = false
            ),
            User(
                id = "u3",
                username = "clara_travels",
                fullName = "Clara Dupont",
                avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400",
                bio = "Wanderlust • Hiking peaks across Patagonia and Bali 🎒✨",
                followersCount = 25,
                followingCount = 19,
                postsCount = 6,
                isFollowing = true
            ),
            User(
                id = "u4",
                username = "zen_coffee",
                fullName = "Zen Roast",
                avatarUrl = "https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?w=400",
                bio = "Organic shade-grown single origin coffee lovers ☕🍃",
                followersCount = 31,
                followingCount = 15,
                postsCount = 7,
                isFollowing = false
            )
        )
        _suggestedUsers.value = users

        val initialPosts = listOf(
            Post(
                id = "p1",
                userId = "u1",
                username = "maya_nature",
                userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
                isVerified = true,
                caption = "Morning dew on fresh monstera leaves. The quiet beauty of waking up with nature. 🌿✨ Welcome everyone to FG TIME! #GreenLife #MorningVibes #FGTime",
                imageUrl = "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?w=800",
                location = "Kyoto Botanical Gardens",
                timestamp = "2h ago",
                likesCount = 342,
                commentsCount = 28,
                isLiked = true,
                isSaved = false,
                tags = listOf("GreenLife", "MorningVibes", "FGTime")
            ),
            Post(
                id = "p2",
                userId = "u2",
                username = "green_architect",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                isVerified = true,
                caption = "Biophilic design concept for our next community center. Sunlight, solar panels, and climbing vines everywhere! 🌱 Architecture that breathes. What do you think?",
                imageUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800",
                location = "Singapore Eco District",
                timestamp = "4h ago",
                likesCount = 894,
                commentsCount = 76,
                isLiked = false,
                isSaved = true,
                tags = listOf("Sustainability", "Architecture", "EcoFriendly")
            ),
            Post(
                id = "p3",
                userId = "u3",
                username = "clara_travels",
                userAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400",
                isVerified = false,
                caption = "Mist rolling over the alpine pine canopy at sunrise. Breathing in pure crisp air 🌲⛰️ Take time to unplug today! #MountainLife #Wanderlust",
                imageUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800",
                location = "Swiss Alps",
                timestamp = "6h ago",
                likesCount = 619,
                commentsCount = 41,
                isLiked = false,
                isSaved = false,
                tags = listOf("MountainLife", "Wanderlust", "Alpine")
            ),
            Post(
                id = "p4",
                userId = "user_me",
                username = "leaf_creator",
                userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                isVerified = true,
                caption = "Testing the brand new camera macro lens on emerald moss! Growth happens millimeter by millimeter every day. Keep going! 🍃💚 #FGTime #MacroWorld",
                imageUrl = "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=800",
                location = "Olympic National Park",
                timestamp = "1d ago",
                likesCount = 490,
                commentsCount = 33,
                isLiked = true,
                isSaved = true,
                tags = listOf("FGTime", "MacroWorld", "Growth")
            )
        )
        _posts.value = initialPosts

        val initialStories = listOf(
            Story(
                id = "s_me",
                userId = "user_me",
                username = "Your Story",
                userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                mediaUrl = "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=800",
                timestamp = "1h ago",
                viewersCount = 42,
                isViewed = false
            ),
            Story(
                id = "s1",
                userId = "u1",
                username = "maya_nature",
                userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
                mediaUrl = "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?w=800",
                timestamp = "3h ago",
                viewersCount = 189,
                isViewed = false
            ),
            Story(
                id = "s2",
                userId = "u2",
                username = "green_arch",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                mediaUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800",
                timestamp = "5h ago",
                viewersCount = 312,
                isViewed = false
            ),
            Story(
                id = "s3",
                userId = "u3",
                username = "clara_t",
                userAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400",
                mediaUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800",
                timestamp = "8h ago",
                viewersCount = 95,
                isViewed = true
            ),
            Story(
                id = "s4",
                userId = "u4",
                username = "zen_coffee",
                userAvatar = "https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?w=400",
                mediaUrl = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=800",
                timestamp = "12h ago",
                viewersCount = 520,
                isViewed = true
            )
        )
        _stories.value = initialStories

        val initialReels = listOf(
            Reel(
                id = "r1",
                userId = "u1",
                username = "maya_nature",
                userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
                caption = "How rain droplets bead on lotus leaves (Superhydrophobicity in nature! 💧🍃) #NatureFacts #FGTime",
                audioTrack = "Maya Chen • Lo-Fi Rain Beats",
                likesCount = 42,
                commentsCount = 8,
                videoGradientColors = listOf(0xFF004D40, 0xFF00C853, 0xFF12372A)
            ),
            Reel(
                id = "r2",
                userId = "u2",
                username = "green_architect",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                caption = "Touring the world's tallest indoor waterfall & biodome! 🌿🌊 Sustainable architecture is here.",
                audioTrack = "Original Audio • Sustainable Futures",
                likesCount = 85,
                commentsCount = 12,
                videoGradientColors = listOf(0xFF0D47A1, 0xFF00897B, 0xFF1B5E20)
            ),
            Reel(
                id = "r3",
                userId = "u3",
                username = "clara_travels",
                userAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400",
                caption = "Sunset drone shot soaring over Bali emerald rice terraces 🌾🌅 Pure serenity.",
                audioTrack = "Tropical Serenade • Chillout Lounge",
                likesCount = 61,
                commentsCount = 9,
                videoGradientColors = listOf(0xFFE65100, 0xFF2E7D32, 0xFF12372A)
            )
        )
        _reels.value = initialReels

        // Initial comments
        val p1Comments = listOf(
            Comment("c1", "p1", "u2", "green_architect", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400", "Stunning macro capture Maya! The lighting is immaculate.", "1h ago", 12, true),
            Comment("c2", "p1", "u3", "clara_travels", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400", "FG TIME looks so smooth and clean. Loving this community already!", "45m ago", 8, false)
        )
        val p2Comments = listOf(
            Comment("c3", "p2", "u1", "maya_nature", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400", "The curved glass and living walls look incredible Liam!", "3h ago", 24, true)
        )
        _comments.value = mapOf("p1" to p1Comments, "p2" to p2Comments)

        // Conversations
        val convos = listOf(
            Conversation(
                id = "conv_1",
                participant = users[0],
                lastMessage = "Let's collaborate on the green leaf photography series!",
                lastMessageTime = "10:14 AM",
                unreadCount = 1
            ),
            Conversation(
                id = "conv_2",
                participant = users[1],
                lastMessage = "Thanks for the feedback on the eco tower model.",
                lastMessageTime = "Yesterday",
                unreadCount = 0
            ),
            Conversation(
                id = "conv_3",
                participant = users[2],
                lastMessage = "Did you check out the new stories feature on FG TIME?",
                lastMessageTime = "2d ago",
                unreadCount = 0
            ),
            Conversation(
                id = "conv_group_1",
                participant = users[0],
                lastMessage = "Alex: Check out our new project preview!",
                lastMessageTime = "Yesterday",
                unreadCount = 3,
                isGroup = true,
                groupName = "🌿 FG TIME Creators Hub"
            )
        )
        _conversations.value = convos

        val conv1Messages = listOf(
            Message("m1", "u1", "Hey Alex! Welcome to FG TIME!", "10:00 AM", isMe = false, status = MessageStatus.READ),
            Message("m2", "user_me", "Hey Maya! Loving the design and the green leaf vibe.", "10:05 AM", isMe = true, status = MessageStatus.READ),
            Message("m3", "u1", "Let's collaborate on the green leaf photography series!", "10:14 AM", isMe = false, status = MessageStatus.DELIVERED)
        )
        _messages.value = mapOf("conv_1" to conv1Messages)

        val notifs = listOf(
            NotificationItem("n1", "u1", "maya_nature", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400", "liked your photo", "10m ago", NotificationType.LIKE, "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=800"),
            NotificationItem("n2", "u2", "green_architect", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400", "commented: \"Incredible macro quality Alex!\"", "1h ago", NotificationType.COMMENT, "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=800"),
            NotificationItem("n3", "u3", "clara_travels", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400", "started following you", "3h ago", NotificationType.FOLLOW),
            NotificationItem("n4", "sys", "FG TIME Team", "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?w=800", "Welcome to FG TIME — Green Leaf Edition! Explore, connect, and grow.", "1d ago", NotificationType.SYSTEM)
        )
        _notifications.value = notifs

        val initialReports = listOf(
            ReportItem("rep1", "POST", "p99", "Inappropriate commercial advertising link", "maya_nature", "Yesterday", "PENDING")
        )
        _reports.value = initialReports
    }

    // Post Actions
    fun toggleLike(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newLiked = !post.isLiked
                    val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
                    post.copy(isLiked = newLiked, likesCount = newCount)
                } else post
            }
        }
    }

    fun toggleSave(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) post.copy(isSaved = !post.isSaved) else post
            }
        }
    }

    fun createPost(
        caption: String,
        imageUrl: String,
        videoUrl: String? = null,
        isVideo: Boolean = false,
        location: String = "",
        tags: List<String> = emptyList()
    ) {
        val user = currentUser.value
        val newPost = Post(
            id = "p_${UUID.randomUUID()}",
            userId = user.id,
            username = user.username,
            userAvatar = user.avatarUrl,
            isVerified = user.isVerified,
            caption = caption,
            imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?w=800" },
            videoUrl = videoUrl,
            isVideo = isVideo,
            location = location,
            timestamp = "Just now",
            likesCount = 0,
            commentsCount = 0,
            isLiked = false,
            isSaved = false,
            tags = tags
        )
        _posts.update { listOf(newPost) + it }
        currentUser.update { current ->
            val updated = current.copy(postsCount = current.postsCount + 1)
            registeredAccounts[updated.username] = updated
            registeredAccounts[updated.id] = updated
            updated
        }

        if (isVideo && videoUrl != null) {
            val newReel = Reel(
                id = "r_${UUID.randomUUID()}",
                userId = user.id,
                username = user.username,
                userAvatar = user.avatarUrl,
                caption = caption,
                audioTrack = "Original Audio • ${user.fullName}",
                likesCount = 0,
                commentsCount = 0,
                videoGradientColors = listOf(0xFF004D40, 0xFF00C853, 0xFF12372A)
            )
            _reels.update { listOf(newReel) + it }
        }
    }

    fun deletePost(postId: String) {
        _posts.update { it.filterNot { post -> post.id == postId } }
        currentUser.update { current ->
            val updated = current.copy(postsCount = (current.postsCount - 1).coerceAtLeast(0))
            registeredAccounts[updated.username] = updated
            registeredAccounts[updated.id] = updated
            updated
        }
    }

    fun editPostCaption(postId: String, newCaption: String) {
        _posts.update { list ->
            list.map { if (it.id == postId) it.copy(caption = newCaption) else it }
        }
    }

    // Comment Actions
    fun addComment(postId: String, text: String) {
        val user = currentUser.value
        val newComment = Comment(
            id = "c_${UUID.randomUUID()}",
            postId = postId,
            userId = user.id,
            username = user.username,
            userAvatar = user.avatarUrl,
            text = text,
            timestamp = "Just now",
            likesCount = 0,
            isLiked = false
        )
        _comments.update { map ->
            val existing = map[postId] ?: emptyList()
            map + (postId to (existing + newComment))
        }
        _posts.update { list ->
            list.map { if (it.id == postId) it.copy(commentsCount = it.commentsCount + 1) else it }
        }
    }

    fun toggleCommentLike(postId: String, commentId: String) {
        _comments.update { map ->
            val list = map[postId] ?: return@update map
            val updated = list.map { c ->
                if (c.id == commentId) {
                    val liked = !c.isLiked
                    c.copy(isLiked = liked, likesCount = if (liked) c.likesCount + 1 else (c.likesCount - 1).coerceAtLeast(0))
                } else c
            }
            map + (postId to updated)
        }
    }

    // Story Actions
    fun addStory(mediaUrl: String) {
        val user = currentUser.value
        val newStory = Story(
            id = "s_${UUID.randomUUID()}",
            userId = user.id,
            username = "Your Story",
            userAvatar = user.avatarUrl,
            mediaUrl = mediaUrl.ifBlank { "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=800" },
            timestamp = "Just now",
            viewersCount = 0,
            isViewed = false
        )
        _stories.update { listOf(newStory) + it.filterNot { s -> s.userId == user.id } }
    }

    fun markStoryViewed(storyId: String) {
        _stories.update { list ->
            list.map { if (it.id == storyId) it.copy(isViewed = true) else it }
        }
    }

    // Reels Actions
    fun toggleReelLike(reelId: String) {
        _reels.update { list ->
            list.map { reel ->
                if (reel.id == reelId) {
                    val liked = !reel.isLiked
                    reel.copy(isLiked = liked, likesCount = if (liked) reel.likesCount + 1 else (reel.likesCount - 1).coerceAtLeast(0))
                } else reel
            }
        }
    }

    fun toggleReelSave(reelId: String) {
        _reels.update { list ->
            list.map { if (it.id == reelId) it.copy(isSaved = !it.isSaved) else it }
        }
    }

    // Chat Actions
    fun sendMessage(conversationId: String, text: String, mediaUrl: String? = null, isVoice: Boolean = false, voiceDur: Int = 0) {
        val newMsg = Message(
            id = "m_${UUID.randomUUID()}",
            senderId = currentUser.value.id,
            text = text,
            timestamp = nowFormat.format(Date()),
            isMe = true,
            status = MessageStatus.SENT,
            mediaUrl = mediaUrl,
            isVoiceNote = isVoice,
            voiceDurationSec = voiceDur
        )
        _messages.update { map ->
            val currentList = map[conversationId] ?: emptyList()
            map + (conversationId to (currentList + newMsg))
        }
        _conversations.update { list ->
            list.map { c ->
                if (c.id == conversationId) {
                    c.copy(
                        lastMessage = if (isVoice) "🎤 Voice message ($voiceDur s)" else text.ifBlank { "📷 Photo" },
                        lastMessageTime = "Just now"
                    )
                } else c
            }
        }
    }

    // Call Actions
    fun startCall(user: User, type: CallType) {
        _activeCall.value = ActiveCall(
            user = user,
            type = type,
            isIncoming = false,
            isConnected = true,
            durationSeconds = 0
        )
    }

    fun toggleCallMute() {
        _activeCall.update { it?.copy(isMuted = !it.isMuted) }
    }

    fun toggleCallSpeaker() {
        _activeCall.update { it?.copy(isSpeaker = !it.isSpeaker) }
    }

    fun flipCallCamera() {
        _activeCall.update { it?.copy(isVideoFrontCamera = !it.isVideoFrontCamera) }
    }

    fun endCall() {
        _activeCall.value = null
    }

    // Follow / Unfollow
    fun toggleFollowUser(userId: String) {
        _suggestedUsers.update { list ->
            list.map { u ->
                if (u.id == userId) {
                    val willFollow = !u.isFollowing
                    val count = if (willFollow) u.followersCount + 1 else (u.followersCount - 1).coerceAtLeast(0)
                    u.copy(isFollowing = willFollow, followersCount = count)
                } else u
            }
        }
        val target = _suggestedUsers.value.find { it.id == userId }
        if (target != null) {
            val delta = if (target.isFollowing) 1 else -1
            currentUser.update { current ->
                val updated = current.copy(followingCount = (current.followingCount + delta).coerceAtLeast(0))
                registeredAccounts[updated.username] = updated
                registeredAccounts[updated.id] = updated
                updated
            }
        }
    }

    // Profile updates
    fun updateProfile(fullName: String, username: String, bio: String, website: String, avatarUrl: String, coverUrl: String, isPrivate: Boolean) {
        currentUser.update { current ->
            val updated = current.copy(
                fullName = fullName,
                username = username,
                bio = bio,
                website = website,
                avatarUrl = avatarUrl.ifBlank { current.avatarUrl },
                coverUrl = coverUrl.ifBlank { current.coverUrl },
                isPrivate = isPrivate
            )
            registeredAccounts[updated.username] = updated
            registeredAccounts[updated.id] = updated
            updated
        }
    }

    fun setOnlineStatus(online: Boolean) {
        currentUser.update { it.copy(isOnline = online, lastActive = if (online) "Online" else "Away") }
    }

    // Reports / Moderation
    fun submitReport(targetType: String, targetId: String, reason: String) {
        val newRep = ReportItem(
            id = "rep_${UUID.randomUUID()}",
            targetType = targetType,
            targetId = targetId,
            reason = reason,
            reportedByUsername = currentUser.value.username,
            timestamp = "Just now",
            status = "PENDING"
        )
        _reports.update { listOf(newRep) + it }
    }

    fun resolveReport(reportId: String, actionDismiss: Boolean) {
        _reports.update { list ->
            list.map {
                if (it.id == reportId) it.copy(status = if (actionDismiss) "DISMISSED" else "RESOLVED") else it
            }
        }
    }

    // Beta Feedback
    fun submitBetaFeedback(rating: Int, category: String, comment: String, deviceModel: String, osVersion: String) {
        val user = currentUser.value
        val feedback = BetaFeedback(
            id = "fb_${UUID.randomUUID().toString().take(8)}",
            userId = user.id,
            username = user.username,
            rating = rating,
            category = category,
            comment = comment,
            deviceModel = deviceModel,
            osVersion = osVersion,
            timestamp = "Just now"
        )
        _betaFeedbacks.update { listOf(feedback) + it }
    }

    // User Blocking
    fun blockUser(userId: String) {
        _blockedUserIds.update { it + userId }
        _posts.update { it.filterNot { post -> post.userId == userId } }
        _conversations.update { it.filterNot { conv -> conv.participant.id == userId } }
    }

    fun unblockUser(userId: String) {
        _blockedUserIds.update { it - userId }
    }

    // Auth actions
    fun loginWithDemo() {
        isDemoMode.value = true
        isAuthenticated.value = true
    }

    fun loginWithCredentials(emailOrPhone: String, username: String) {
        isDemoMode.value = false
        isAuthenticated.value = true

        val key = emailOrPhone.ifBlank { username }.trim()
        val uname = username.ifBlank { key }.trim()
        val existing = registeredAccounts[key]
            ?: registeredAccounts[uname]
            ?: registeredAccounts.values.find { it.username.equals(uname, ignoreCase = true) || it.username.equals(key, ignoreCase = true) }

        if (existing != null) {
            currentUser.value = existing
        } else {
            val newUser = User(
                id = "user_${UUID.randomUUID().toString().take(8)}",
                username = uname.ifBlank { "green_member" },
                fullName = if (uname.isNotBlank()) uname.replaceFirstChar { c -> c.uppercase() } else "FG Member",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                bio = "🌿 FG TIME creator",
                followersCount = 0,
                followingCount = 0,
                postsCount = 0,
                isVerified = false,
                isAdmin = false
            )
            registeredAccounts[key] = newUser
            registeredAccounts[uname] = newUser
            registeredAccounts[newUser.id] = newUser
            currentUser.value = newUser
        }
    }

    fun deleteAccount() {
        val currentId = currentUser.value.id
        _posts.update { it.filterNot { p -> p.userId == currentId } }
        _stories.update { it.filterNot { s -> s.userId == currentId } }
        _reels.update { it.filterNot { r -> r.userId == currentId } }
        registeredAccounts.remove(currentUser.value.username)
        registeredAccounts.remove(currentId)
        isAuthenticated.value = false
    }

    fun logout() {
        // Save current profile to cache before logging out
        val user = currentUser.value
        registeredAccounts[user.username] = user
        registeredAccounts[user.id] = user
        isAuthenticated.value = false
    }
}
