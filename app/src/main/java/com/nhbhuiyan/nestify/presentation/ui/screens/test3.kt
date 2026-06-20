package com.nhbhuiyan.nestify.presentation.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color constants matching the original design assets
private val PaperBackground = Color(0xFFF4F6ED)
private val VineOrnamentColor = Color(0xFF435B28)

@Composable
fun VintageNotesScreen(
    modifier: Modifier = Modifier
) {
    var noteContent by remember {
        mutableStateOf(
            "This is a fully native and interactive Jetpack Compose note area.\n\n" +
                    "You can click anywhere on these lines and type naturally.\n\n" +
                    "The horizontal rules adjust gracefully to match the rhythm of your handwriting layout structure."
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PaperBackground)
    ) {
        // --- Layer 1: Ornamental Vine Border ---
        ElegantFiligreeBorder(modifier = Modifier.fillMaxSize())

        // --- Layer 2: Interactive Notepad Content Layout ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 48.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant Script Header Style
            Text(
                text = "Notes:",
                fontSize = 42.sp,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                color = VineOrnamentColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // Interactive Dynamic Notebook Text Input Block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                val lineSpacingPx = with(androidx.compose.ui.platform.LocalDensity.current) { 36.sp.toPx() }

                // Native structural canvas backdrop drawing lines matching font layout rhythm
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val totalLines = (size.height / lineSpacingPx).toInt() + 5
                    for (i in 1..totalLines) {
                        val yOffset = i * lineSpacingPx
                        drawLine(
                            color = VineOrnamentColor.copy(alpha = 0.6f),
                            start = androidx.compose.ui.geometry.Offset(0f, yOffset),
                            end = androidx.compose.ui.geometry.Offset(size.width, yOffset),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }

                BasicTextField(
                    value = noteContent,
                    onValueChange = { noteContent = it },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFF2C3E50),
                        lineHeight = 36.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ElegantFiligreeBorder(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val pad = 24.dp.toPx() // Base outer framework margin padding

        val strokeStyle = Stroke(
            width = 3.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Draw left side organic dynamic curve track
        val leftVinePath = Path().apply {
            moveTo(w * 0.5f, pad)
            // Left Top symmetric corner loop mapping
            cubicTo(w * 0.25f, pad * 0.2f, pad, pad * 0.5f, pad, pad * 3f)
            // S-curve down the left waistline
            cubicTo(pad * 3f, h * 0.25f, pad * 0.2f, h * 0.35f, pad, h * 0.5f)
            cubicTo(pad * 2f, h * 0.65f, pad * 0.1f, h * 0.75f, pad, h * 0.9f)
            // Left bottom classic framework loop corner
            cubicTo(pad, h - pad * 0.5f, w * 0.25f, h - pad * 0.2f, w * 0.5f, h - pad)
        }

        // Draw right side organic dynamic curve track
        val rightVinePath = Path().apply {
            moveTo(w * 0.5f, pad)
            // Right Top symmetric corner loop mapping
            cubicTo(w * 0.75f, pad * 0.2f, w - pad, pad * 0.5f, w - pad, pad * 3f)
            // S-curve down the right waistline
            cubicTo(w - pad * 3f, h * 0.25f, w - pad * 0.2f, h * 0.35f, w - pad, h * 0.5f)
            cubicTo(w - pad * 2f, h * 0.65f, w - pad * 0.1f, h * 0.75f, w - pad, h * 0.9f)
            // Right bottom classic framework loop corner
            cubicTo(w - pad, h - pad * 0.5f, w * 0.75f, h - pad * 0.2f, w * 0.5f, h - pad)
        }

        // Draw Interlocking Secondary Vines for Layered Depth Effect
        // Draw Interlocking Secondary Vines for Layered Depth Effect
        val innerLeftDecoration = Path().apply {
            moveTo(pad, pad * 2f)
            quadraticTo(pad * 3.5f, h * 0.25f, pad * 1.5f, h * 0.5f)
            quadraticTo(pad * 0.2f, h * 0.75f, pad * 2.5f, h * 0.92f)
        }

        val innerRightDecoration = Path().apply {
            moveTo(w - pad, pad * 2f)
            quadraticTo(w - pad * 3.5f, h * 0.25f, w - pad * 1.5f, h * 0.5f)
            quadraticTo(w - pad * 0.2f, h * 0.75f, w - pad * 2.5f, h * 0.92f)
        }

        // Commit all algorithmic stroke paths neatly to the engine layout canvas
        drawPath(leftVinePath, color = VineOrnamentColor, style = strokeStyle)
        drawPath(rightVinePath, color = VineOrnamentColor, style = strokeStyle)
        drawPath(innerLeftDecoration, color = VineOrnamentColor, style = strokeStyle)
        drawPath(innerRightDecoration, color = VineOrnamentColor, style = strokeStyle)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 800)
@Composable
private fun VintageNotesPreview() {
    VintageNotesScreen()
}