package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.GlassNavSpace
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun CategorySpreadSheetScreen(navController: NavController, categoryId: String) {
    val c = NestifyTheme.colors
    val category = mockLinkCategories.find { it.id == categoryId } ?: mockLinkCategories.first()

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(
            title = category.name,
            subtitle = "${mockLinkItems.size} entries",
            onBack = { navController.popBackStack() },
            trailing = {
                IconButtonChrome(
                    Icons.Default.Add,
                    onClick = { /* Add New Item */ },
                    tint = c.brand,
                    contentDescription = "Add Item",
                )
            },
        )

        // Sheet container — a single bordered surface with hairline rows (table-like).
        Box(
            Modifier
                .fillMaxSize()
                .padding(horizontal = Space.screen, vertical = Space.l)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .clip(Radii.l)
                    .background(c.surface)
                    .border(1.dp, c.hair2, Radii.l)
            ) {
                // Column header row
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Space.l, vertical = Space.m),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Kicker("Entry", modifier = Modifier.weight(1f))
                    Kicker("Open", color = c.ink30)
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(c.hair2))

                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = GlassNavSpace),
                ) {
                    itemsIndexed(mockLinkItems) { index, item ->
                        SpreadSheetRow(item = item) {
                            navController.navigate(Route.LinkGroupDetail.createRoute(item.id))
                        }
                        if (index != mockLinkItems.lastIndex) {
                            Box(Modifier.fillMaxWidth().height(1.dp).background(c.hair))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpreadSheetRow(item: LinkItem, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = Space.l, vertical = Space.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Space.m),
    ) {
        IconTile(icon = Icons.Outlined.Link, size = 40.dp)
        Column(Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = NestifyTheme.type.label.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                color = c.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.description,
                style = NestifyTheme.type.meta,
                color = c.ink50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = c.ink30,
            modifier = Modifier.size(20.dp),
        )
    }
}
