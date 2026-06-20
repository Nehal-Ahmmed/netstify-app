package com.nhbhuiyan.nestify.presentation.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color Palette Constants
private val WoodDark = Color(0xFF5C2E16)
private val WoodLight = Color(0xFF8B4513)
private val RopeColor = Color(0xFFE1B382)
private val BoardBg = Color(0xFFFFFFFF)
private val ScrewColor = Color(0xFFFFFFFF)
private val TextColor = Color(0xFF2C3E50)

@Composable
fun KidsNoticeBoard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // --- Layer 1: The Wooden Board Structure ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 90.dp) // Leave space for top characters overlapping
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f) // Matches the image ratio aspect
                    .shadow(8.dp, shape = RoundedCornerShape(4.dp))
                    .background(WoodDark)
                    .padding(14.dp) // Thickness of the wooden outer frame frame
            ) {
                // White Writing/Interaction Surface
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BoardBg, shape = RoundedCornerShape(2.dp))
                        .padding(24.dp)
                ) {
                    content()
                }

                // Inner Corner Fasteners (Metallic Screws)
                CornerScrews()
            }
        }

        // --- Layer 2: Top Overlapping Cartoon Elements ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            SkaterGirlPlaceholder(modifier = Modifier.weight(1f))
            CenterGirlPlaceholder(modifier = Modifier.weight(1f))
            DogGirlPlaceholder(modifier = Modifier.weight(1.2f))
        }

        // --- Layer 3: Accent Rope Ties on Corners ---
        CornerRopeTies()
    }
}

@Composable
private fun CornerScrews() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val radius = 6.dp.toPx()
        val offsetFromEdge = 6.dp.toPx()

        val positions = listOf(
            Offset(offsetFromEdge, offsetFromEdge), // Top Left
            Offset(size.width - offsetFromEdge, offsetFromEdge), // Top Right
            Offset(offsetFromEdge, size.height - offsetFromEdge), // Bottom Left
            Offset(size.width - offsetFromEdge, size.height - offsetFromEdge) // Bottom Right
        )

        positions.forEach { center ->
            // Screw Body
            drawCircle(color = ScrewColor, radius = radius, center = center)
            // Screw Center Groove Detail
            drawLine(
                color = Color.Gray,
                start = Offset(center.x - radius/2, center.y - radius/2),
                end = Offset(center.x + radius/2, center.y + radius/2),
                strokeWidth = 2f
            )
        }
    }
}

@Composable
private fun CornerRopeTies() {
    // Overlays custom crossed ropes matching the corners
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 90.dp)
            .aspectRatio(1.5f)
    ) {
        val ropeWidth = 6.dp.toPx()
        val corners = listOf(
            Offset(0f, 0f),
            Offset(size.width, 0f),
            Offset(0f, size.height),
            Offset(size.width, size.height)
        )

        corners.forEach { corner ->
            // Diagonal 1
            drawLine(
                color = RopeColor,
                start = Offset(corner.x - 20f, corner.y - 20f),
                end = Offset(corner.x + 20f, corner.y + 20f),
                strokeWidth = ropeWidth
            )
            // Diagonal 2
            drawLine(
                color = RopeColor,
                start = Offset(corner.x - 20f, corner.y + 20f),
                end = Offset(corner.x + 20f, corner.y - 20f),
                strokeWidth = ropeWidth
            )
        }
    }
}

// --- Vector/Canvas Stylized Graphic Placeholders ---

@Composable
private fun SkaterGirlPlaceholder(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.height(110.dp)) {
        // Skateboard base
        drawRoundRect(
            color = Color(0xFFE91E63),
            size = Size(size.width * 0.8f, 16.dp.toPx()),
            topLeft = Offset(0f, size.height - 25.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
        )
        // Wheels
        drawCircle(color = Color(0xFF2196F3), radius = 8f, center = Offset(20f, size.height - 9.dp.toPx()))
        drawCircle(color = Color(0xFF2196F3), radius = 8f, center = Offset(size.width * 0.6f, size.height - 9.dp.toPx()))

        // Character Silhouette/Basic Form structure representation
        val bodyPath = Path().apply {
            moveTo(size.width * 0.3f, size.height)
            lineTo(size.width * 0.6f, size.height - 60.dp.toPx())
            lineTo(size.width * 0.2f, size.height - 40.dp.toPx())
            close()
        }
        drawPath(bodyPath, color = Color(0xFF9C27B0)) // Shirt representation
        drawCircle(color = Color(0xFFFFCC80), radius = 22.dp.toPx(), center = Offset(size.width * 0.45f, size.height - 70.dp.toPx())) // Face
        drawCircle(color = Color(0xFF0D47A1), radius = 24.dp.toPx(), center = Offset(size.width * 0.42f, size.height - 82.dp.toPx())) // Cap
    }
}

@Composable
private fun CenterGirlPlaceholder(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.height(130.dp)) {
        // Pink Dress Profile
        val dressPath = Path().apply {
            moveTo(size.width * 0.2f, size.height)
            lineTo(size.width * 0.3f, size.height - 70.dp.toPx())
            lineTo(size.width * 0.7f, size.height - 70.dp.toPx())
            lineTo(size.width * 0.8f, size.height)
            close()
        }
        drawPath(dressPath, color = Color(0xFFFF8A80))
        // Head and Orange Hair
        drawCircle(color = Color(0xFFFFB74D), radius = 32.dp.toPx(), center = Offset(size.width * 0.5f, size.height - 95.dp.toPx()))
        drawCircle(color = Color(0xFFFFCC80), radius = 24.dp.toPx(), center = Offset(size.width * 0.5f, size.height - 90.dp.toPx()))
    }
}

@Composable
private fun DogGirlPlaceholder(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.height(120.dp)) {
        // Dog Shape (Brown)
        drawCircle(color = Color(0xFF8D6E63), radius = 25.dp.toPx(), center = Offset(size.width * 0.3f, size.height - 35.dp.toPx()))
        drawCircle(color = Color(0xFF5D4037), radius = 18.dp.toPx(), center = Offset(size.width * 0.25f, size.height - 65.dp.toPx()))

        // Girl profile (Purple & Yellow Hair)
        val shirtPath = Path().apply {
            moveTo(size.width * 0.5f, size.height)
            lineTo(size.width * 0.6f, size.height - 50.dp.toPx())
            lineTo(size.width * 0.9f, size.height - 50.dp.toPx())
            lineTo(size.width * 0.95f, size.height)
            close()
        }
        drawPath(shirtPath, color = Color(0xFF673AB7))
        drawCircle(color = Color(0xFFFFEE58), radius = 24.dp.toPx(), center = Offset(size.width * 0.75f, size.height - 75.dp.toPx()))
        drawCircle(color = Color(0xFFFFCC80), radius = 20.dp.toPx(), center = Offset(size.width * 0.72f, size.height - 70.dp.toPx()))

        // Glasses outline red
        drawCircle(color = Color.Red, radius = 10.dp.toPx(), center = Offset(size.width * 0.66f, size.height - 72.dp.toPx()), style = Stroke(width = 3f))
    }
}

// --- Preview and Sandbox Content Execution ---

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun KidsNoticeBoardPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEF55)), // Playground Background color style
        contentAlignment = Alignment.Center
    ) {
        KidsNoticeBoard {
            // Native editable content structure built cleanly into the center frame
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to Class!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Today's activities: Painting, Storytime, and Outdoor Playground fun!",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }
    }
}