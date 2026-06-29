package com.nhbhuiyan.nestify.presentation.ui.screens.AcademicFeed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.domain.model.Post
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Avatar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnSize
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.GlassNavSpace
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyInput
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space
import kotlinx.coroutines.launch

/**
 * Academic Network — a Firestore-backed, university-focused social feed in the BrainSton
 * design language. Students share academic aids, project plans, completed projects,
 * hackathons, project & research invites and university news, scoped to their class group.
 *
 * Backed by [NetworkFeedViewModel] over `classGroups/{groupId}/posts`. One snapshot listener
 * feeds the screen; category filtering happens here in the UI.
 */

// ── Post categories → label + ChipTone ──────────────────────────────────────

enum class PostCategory(val key: String, val label: String, val tone: ChipTone) {
    ACADEMIC_AID("academic_aid", "Academic Aid", ChipTone.Ok),
    PROJECT_PLAN("project_plan", "Project Plan", ChipTone.Soft),
    COMPLETED_PROJECT("completed_project", "Completed Project", ChipTone.Brand),
    HACKATHON("hackathon", "Hackathon", ChipTone.Warn),
    PROJECT_INVITE("project_invite", "Project Invite", ChipTone.Soft),
    UNIVERSITY_NEWS("university_news", "University News", ChipTone.Coral),
    RESEARCH_INVITE("research_invite", "Research Invite", ChipTone.Ok);

    companion object {
        fun fromKey(key: String): PostCategory? = entries.firstOrNull { it.key == key }
    }
}

// ── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicFeedScreen(
    navController: NavController,
    viewModel: NetworkFeedViewModel = hiltViewModel(),
) {
    val c = NestifyTheme.colors
    val posts by viewModel.posts.collectAsState()
    val session by viewModel.sessionFlow.collectAsState()
    val status by viewModel.status.collectAsState()

    var selectedCategory by remember { mutableStateOf<PostCategory?>(null) }
    var showComposer by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val visiblePosts = remember(posts, selectedCategory) {
        selectedCategory?.let { cat -> posts.filter { it.type == cat.key } } ?: posts
    }
    val currentUid = session?.uid

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Space.screen, vertical = Space.m)
        ) {
            Text(
                text = "Network",
                style = NestifyTheme.type.h2Serif,
                color = c.ink
            )
            Text(
                text = "Your academic community",
                style = NestifyTheme.type.meta,
                color = c.ink50
            )
        }

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
            item(key = "composer") {
                ComposerRow(
                    name = session?.displayName ?: "",
                    onOpen = { showComposer = true },
                )
            }
            item(key = "filters") {
                CategoryFilterRow(
                    selected = selectedCategory,
                    onSelect = { selectedCategory = it },
                )
            }

            if (visiblePosts.isEmpty()) {
                item(key = "empty") {
                    Spacer(Modifier.height(Space.xl))
                    EmptyState(
                        icon = Icons.Outlined.Hub,
                        title = if (selectedCategory == null) "No posts yet" else "Nothing here yet",
                        description = if (selectedCategory == null)
                            "Be the first to share an academic resource, project, or opportunity with your class."
                        else
                            "No ${selectedCategory!!.label.lowercase()} posts in your network right now.",
                        primaryLabel = if (selectedCategory == null) "Create a post" else null,
                        onPrimary = if (selectedCategory == null) ({ showComposer = true }) else null,
                    )
                }
            } else {
                items(visiblePosts, key = { it.id }) { post ->
                    FeedPostCard(
                        post = post,
                        likedByMe = currentUid != null && post.likedBy.contains(currentUid),
                        canModerate = viewModel.canModerate(post, session),
                        onLike = { viewModel.toggleLike(post.id) },
                        onDelete = { viewModel.deletePost(post.id) },
                    )
                }
            }
        }
    }

    if (showComposer) {
        ModalBottomSheet(
            onDismissRequest = { showComposer = false },
            sheetState = sheetState,
            containerColor = c.surface,
            dragHandle = { BottomSheetDefaults.DragHandle(color = c.hair2) },
            shape = Radii.xl,
        ) {
            ComposePostSheet(
                posting = status is FeedStatus.Posting,
                errorMessage = (status as? FeedStatus.Error)?.message,
                onSubmit = { category, title, body, tags ->
                    viewModel.createPost(category.key, title, body, tags) {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { showComposer = false }
                    }
                },
            )
        }
    }
}

