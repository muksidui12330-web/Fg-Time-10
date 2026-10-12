package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.CallType
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.FGTimeTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkMode by viewModel.repository.isDarkMode.collectAsStateWithLifecycle()

            FGTimeTheme(darkTheme = isDarkMode) {
                FGTimeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FGTimeApp(viewModel: MainViewModel) {
    val repository = viewModel.repository

    val isAuthenticated by repository.isAuthenticated.collectAsStateWithLifecycle()
    val isDemoMode by repository.isDemoMode.collectAsStateWithLifecycle()
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()
    val isDarkMode by repository.isDarkMode.collectAsStateWithLifecycle()

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedConversation by viewModel.selectedConversation.collectAsStateWithLifecycle()
    val selectedUserProfile by viewModel.selectedUserProfile.collectAsStateWithLifecycle()
    val viewingStory by viewModel.viewingStory.collectAsStateWithLifecycle()
    val activeCommentPost by viewModel.activeCommentPost.collectAsStateWithLifecycle()
    val showEditProfile by viewModel.showEditProfileDialog.collectAsStateWithLifecycle()
    val showReportDialog by viewModel.showReportDialog.collectAsStateWithLifecycle()
    val activeCall by viewModel.activeCall.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val posts by repository.posts.collectAsStateWithLifecycle()
    val stories by repository.stories.collectAsStateWithLifecycle()
    val reels by repository.reels.collectAsStateWithLifecycle()
    val conversations by repository.conversations.collectAsStateWithLifecycle()
    val messagesMap by repository.messages.collectAsStateWithLifecycle()
    val notifications by repository.notifications.collectAsStateWithLifecycle()
    val suggestedUsers by repository.suggestedUsers.collectAsStateWithLifecycle()
    val reports by repository.reports.collectAsStateWithLifecycle()
    val commentsMap by repository.comments.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Hardware Back Button Handler
    BackHandler(enabled = true) {
        if (!viewModel.navigateBack()) {
            // Let system handle exit if at root Home
        }
    }

    if (!isAuthenticated) {
        AuthScreen(
            onLoginSuccess = { emailOrPhone, username ->
                repository.loginWithCredentials(emailOrPhone, username)
                viewModel.showToast("Welcome to FG TIME, $username!")
            },
            onDemoLogin = {
                repository.loginWithDemo()
                viewModel.showToast("Signed in with Demo Account!")
            }
        )
        return
    }

    val isMainTab = currentScreen in listOf(
        Screen.HOME, Screen.EXPLORE, Screen.REELS, Screen.MESSAGES, Screen.PROFILE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isMainTab) {
                FGTopBar(
                    isDemoMode = isDemoMode,
                    unreadNotifications = notifications.count { !it.isRead },
                    unreadMessages = conversations.sumOf { it.unreadCount },
                    isAdmin = currentUser.isAdmin,
                    onNotificationsClick = { viewModel.navigateTo(Screen.NOTIFICATIONS) },
                    onMessagesClick = { viewModel.navigateTo(Screen.MESSAGES) },
                    onAdminClick = { viewModel.navigateTo(Screen.ADMIN_DASHBOARD) }
                )
            }
        },
        bottomBar = {
            if (isMainTab) {
                FGBottomNav(
                    currentScreen = currentScreen,
                    userAvatar = currentUser.avatarUrl,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.HOME -> {
                    HomeScreen(
                        posts = posts,
                        stories = stories,
                        currentUser = currentUser,
                        onStoryClick = { viewModel.openStory(it) },
                        onAddStoryClick = {
                            repository.addStory("https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=800")
                            viewModel.showToast("Story uploaded to FG TIME! 🌿")
                        },
                        onUserClick = { userId ->
                            val targetUser = suggestedUsers.find { it.id == userId }
                            if (targetUser != null) viewModel.openUserProfile(targetUser)
                            else viewModel.navigateTo(Screen.PROFILE)
                        },
                        onLikePost = { repository.toggleLike(it) },
                        onCommentPost = { viewModel.openComments(it) },
                        onSavePost = {
                            repository.toggleSave(it)
                            viewModel.showToast("Post updated in saved collection")
                        },
                        onSharePost = {
                            viewModel.showToast("Post link copied to clipboard!")
                        },
                        onEditPost = {
                            repository.editPostCaption(it.id, "${it.caption} (Updated 🌱)")
                            viewModel.showToast("Caption updated!")
                        },
                        onDeletePost = {
                            repository.deletePost(it)
                            viewModel.showToast("Post removed from FG TIME")
                        },
                        onReportPost = {
                            viewModel.openReportDialog("POST", it)
                        },
                        onCreatePostClick = { viewModel.navigateTo(Screen.CREATE) }
                    )
                }

                Screen.EXPLORE -> {
                    ExploreScreen(
                        suggestedUsers = suggestedUsers,
                        allPosts = posts,
                        onUserClick = { viewModel.openUserProfile(it) },
                        onFollowToggle = { repository.toggleFollowUser(it) },
                        onPostClick = { viewModel.openComments(it) }
                    )
                }

                Screen.CREATE -> {
                    CreatePostScreen(
                        onPublish = { caption, imageUrl, location, tags ->
                            repository.createPost(caption, imageUrl, location, tags)
                            viewModel.showToast("Post published successfully! 🍃")
                            viewModel.navigateTo(Screen.HOME)
                        },
                        onCancel = { viewModel.navigateBack() }
                    )
                }

                Screen.REELS -> {
                    ReelsScreen(
                        reels = reels,
                        onLikeReel = { repository.toggleReelLike(it) },
                        onSaveReel = {
                            repository.toggleReelSave(it)
                            viewModel.showToast("Reel saved to bookmarks!")
                        },
                        onCommentReel = {
                            val dummyPost = posts.firstOrNull() ?: return@ReelsScreen
                            viewModel.openComments(dummyPost)
                        },
                        onUserClick = { userId ->
                            val targetUser = suggestedUsers.find { it.id == userId }
                            if (targetUser != null) viewModel.openUserProfile(targetUser)
                        }
                    )
                }

                Screen.MESSAGES -> {
                    MessagesScreen(
                        conversations = conversations,
                        suggestedUsers = suggestedUsers,
                        onSelectConversation = { viewModel.openConversation(it) },
                        onStartNewChat = { user ->
                            val existing = conversations.find { it.participant.id == user.id }
                            if (existing != null) {
                                viewModel.openConversation(existing)
                            } else {
                                val newConv = com.example.data.model.Conversation(
                                    id = "conv_${user.id}",
                                    participant = user,
                                    lastMessage = "Say hello! 🌿",
                                    lastMessageTime = "Now"
                                )
                                viewModel.openConversation(newConv)
                            }
                        }
                    )
                }

                Screen.PROFILE -> {
                    ProfileScreen(
                        user = currentUser,
                        posts = posts,
                        onEditProfileClick = { viewModel.openEditProfile() },
                        onSettingsClick = { viewModel.navigateTo(Screen.SETTINGS) },
                        onPostClick = { viewModel.openComments(it) },
                        onToggleOnline = { repository.setOnlineStatus(it) }
                    )
                }

                Screen.CHAT_DETAIL -> {
                    selectedConversation?.let { conv ->
                        val chatMessages = messagesMap[conv.id] ?: emptyList()
                        ChatDetailScreen(
                            conversation = conv,
                            messages = chatMessages,
                            onSendMessage = { text, mediaUrl, isVoice, dur ->
                                repository.sendMessage(conv.id, text, mediaUrl, isVoice, dur)
                            },
                            onStartCall = { user, type ->
                                repository.startCall(user, type)
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }

                Screen.USER_PROFILE -> {
                    selectedUserProfile?.let { user ->
                        UserProfileScreen(
                            user = user,
                            posts = posts,
                            onFollowToggle = { repository.toggleFollowUser(user.id) },
                            onMessageClick = {
                                val conv = conversations.find { it.participant.id == user.id } ?: com.example.data.model.Conversation(
                                    id = "conv_${user.id}",
                                    participant = user,
                                    lastMessage = "Hi there!",
                                    lastMessageTime = "Just now"
                                )
                                viewModel.openConversation(conv)
                            },
                            onCallClick = { type ->
                                repository.startCall(user, type)
                            },
                            onPostClick = { viewModel.openComments(it) },
                            onReportUser = { viewModel.openReportDialog("USER", user.id) },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }

                Screen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onBack = { viewModel.navigateBack() },
                        onUserClick = { userId ->
                            val target = suggestedUsers.find { it.id == userId }
                            if (target != null) viewModel.openUserProfile(target)
                        }
                    )
                }

                Screen.SETTINGS -> {
                    SettingsScreen(
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { repository.isDarkMode.value = it },
                        isPrivateAccount = currentUser.isPrivate,
                        onTogglePrivate = { isPriv ->
                            currentUser.let {
                                repository.updateProfile(it.fullName, it.username, it.bio, it.website, it.avatarUrl, it.coverUrl, isPriv)
                            }
                        },
                        onLogout = {
                            repository.logout()
                            viewModel.navigateTo(Screen.HOME)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                Screen.ADMIN_DASHBOARD -> {
                    AdminDashboardScreen(
                        reports = reports,
                        totalPostsCount = posts.size,
                        onResolveReport = { id, dismiss ->
                            repository.resolveReport(id, dismiss)
                            viewModel.showToast(if (dismiss) "Report dismissed." else "Content removed.")
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }

    // Full-screen Story Viewer Overlay
    viewingStory?.let { story ->
        StoryViewerScreen(
            story = story,
            onClose = { viewModel.closeStory() },
            onReply = { text ->
                viewModel.showToast("Reply sent: $text")
            }
        )
    }

    // Comments Bottom Sheet
    activeCommentPost?.let { post ->
        val postComments = commentsMap[post.id] ?: emptyList()
        CommentBottomSheet(
            post = post,
            comments = postComments,
            currentUserAvatar = currentUser.avatarUrl,
            onAddComment = { text ->
                repository.addComment(post.id, text)
                viewModel.showToast("Comment posted!")
            },
            onLikeComment = { commentId ->
                repository.toggleCommentLike(post.id, commentId)
            },
            onDismiss = { viewModel.closeComments() }
        )
    }

    // Active Call Overlay
    activeCall?.let { call ->
        CallScreen(
            call = call,
            onToggleMute = { repository.toggleCallMute() },
            onToggleSpeaker = { repository.toggleCallSpeaker() },
            onFlipCamera = { repository.flipCallCamera() },
            onEndCall = { repository.endCall() }
        )
    }

    // Edit Profile Dialog
    if (showEditProfile) {
        EditProfileDialog(
            user = currentUser,
            onSave = { fullName, username, bio, website, avatarUrl, coverUrl, isPrivate ->
                repository.updateProfile(fullName, username, bio, website, avatarUrl, coverUrl, isPrivate)
                viewModel.showToast("Profile updated successfully! 🌿")
            },
            onDismiss = { viewModel.closeEditProfile() }
        )
    }

    // Report Dialog
    showReportDialog?.let { (type, id) ->
        ReportContentDialog(
            targetType = type,
            targetId = id,
            onSubmitReport = { reason ->
                repository.submitReport(type, id, reason)
                viewModel.showToast("Report submitted to FG TIME moderators. Thank you!")
            },
            onDismiss = { viewModel.closeReportDialog() }
        )
    }
}
