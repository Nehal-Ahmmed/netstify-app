package com.nhbhuiyan.nestify.presentation.ui.screens.RoutineScreen

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.domain.model.ClassRoutine
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(
    routine: ClassRoutine,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var zoomingState by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = routine.imageDescription.ifEmpty { "Class Routine Details" },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
        ,
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = {
                    zoomingState = !zoomingState
                }
            ) {
                Icon(
                    painter =
                    if(zoomingState) painterResource(R.drawable.baseline_zoom_out_24)
                    else  painterResource(R.drawable.baseline_zoom_in_24),
                    contentDescription = "Back"
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                   // .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (zoomingState.equals(true)) {
                    ZoomableImage(routine)
                }else{
                    RoutineDetailImage(routine)
                }
//                RoutineContentCard(routine)
//                ImagePathInfoCard(routine) // Keep for debugging
            }
        }
    }
}

@Composable
private fun RoutineImageCard(routine: ClassRoutine) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            RoutineDetailImage(routine)
            Text(
                text = routine.imageDescription.ifEmpty { "Class Routine Image" },
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

//without zooming function
@Composable
private fun RoutineDetailImage(routine: ClassRoutine) {
    val imageFile = File(routine.imageUri)
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(if (imageFile.exists()) imageFile else null) // ✅ Check if file exists
            .crossfade(true)
            .build()
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(0.dp))
    ) {
        // Show image when loaded
            Image(
                painter = painter,
                contentDescription = routine.imageDescription.ifEmpty { "Class Routine Image" },
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.padding(32.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔄 Loading...")
                        }
                    }
                }
            }
            is AsyncImagePainter.State.Error -> {
                val error = (painter.state as AsyncImagePainter.State.Error).result.throwable
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                        modifier = Modifier.padding(32.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("❌ Load Failed")
                            Text(
                                text = "Error: ${error.message}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            is AsyncImagePainter.State.Success -> {
                println("✅ Image loaded successfully!")
            }
            else -> {}
        }
    }
}

//with zooming function
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoomableImage(
    routine: ClassRoutine
) {
    val imageFile = File(routine.imageUri)
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(if (imageFile.exists()) imageFile else null) // ✅ Check if file exists
            .crossfade(true)
            .build()
    )
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotation by remember { mutableStateOf(0f) }

    var showControls by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { showControls = !showControls },
                    onDoubleTap = {
                        // Reset on double tap
                        scale = 1f
                        offset = Offset.Zero
                        rotation = 0f
                    }
                )
            }
    ) {
        // Zoomable Image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    rotationZ = rotation
                )
                .transformable(
                    state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                        scale *= zoomChange
                        rotation += rotationChange
                        offset += offsetChange
                    }
                )
        ) {
            Image(
                painter = painter,
                contentDescription = "Zoomable Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            )
        }

        // Zoom indicator
        Text(
            text = "Zoom: ${"%.1f".format(scale)}x",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(8.dp)
        )
    }
}


/**
 * Determine the correct image data type based on the routine's image field
 */
private fun getImageData(routine: ClassRoutine): Any {
    val imageValue = routine.imageUri // Or routine.imagePath depending on your model

    println("🔍 getImageData analysis:")
    println("Raw image value: $imageValue")

    return when {
        // Case 1: It's a file path (starts with /data/)
        imageValue.startsWith("/data/") -> {
            println("📁 Detected file path")
            File(imageValue)
        }
        // Case 2: It's a content URI (starts with content://)
        imageValue.startsWith("content://") -> {
            println("🌐 Detected content URI")
            android.net.Uri.parse(imageValue)
        }
        // Case 3: It's a file URI (starts with file://)
        imageValue.startsWith("file://") -> {
            println("📄 Detected file URI")
            android.net.Uri.parse(imageValue)
        }
        // Case 4: Unknown - try File first, then fallback to URI
        else -> {
            println("❓ Unknown type, trying File first")
            val file = File(imageValue)
            if (file.exists()) {
                println("✅ File exists, using File object")
                file
            } else {
                println("❌ File doesn't exist, trying as URI")
                android.net.Uri.parse(imageValue)
            }
        }
    }
}

@Composable
private fun RoutineContentCard(routine: ClassRoutine) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Routine Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = routine.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )
        }
    }
}

@Composable
private fun ImagePathInfoCard(routine: ClassRoutine) {
    val imageValue = routine.imageUri // Or routine.imagePath
    val imageFile = File(imageValue)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🔍 Debug Info",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Value: $imageValue",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Type: ${if (imageValue.startsWith("/data/")) "File Path" else if (imageValue.startsWith("content://")) "Content URI" else "Unknown"}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "File exists: ${imageFile.exists()}",
                style = MaterialTheme.typography.bodySmall,
                color = if (imageFile.exists()) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )

            if (imageFile.exists()) {
                Text(
                    text = "File size: ${imageFile.length()} bytes",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}