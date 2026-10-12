package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.LeafPrimary
import com.example.ui.theme.LeafPrimaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPublish: (caption: String, imageUrl: String, location: String, tags: List<String>) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val presetImages = listOf(
        "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?w=800",
        "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=800",
        "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800",
        "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
        "https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?w=800"
    )

    var selectedImage by remember { mutableStateOf(presetImages[0]) }
    var customImageUrl by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var activeTags by remember { mutableStateOf(mutableListOf("FGTime", "GreenLeaf")) }

    val suggestedTags = listOf("FGTime", "GreenLeaf", "EcoLife", "Growth", "Mindful", "NatureVibes")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            val finalImg = if (customImageUrl.isNotBlank()) customImageUrl else selectedImage
                            onPublish(caption, finalImg, location, activeTags)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LeafPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("publish_post_button")
                    ) {
                        Text("Share", fontWeight = FontWeight.Bold)
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
            // Selected Image Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = if (customImageUrl.isNotBlank()) customImageUrl else selectedImage,
                    contentDescription = "Selected media",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Preset Media Picker
            Column {
                Text(
                    text = "Select from Gallery Presets:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(presetImages) { url ->
                        val isPicked = (url == selectedImage && customImageUrl.isBlank())
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isPicked) 3.dp else 1.dp,
                                    color = if (isPicked) LeafPrimary else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedImage = url
                                    customImageUrl = ""
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

            // Custom Image URL input
            OutlinedTextField(
                value = customImageUrl,
                onValueChange = { customImageUrl = it },
                label = { Text("Or Paste Custom Image URL") },
                placeholder = { Text("https://example.com/photo.jpg") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = LeafPrimary)
                },
                singleLine = true
            )

            // Caption Field
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Write a caption...") },
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
                placeholder = { Text("e.g. Costa Rica Rain Forest") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = LeafPrimary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Tag Pills
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
