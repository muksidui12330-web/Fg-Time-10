package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.FGTimeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
    HOME, EXPLORE, CREATE, REELS, MESSAGES, PROFILE,
    CHAT_DETAIL, NOTIFICATIONS, SETTINGS, ADMIN_DASHBOARD, USER_PROFILE
}

class MainViewModel(
    val repository: FGTimeRepository = FGTimeRepository()
) : ViewModel() {

    // Navigation state
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>()

    // Selected items for sub-screens
    private val _selectedConversation = MutableStateFlow<Conversation?>(null)
    val selectedConversation: StateFlow<Conversation?> = _selectedConversation.asStateFlow()

    private val _selectedUserProfile = MutableStateFlow<User?>(null)
    val selectedUserProfile: StateFlow<User?> = _selectedUserProfile.asStateFlow()

    private val _viewingStory = MutableStateFlow<Story?>(null)
    val viewingStory: StateFlow<Story?> = _viewingStory.asStateFlow()

    // Dialog & Sheet States
    private val _activeCommentPost = MutableStateFlow<Post?>(null)
    val activeCommentPost: StateFlow<Post?> = _activeCommentPost.asStateFlow()

    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog: StateFlow<Boolean> = _showEditProfileDialog.asStateFlow()

    private val _showReportDialog = MutableStateFlow<Pair<String, String>?>(null) // (type, id)
    val showReportDialog: StateFlow<Pair<String, String>?> = _showReportDialog.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Search query for Explore
    val searchQuery = MutableStateFlow("")
    val searchFilter = MutableStateFlow("ALL") // "ALL", "ACCOUNTS", "TAGS"

    // Call duration timer
    val activeCall = repository.activeCall

    init {
        // Active call duration counter
        viewModelScope.launch {
            while (true) {
                delay(1000)
                if (repository.activeCall.value != null && repository.activeCall.value?.isConnected == true) {
                    // repo could update duration if needed
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (viewingStory.value != null) {
            _viewingStory.value = null
            return true
        }
        if (activeCommentPost.value != null) {
            _activeCommentPost.value = null
            return true
        }
        if (repository.activeCall.value != null) {
            repository.endCall()
            return true
        }
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            return true
        }
        if (_currentScreen.value != Screen.HOME) {
            _currentScreen.value = Screen.HOME
            return true
        }
        return false
    }

    fun openConversation(conversation: Conversation) {
        _selectedConversation.value = conversation
        navigateTo(Screen.CHAT_DETAIL)
    }

    fun openUserProfile(user: User) {
        _selectedUserProfile.value = user
        navigateTo(Screen.USER_PROFILE)
    }

    fun openStory(story: Story) {
        _viewingStory.value = story
        repository.markStoryViewed(story.id)
    }

    fun closeStory() {
        _viewingStory.value = null
    }

    fun openComments(post: Post) {
        _activeCommentPost.value = post
    }

    fun closeComments() {
        _activeCommentPost.value = null
    }

    fun openEditProfile() {
        _showEditProfileDialog.value = true
    }

    fun closeEditProfile() {
        _showEditProfileDialog.value = false
    }

    fun openReportDialog(targetType: String, targetId: String) {
        _showReportDialog.value = Pair(targetType, targetId)
    }

    fun closeReportDialog() {
        _showReportDialog.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
        viewModelScope.launch {
            delay(2500)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }
}
