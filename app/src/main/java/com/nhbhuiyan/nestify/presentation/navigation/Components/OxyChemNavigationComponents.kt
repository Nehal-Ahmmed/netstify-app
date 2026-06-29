package com.nhbhuiyan.nestify.presentation.navigation.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.domain.model.UserSession
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Avatar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BrandMark
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.ui.theme.Brand
import com.nhbhuiyan.nestify.ui.theme.BrandDeep
import com.nhbhuiyan.nestify.ui.theme.Elevation
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun OxyChemTopAppBar(
    session: UserSession?,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = NestifyTheme.colors
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = c.surface,
        tonalElevation = 0.dp
    ) {
        Column(Modifier.statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Space.s, end = Space.s, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButtonChrome(
                    icon = Icons.Outlined.Menu,
                    onClick = onMenuClick,
                    contentDescription = "Menu"
                )
                Spacer(modifier = Modifier.width(2.dp))

                // Unified Nestify lockup (deep-red → red gradient "N").
                BrandMark(size = 32.dp)

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nestify",
                        style = NestifyTheme.type.label.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = c.ink
                    )
                    Text(
                        text = "Organize smarter",
                        style = NestifyTheme.type.meta.copy(fontSize = 10.sp),
                        color = c.ink50
                    )
                }

                IconButtonChrome(
                    icon = Icons.Outlined.Search,
                    onClick = onSearchClick,
                    contentDescription = "Search"
                )
                // Notifications with a pure-red accent dot (the one place red pops).
                Box(contentAlignment = Alignment.Center) {
                    IconButtonChrome(
                        icon = Icons.Outlined.NotificationsNone,
                        onClick = onNotificationsClick,
                        contentDescription = "Notifications"
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 9.dp, end = 9.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(c.accent)
                            .border(1.5.dp, c.surface, CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))

                // Profile circle
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onProfileClick)
                ) {
                    Avatar(
                        name = session?.displayName ?: "Guest",
                        size = 34.dp,
                        ring = false
                    )
                }
            }
            HorizontalDivider(color = c.hair2, thickness = 1.dp)
        }
    }
}

@Composable
fun OxyChemBottomNavBar(
    selectedRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val c = NestifyTheme.colors
    
    val items = listOf(
        OxyNavItem("Home", Icons.Outlined.Home, Icons.Filled.Home, Route.Home.route),
        OxyNavItem("Academics", Icons.Outlined.School, Icons.Filled.School, Route.ExamPlanner.route),
        OxyNavItem("Network", Icons.Outlined.Hub, Icons.Filled.Hub, Route.Network.route),
        OxyNavItem("My Space", Icons.Outlined.GridView, Icons.Filled.GridView, Route.MySpace.route)
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                .height(68.dp)
                .shadow(
                    elevation = Elevation.sheet,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = c.ink.copy(alpha = 0.15f),
                    ambientColor = c.ink.copy(alpha = 0.10f),
                    clip = false
                )
                .clip(RoundedCornerShape(24.dp))
                .background(c.surface)
                .border(1.dp, c.hair2, RoundedCornerShape(24.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                val isSelected = selectedRoute == item.route
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val bgModifier = if (isSelected) {
                        Modifier
                            .fillMaxHeight()
                            .width(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(c.brandSoft)
                    } else {
                        Modifier
                    }
                    
                    Column(
                        modifier = bgModifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onItemClick(item.route) }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.iconActive else item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) c.brand else c.ink50,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            style = NestifyTheme.type.meta.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.sp
                            ),
                            color = if (isSelected) c.brand else c.ink50
                        )
                    }
                }
            }
        }
    }
}

data class OxyNavItem(
    val label: String,
    val icon: ImageVector,
    val iconActive: ImageVector,
    val route: String
)

@Composable
fun OxyChemDrawerContent(
    session: UserSession?,
    onCloseDrawer: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = NestifyTheme.colors
    
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(c.surface)
    ) {
        // Deep-red gradient header (brand-constant in light & dark so white text stays legible).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Brand, BrandDeep)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(
                        name = session?.displayName ?: "Guest",
                        size = 56.dp,
                        ring = true
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = session?.displayName ?: "Guest User",
                            style = NestifyTheme.type.h3Serif,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = session?.email ?: "guest@nestify.ac.bd",
                            style = NestifyTheme.type.meta,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // "Profile ->" button
                Button(
                    onClick = {
                        onCloseDrawer()
                        onNavigateToRoute(Route.Profile.route)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Brand
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Profile →",
                        style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    )
                }
            }
        }
        
        // Scrollable drawer items
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp)
        ) {
            DrawerItem("Courses", Icons.Outlined.School, onClick = {
                onCloseDrawer()
                onNavigateToRoute(Route.ExamPlanner.route)
            })
            DrawerItem("Study", Icons.Outlined.Book, onClick = {
                onCloseDrawer()
                onNavigateToRoute(Route.MySpace.route)
            })
            DrawerItem("Wishlist", Icons.Outlined.FavoriteBorder, onClick = {
                onCloseDrawer()
                onNavigateToRoute(Route.Favorites.route)
            })
            DrawerItem("Articles", Icons.Outlined.Description, onClick = {
                onCloseDrawer()
                onNavigateToRoute(Route.Network.route)
            })
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = c.hair2)
            
            Text(
                text = "SETTINGS",
                style = NestifyTheme.type.meta.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                color = c.ink50,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            
            DrawerItem("Settings", Icons.Outlined.Settings, onClick = {
                onCloseDrawer()
                onNavigateToRoute(Route.Settings.route)
            })
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Language,
                        contentDescription = "Language",
                        tint = c.ink70,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Language",
                        style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium),
                        color = c.ink
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(c.brandSoft)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "EN",
                        style = NestifyTheme.type.meta.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                        color = c.brand
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = c.hair2)
            
            Text(
                text = "INFORMATION",
                style = NestifyTheme.type.meta.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                color = c.ink50,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            
            DrawerItem("Testimonials", Icons.Outlined.StarBorder, onClick = {})
            DrawerItem("Privacy Policy", Icons.Outlined.Info, onClick = {})
            DrawerItem("Terms of Service", Icons.Outlined.Gavel, onClick = {})
            DrawerItem("Sign Out", Icons.Outlined.ExitToApp, onClick = {
                onCloseDrawer()
                onSignOut()
            })
        }
        
        // Footer buttons
        HorizontalDivider(color = c.hair2)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(onClick = {}) {
                Text("About Us", color = c.brand, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Bold))
            }
            TextButton(onClick = {}) {
                Text("Contact Us", color = c.brand, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val c = NestifyTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = c.ink70,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium),
            color = c.ink
        )
    }
}
