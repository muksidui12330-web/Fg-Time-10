package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.FGVideoPlayer
import com.example.ui.theme.LeafAccent
import com.example.ui.theme.LeafPrimary
import com.example.ui.theme.LeafPrimaryDark
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPublish: (caption: String, imageUrl: String, videoUrl: String?, isVideo: Boolean, location: String, tags: List<String>) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val presetImages = listOf(
        "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?w=800",
        "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=800",
        "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800",
        "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800"
    )

    // Media states
    var isVideoPost by remember { mutableStateOf(false) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImage by remember { mutableStateOf(presetImages[0]) }
    var customImageUrl by remember { mutableStateOf("") }
    var videoFileSizeMb by remember { mutableDoubleStateOf(0.0) }

    // Upload & validation states
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }
    var uploadStatusMessage by remember { mutableStateOf("") }
    var uploadJob by remember { mutableStateOf<Job?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Text & tags
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var activeTags by remember { mutableStateOf(mutableListOf("FGTime", "GreenLeaf")) }

    val suggestedTags = listOf("FGTime", "GreenLeaf", "EcoLife", "VideoVibes", "Nature", "DailyGrowth")

    // Video Picker Launcher (Android Native zero-permission Photo/Video Picker)
    val pickVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            errorMessage = null
            // Check file size
            try {
                context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                    val sizeBytes = pfd.statSize
                    val sizeMb = sizeBytes / (1024.0 * 1024.0)
                    videoFileSizeMb = (sizeMb * 10).toInt() / 10.0

                    if (sizeMb > 100.0) {
                        errorMessage = "Video size (${videoFileSizeMb}MB) exceeds the 100MB limit. Please choose a smaller clip."
                        selectedVideoUri = null
                        isVideoPost = false
                        return@use
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            selectedVideoUri = uri
            isVideoPost = true
            selectedPhotoUri = null
        }
    }

    // Photo Picker Launcher
    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
            isVideoPost = false
            selectedVideoUri = null
            customImageUrl = ""
            errorMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isVideoPost) "New Video Post" else "New Post", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (isVideoPost && selectedVideoUri != null) {
                                // Real/Simulated Cloud Upload flow with progress
                                isUploading = true
                                uploadProgress = 0f
                                uploadStatusMessage = "Connecting to storage bucket..."
                                uploadJob = coroutineScope.launch {
                                    try {
                                        delay(400)
                                        uploadProgress = 0.25f
                                        uploadStatusMessage = "Uploading video (${videoFileSizeMb}MB)..."
                                        delay(600)
                                        uploadProgress = 0.65f
                                        uploadStatusMessage = "Generating streamable video metadata..."
                                        delay(500)
                                        uploadProgress = 0.95f
                                        uploadStatusMessage = "Finalizing video post..."
                                        delay(300)
                                        uploadProgress = 1.0f
                                        isUploading = false

                                        val thumbnail = "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?w=800"
                                        onPublish(caption, thumbnail, selectedVideoUri.toString(), true, location, activeTags)
                                    } catch (e: Exception) {
                                        isUploading = false
                                        errorMessage = "Upload failed: ${e.message}"
                                    }
                                }
                            } else {
                                val finalImg = when {
                                    selectedPhotoUri != null -> selectedPhotoUri.toString()
                                    customImageUrl.isNotBlank() -> customImageUrl
                                    else -> selectedImage
                                }
                                onPublish(caption, finalImg, null, false, location, activeTags)
                            }
                        },
                        enabled = !isUploading,
                        colors = ButtonDefaults.buttonColors(containerColor = LeafPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("publish_post_button")
                    ) {
                        Text(if (isUploading) "Uploading..." else "Share", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Upload Progress Banner (active during upload)
            if (isUploading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Uploading Video", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${(uploadProgress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = LeafPrimary)
                        }
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = LeafPrimary,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(uploadStatusMessage, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            TextButton(
                                onClick = {
                                    uploadJob?.cancel()
                                    isUploading = false
                                    errorMessage = "Upload cancelled by user."
                                }
                            ) {
                                Text("Cancel", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Media Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (isVideoPost && selectedVideoUri != null) {
                    FGVideoPlayer(
                        videoUriString = selectedVideoUri.toString(),
                        modifier = Modifier.fillMaxSize(),
                        autoPlay = false
                    )
                } else {
                    val displayImage = when {
                        selectedPhotoUri != null -> selectedPhotoUri.toString()
                        customImageUrl.isNotBlank() -> customImageUrl
                        else -> selectedImage
                    }
                    AsyncImage(
                        model = displayImage,
                        contentDescription = "Selected media",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Media Selection Buttons: Gallery Video vs Gallery Photo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        pickVideoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVideoPost) LeafPrimary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pick_video_from_gallery")
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isVideoPost) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pick Video",
                        color = if (isVideoPost) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = {
                        pickPhotoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isVideoPost && selectedPhotoUri != null) LeafPrimary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pick_photo_from_gallery")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (!isVideoPost && selectedPhotoUri != null) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pick Photo",
                        color = if (!isVideoPost && selectedPhotoUri != null) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Preset Gallery fallback choices
            if (!isVideoPost && selectedPhotoUri == null) {
                Column {
                    Text(
                        text = "Or choose sample nature photos:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(presetImages) { url ->
                            val isPicked = (url == selectedImage && customImageUrl.isBlank() && selectedPhotoUri == null)
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = if (isPicked) 3.dp else 1.dp,
                                        color = if (isPicked) LeafPrimary else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedImage = url
                                        customImageUrl = ""
                                        selectedPhotoUri = null
                                        isVideoPost = false
                                        selectedVideoUri = null
                                    }
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Preset photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Caption Field
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text(if (isVideoPost) "Video Caption & Description" else "Write a caption...") },
                placeholder = { Text("Share the story behind this moment...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("create_caption_input"),
                shape = RoundedCornerShape(14.dp),
                maxLines = 5
            )

            // Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Add Location") },
                placeholder = { Text("e.g. Kyoto Botanical Garden") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = LeafPrimary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Hashtags
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tag, contentDescription = null, tint = LeafPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Suggested Hashtags:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestedTags.take(4).forEach { tag ->
                        val isAdded = activeTags.contains(tag)
                        FilterChip(
                            selected = isAdded,
                            onClick = {
                                if (isAdded) activeTags.remove(tag) else activeTags.add(tag)
                            },
                            label = { Text("#$tag", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LeafPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}