// ── Composer entry row ─────────────────────────────────────────────────────────

@Composable
private fun ComposerRow(name: String, onOpen: () -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.m) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.m),
        ) {
            Avatar(name, size = 40.dp)
            Box(
                Modifier
                    .weight(1f)
                    .clip(Radii.pill)
                    .background(c.surface2)
                    .clickable(onClick = onOpen)
                    .padding(horizontal = Space.l, vertical = 12.dp),
            ) {
                Text("Share an academic resource…", style = NestifyTheme.type.body, color = c.ink50)
            }
            Box(
                Modifier
                    .size(40.dp)
                    .clip(Radii.m)
                    .background(c.brand)
                    .clickable(onClick = onOpen),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Add, contentDescription = "New post", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}

// ── Category filter row ────────────────────────────────────────────────────────

@Composable
private fun CategoryFilterRow(selected: PostCategory?, onSelect: (PostCategory?) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Space.s),
    ) {
        Chip(
            label = "All",
            tone = if (selected == null) ChipTone.Default else ChipTone.Ghost,
            active = selected == null,
            onClick = { onSelect(null) },
        )
        PostCategory.entries.forEach { cat ->
            Chip(
                label = cat.label,
                tone = if (selected == cat) cat.tone else ChipTone.Ghost,
                onClick = { onSelect(cat) },
            )
        }
    }
}

// ── Feed post card ─────────────────────────────────────────────────────────────

@Composable
private fun FeedPostCard(
    post: Post,
    likedByMe: Boolean,
    canModerate: Boolean,
    onLike: () -> Unit,
    onDelete: () -> Unit,
) {
    val c = NestifyTheme.colors
    val category = remember(post.type) { PostCategory.fromKey(post.type) }

    NestifyCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            // Author row
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                Avatar(post.authorName, size = 44.dp)
                Column(Modifier.weight(1f)) {
                    Text(
                        post.authorName.ifBlank { "Anonymous" },
                        style = NestifyTheme.type.h3Serif,
                        color = c.ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Kicker(buildMetaLine(post))
                }
                if (category != null) Chip(label = category.label, tone = category.tone)
            }

            Spacer(Modifier.height(Space.m))

            Text(post.title, style = NestifyTheme.type.h2Serif, color = c.ink)
            if (post.body.isNotBlank()) {
                Spacer(Modifier.height(Space.xs))
                Text(post.body, style = NestifyTheme.type.body, color = c.ink70)
            }

            if (post.tags.isNotEmpty()) {
                Spacer(Modifier.height(Space.s))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Space.s),
                ) {
                    post.tags.forEach { tag ->
                        Text(
                            "#${tag.removePrefix("#")}",
                            style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium),
                            color = c.brand,
                        )
                    }
                }
            }

            Spacer(Modifier.height(Space.m))
            Box(Modifier.fillMaxWidth().height(1.dp).background(c.hair2))
            Spacer(Modifier.height(Space.s))

            // Action row
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                FeedAction(
                    icon = if (likedByMe) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    label = "Endorse${if (post.likeCount > 0) " · ${post.likeCount}" else ""}",
                    tint = if (likedByMe) c.coral else c.ink50,
                    onClick = onLike,
                )
                Spacer(Modifier.width(Space.s))
                FeedAction(
                    icon = Icons.AutoMirrored.Outlined.Comment,
                    label = "Comment${if (post.commentCount > 0) " · ${post.commentCount}" else ""}",
                    tint = c.ink50,
                    onClick = { /* comments — future */ },
                )
                Spacer(Modifier.weight(1f))
                FeedAction(
                    icon = Icons.Outlined.Share,
                    label = "Share",
                    tint = c.ink50,
                    onClick = { /* share — future */ },
                )
                if (canModerate) {
                    Spacer(Modifier.width(Space.s))
                    IconButtonChrome(
                        Icons.Outlined.DeleteOutline,
                        onClick = onDelete,
                        tint = c.coral,
                        contentDescription = "Remove post",
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedAction(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(Radii.s)
            .clickable(onClick = onClick)
            .padding(horizontal = Space.s, vertical = Space.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Space.xs),
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(18.dp))
        Text(label, style = NestifyTheme.type.label, color = tint)
    }
}

