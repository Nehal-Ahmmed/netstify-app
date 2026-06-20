package com.nhbhuiyan.nestify.presentation.navigation.Components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.ui.theme.*

@Composable
fun bottomNavigation(
    items: List<BottomNavItem>,
    selectedItem: Int,
    onItemClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(97.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Navigation Background with softer shadow and more premium rounding
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shadow(
                    elevation = 25.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    ambientColor = NestifySlate.copy(alpha = 0.5f),
                    spotColor = NestifySlate.copy(alpha = 0.5f)
                ),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    if (index != 2) {
                        NavBarItem(
                            item = item,
                            isSelected = selectedItem == index,
                            onClick = { onItemClick(index) }
                        )
                    } else {
                        // Wide spacer for the middle uplifted button
                        Spacer(modifier = Modifier.width(72.dp))
                    }
                }
            }
        }

        // Uplifted Middle Button
        if (items.size > 2) {
            val middleItem = items[2]
            UpliftedMiddleButton(
                item = middleItem,
                isSelected = selectedItem == 2,
                onClick = { onItemClick(2) }
            )
        }
    }
}

@Composable
fun NavBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFF4A6572) // Accurate Brand Primary
    val inactiveColor = NestifySlate.copy(alpha = 0.85f) // Increased Opacity
    
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        label = "IconColor"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        label = "Scale"
    )

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isSelected) activeColor.copy(alpha = 0.1f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale),
                tint = iconColor
            )
        }
        
        Spacer(Modifier.height(2.dp))
        
        Text(
            text = item.text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = iconColor
        )
        
        // Active Line Indicator
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .width(if (isSelected) 12.dp else 0.dp)
                .height(3.dp)
                .background(activeColor, RoundedCornerShape(2.dp))
        )
    }
}

@Composable
fun UpliftedMiddleButton(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFF4A6572)
    
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 8.dp,
        label = "Elevation"
    )

    Box(
        modifier = Modifier
            .padding(bottom = 25.dp) // More dramatic uplift
            .size(68.dp)
            .shadow(
                elevation = elevation, 
                shape = CircleShape, 
                spotColor = activeColor.copy(alpha = 0.6f)
            )
            .border(2.dp, Color.White, CircleShape)
            .clip(CircleShape)
            .background(
                brush = if (isSelected) NestifyGradients.meshGradient() else Brush.linearGradient(listOf(activeColor, activeColor)),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = Color.White
        )
    }
}

data class BottomNavItem(
    val text: String,
    val icon: ImageVector
)
