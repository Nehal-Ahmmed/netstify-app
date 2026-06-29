package com.nhbhuiyan.nestify.presentation.ui.components.brainston

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

/**
 * Phase-A visual contract. Renders the full BrainSton primitive kit on the warm
 * canvas so the design language can be eyeballed before screens are built.
 * Wire into a debug route if you want it on-device; the @Preview renders in the IDE.
 */
@Composable
fun ComponentGallery() {
    val c = NestifyTheme.colors
    var tab by remember { mutableIntStateOf(0) }
    var search by remember { mutableStateOf("") }
    var field by remember { mutableStateOf("") }

    Surface(Modifier.fillMaxSize(), color = c.canvas) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Space.screen),
            verticalArrangement = Arrangement.spacedBy(Space.l),
        ) {
            // Typography
            Text("Display serif", style = NestifyTheme.type.displaySerif, color = c.ink)
            Text("H1 — Med school, without the burnout", style = NestifyTheme.type.h1Serif, color = c.ink)
            Text("H2 section title", style = NestifyTheme.type.h2Serif, color = c.ink)
            Kicker("Mono kicker label")
            Text(
                "Body copy in Inter Tight — calm, clinical, generous line height for comfortable reading across the academic super-app.",
                style = NestifyTheme.type.body, color = c.ink70,
            )

            SectionHead(title = "Chips", kicker = "Tones", actionText = "See all")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip("Default", tone = ChipTone.Default)
                Chip("Active", tone = ChipTone.Default, active = true)
                Chip("Soft", tone = ChipTone.Soft)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip("Brand", tone = ChipTone.Brand)
                Chip("Coral", tone = ChipTone.Coral, leadingIcon = Icons.Default.Favorite)
                Chip("Ok", tone = ChipTone.Ok)
                Chip("Warn", tone = ChipTone.Warn)
            }

            SectionHead(title = "Buttons", kicker = "Variants")
            NButtonGradient("Open Network", onClick = {}, full = true, trailingIcon = Icons.Default.Send)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton("Primary", {})
                NButton("Secondary", {}, variant = BtnVariant.Secondary)
                NButton("Soft", {}, variant = BtnVariant.Soft)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton("Ghost", {}, variant = BtnVariant.Ghost)
                NButton("Danger", {}, variant = BtnVariant.Danger)
                NButton("Dark", {}, variant = BtnVariant.Dark)
            }

            SectionHead(title = "Tab pill")
            TabPill(listOf("Subjects", "CT Marks", "Results"), tab, { tab = it })

            SectionHead(title = "Inputs")
            SearchBarPill(search, { search = it }, placeholder = "Search the campus…")
            NestifyInput(field, { field = it }, label = "Full name", placeholder = "Ayesha Rahman")

            SectionHead(title = "Cards & stats")
            NestifyCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Avatar("Ayesha Rahman", size = 44.dp)
                        Column {
                            Text("Ayesha Rahman", style = NestifyTheme.type.label.copy(), color = c.ink)
                            Kicker("CSE · Batch 21")
                        }
                    }
                    StatRow(listOf("3.82" to "CGPA", "12" to "Subjects", "4d" to "Next exam"))
                    ProgressBar(value = 0.62f)
                }
            }

            // Dark hero card
            NestifyCard(background = c.surfaceDk, padding = Space.xl) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Kicker("Term progress", color = NestifyTheme.colors.ink10)
                    Text("62%", style = NestifyTheme.type.displaySerif, color = androidx.compose.ui.graphics.Color.White)
                    ProgressBar(value = 0.62f, color = androidx.compose.ui.graphics.Color.White, track = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f))
                }
            }

            SectionHead(title = "Icon tiles")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconTile(Icons.Default.Bookmark)
                IconTile(Icons.Default.Notifications, background = c.coralSoft, tint = c.coral)
                IconTile(Icons.Default.Favorite, background = c.okSoft, tint = c.ok)
            }

            SectionHead(title = "Empty state")
            EmptyState(
                icon = Icons.Default.Bookmark,
                title = "No bookmarks yet",
                description = "Save notes, links and files to find them fast later.",
                primaryLabel = "Browse library",
                onPrimary = {},
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true, heightDp = 2200)
@Composable
private fun ComponentGalleryPreview() {
    NestifyTheme {
        ComponentGallery()
    }
}