// ── Compose-post bottom sheet ──────────────────────────────────────────────────

@Composable
private fun ComposePostSheet(
    posting: Boolean,
    errorMessage: String?,
    onSubmit: (PostCategory, String, String, List<String>) -> Unit,
) {
    val c = NestifyTheme.colors
    var category by remember { mutableStateOf(PostCategory.ACADEMIC_AID) }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var tagsRaw by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Space.screen)
            .padding(bottom = Space.xxxl),
    ) {
        Kicker("New post")
        Spacer(Modifier.height(4.dp))
        Text("Share with your network", style = NestifyTheme.type.h2Serif, color = c.ink)
        Spacer(Modifier.height(Space.l))

        // Category selector
        Text("Category", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium), color = c.ink70)
        Spacer(Modifier.height(Space.s))
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Space.s),
        ) {
            PostCategory.entries.forEach { cat ->
                Chip(
                    label = cat.label,
                    tone = if (cat == category) cat.tone else ChipTone.Ghost,
                    onClick = { category = cat },
                )
            }
        }

        Spacer(Modifier.height(Space.l))
        NestifyInput(
            value = title,
            onValueChange = { title = it },
            label = "Title",
            placeholder = "A clear, descriptive headline",
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Space.l))
        Text("Details", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium), color = c.ink70)
        Spacer(Modifier.height(Space.xs))
        MultilineInput(
            value = body,
            onValueChange = { body = it },
            placeholder = "Share the details, context, or what you're looking for…",
        )

        Spacer(Modifier.height(Space.l))
        NestifyInput(
            value = tagsRaw,
            onValueChange = { tagsRaw = it },
            label = "Tags",
            placeholder = "DataStructures CSE221 Notes",
            modifier = Modifier.fillMaxWidth(),
        )

        if (errorMessage != null) {
            Spacer(Modifier.height(Space.m))
            Text(errorMessage, style = NestifyTheme.type.label, color = c.coral)
        }

        Spacer(Modifier.height(Space.xl))
        NButton(
            label = if (posting) "Posting…" else "Post to Network",
            onClick = {
                if (!posting && title.isNotBlank()) {
                    onSubmit(category, title, body, parseTags(tagsRaw))
                }
            },
            size = BtnSize.Lg,
            full = true,
        )
    }
}

@Composable
private fun MultilineInput(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    val c = NestifyTheme.colors
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .clip(Radii.m)
            .background(c.surface)
            .border(1.5.dp, c.hair, Radii.m)
            .padding(Space.m14),
    ) {
        if (value.isEmpty()) Text(placeholder, style = NestifyTheme.type.body, color = c.ink50)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = NestifyTheme.type.body.copy(color = c.ink),
            cursorBrush = SolidColor(c.brand),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────────

private fun parseTags(raw: String): List<String> =
    raw.split(',', ' ', '\n')
        .map { it.trim().removePrefix("#") }
        .filter { it.isNotBlank() }
        .distinct()
        .take(8)

private fun buildMetaLine(post: Post): String {
    val time = relativeTime(post.createdAt)
    return if (post.authorMeta.isBlank()) time
    else if (time.isBlank()) post.authorMeta
    else "${post.authorMeta} · $time"
}

private fun relativeTime(millis: Long?): String {
    if (millis == null) return "now"
    val diff = System.currentTimeMillis() - millis
    if (diff < 0) return "now"
    val minutes = diff / 60_000
    val hours = minutes / 60
    val days = hours / 24
    return when {
        minutes < 1 -> "now"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days < 7 -> "${days}d"
        else -> "${days / 7}w"
    }
}
