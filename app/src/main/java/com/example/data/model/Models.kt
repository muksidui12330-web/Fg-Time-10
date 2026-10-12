package com.example.data.model

data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val avatarUrl: String,
    val coverUrl: String = "",
    val bio: String = "",
    val website: String = "",
    val isVerified: Boolean = false,
    val isPrivate: Boolean = false,
    val isOnline: Boolean = true,
    val lastActive: String = "Just now",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val isFollowing: Boolean = false,
    val isAdmin: Boolean = false
)

data class Post(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val isVerified: Boolean = false,
    val caption: String,
    val imageUrl: String,
    val videoUrl: String? = null,
    val isVideo: Boolean = false,
    val location: String = "",
    val timestamp: String,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val tags: List<String> = emptyList()
)

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val text: String,
    val timestamp: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

data class Story(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val mediaUrl: String,
    val timestamp: String,
    val viewersCount: Int = 0,
    val isViewed: Boolean = false
)

data class Reel(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val caption: String,
    val audioTrack: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val videoGradientColors: List<Long>
)

data class Message(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isMe: Boolean,
    val status: MessageStatus = MessageStatus.READ,
    val mediaUrl: String? = null,
    val isVoiceNote: Boolean = false,
    val voiceDurationSec: Int = 0
)

enum class MessageStatus {
    SENT, DELIVERED, READ
}

data class Conversation(
    val id: String,
    val participant: User,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isGroup: Boolean = false,
    val groupName: String? = null
)

data class NotificationItem(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val text: String,
    val timestamp: String,
    val type: NotificationType,
    val postPreviewUrl: String? = null,
    val isRead: Boolean = false
)

enum class NotificationType {
    LIKE, COMMENT, FOLLOW, MENTION, SYSTEM
}

data class ReportItem(
    val id: String,
    val targetType: String, // "POST" or "USER"
    val targetId: String,
    val reason: String,
    val reportedByUsername: String,
    val timestamp: String,
    val status: String = "PENDING" // "PENDING", "RESOLVED", "DISMISSED"
)

enum class CallType {
    AUDIO, VIDEO
}

data class ActiveCall(
    val user: User,
    val type: CallType,
    val isIncoming: Boolean,
    val isConnected: Boolean = false,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeaker: Boolean = false,
    val isVideoFrontCamera: Boolean = true
)

data class BetaFeedback(
    val id: String,
    val userId: String,
    val username: String,
    val rating: Int,
    val category: String,
    val comment: String,
    val deviceModel: String,
    val osVersion: String,
    val timestamp: String
)
