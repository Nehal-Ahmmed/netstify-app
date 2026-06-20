package com.nhbhuiyan.nestify.presentation.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StickyNoteCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {

        Card(
            modifier = Modifier
                .width(320.dp)
                .height(420.dp)
                .padding(top = 20.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8E97A)
            )
        ) {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                FoldedCorner()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 40.dp,
                            bottom = 30.dp
                        )
                ) {

                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = content,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = Color(0xFF444444)
                    )
                }
            }
        }

        RedPin()
    }
}

@Composable
private fun FoldedCorner() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {

        Canvas(
            modifier = Modifier.size(90.dp)
        ) {

            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(size.width, size.height)
                lineTo(0f, 0f)
                close()
            }

            drawPath(
                path = path,
                color = Color(0xFFE6C600),
                style = Fill
            )
        }
    }
}

@Composable
private fun RedPin() {

    Box(
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = Color.Red,
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(22.dp)
                .background(
                    color = Color(0xFFFF6666),
                    shape = CircleShape
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StickyNotePreview() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE7E7E7)),
        contentAlignment = Alignment.Center
    ) {

        StickyNoteCard(
            title = "Project Ideas",
            content =
                """
                • Build Nestify
                
                • Add Notes Module
                
                • Add Link Manager
                
                • Implement Reminder System
                
                • Publish on Play Store
                """.trimIndent()
        )
    }
}