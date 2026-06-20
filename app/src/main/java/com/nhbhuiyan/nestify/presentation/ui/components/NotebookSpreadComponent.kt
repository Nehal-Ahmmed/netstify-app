package com.nhbhuiyan.nestify.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NotebookSpreadComponent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val paperColorTop = Color(0xFFFFFDF5) 
    val paperColorBottom = Color(0xFFFDFBF7)
    val marginColor = Color(0x88FF7E7E) 
    val lineColor = Color(0x55A0A0A0) 
    val holeColor = Color(0xFFE5E0D8)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0x33000000))
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(paperColorTop, paperColorBottom)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val lineSpacing = 45.dp.toPx()
            val marginPosition = 70.dp.toPx()

            // Draw horizontal lines
            var currentY = lineSpacing * 1.5f
            while (currentY < height) {
                drawLine(
                    color = lineColor,
                    start = Offset(0f, currentY),
                    end = Offset(width, currentY),
                    strokeWidth = 2f
                )
                currentY += lineSpacing
            }


        }

        // Content goes over the canvas
        Box(modifier = Modifier.fillMaxSize().padding(
            start = 16.dp,
            top = 20.dp,
            end = 16.dp,
            bottom = 16.dp
        )) {
            content()
        }
    }
}
