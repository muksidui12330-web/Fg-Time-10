package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.model.User
import com.example.ui.components.PostCard
import com.example.ui.components.StoryTray
import com.example.ui.theme.LeafAccent
import com.example.ui.theme.LeafPrimary
import com.example.ui.theme.LeafSoftGreen

@Composable
fun HomeScreen(
    posts: List<Post>,
    stories: List<Story>,
    currentUser: User,
    onStoryClick: (Story) -> Unit,
    onAddStoryClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onLikePost: (String) -> Unit,
    onCommentPost: (Post) -> Unit,
    onSavePost: (String) -> Unit,
    onSharePost: (Post) -> Unit,
    onEditPost: (Post) -> Unit,
    onDeletePost: (String) -> Unit,
    onReportPost: (String) -> Unit,
    onCreatePostClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_feed_list"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Stories Row
        item {
            StoryTray(
                stories = stories,
                currentUserAvatar = currentUser.avatarUrl,
                onStoryClick = onStoryClick,
                onAddStoryClick = onAddStoryClick
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 0.5.dp
            )
        }

        // Welcome / Community Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = LeafPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Welcome back, ${currentUser.fullName}!",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Share today's growth with the FG TIME leaf community.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Button(
                        onClick = onCreatePostClick,
                        colors = ButtonDefaults.buttonColors(containerColor = LeafPrimary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("home_quick_post_button")
                    ) {
                        Text("Post", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Feed Posts
        if (posts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🌿 Your feed is empty",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Follow inspiring creators or publish your very first post!",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        } else {
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    isMyPost = post.userId == currentUser.id,
                    onUserClick = { onUserClick(post.userId) },
                    onLikeClick = { onLikePost(post.id) },
                    onCommentClick = { onCommentPost(post) },
                    onSaveClick = { onSavePost(post.id) },
                    onShareClick = { onSharePost(post) },
                    onEditCaption = { onEditPost(post) },
                    onDeletePost = { onDeletePost(post.id) },
                    onReportPost = { onReportPost(post.id) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
