package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.GlassNavSpace
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun LinkGroupDetailScreen(navController: NavController, groupId: String) {
    val context = LocalContext.current
    val c = NestifyTheme.colors
    val item = mockLinkItems.find { it.id == groupId } ?: mockLinkItems.first()

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(
            title = "Group Details",
            subtitle = null,
            onBack = { navController.popBackStack() },
            trailing = {
                IconButtonChrome(
                    Icons.Outlined.Edit,
                    onClick = { /* Toggle Edit Mode */ },
                    contentDescription = "Edit",
                )
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Space.screen,
                end = Space.screen,
                top = Space.l,
                bottom = GlassNavSpace,
            ),
            verticalArrangement = Arrangement.spacedBy(Space.m),
        ) {
            item {
                Column {
                    Text(item.title, style = NestifyTheme.type.h1Serif, color = c.ink)
                    Spacer(Modifier.height(Space.s))
                    Text(item.description, style = NestifyTheme.type.body, color = c.ink50)
                }
            }

            item {
                Spacer(Modifier.height(Space.s))
                SectionHead(title = "Associated Links", kicker = "${item.links.size} links")
            }

            items(item.links) { subLink ->
                SubLinkCard(subLink = subLink) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(subLink.url))
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun SubLinkCard(subLink: SubLink, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.m, onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.m),
        ) {
            IconTile(icon = Icons.Outlined.Link, size = 40.dp)
            Column(Modifier.weight(1f)) {
                OneLine(
                    text = subLink.name,
                    style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
                    color = c.ink,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    subLink.url,
                    style = NestifyTheme.type.meta,
                    color = c.brand,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButtonChrome(
                Icons.Outlined.OpenInNew,
                onClick = onClick,
                tint = c.ink50,
                contentDescription = "Open",
            )
        }
    }
}
