package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.LoadingShimmer
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.ViewMode
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.getRelativeTimeString
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.data.LinksViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinksListScreen(navController: NavController) {
    val viewModel: LinksViewmodel = hiltViewModel()
    val state = viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    var selectedLinks by remember { mutableStateOf(emptySet<Long>()) }
    val isSelectionMode = selectedLinks.isNotEmpty()

    val keyboardController = LocalSoftwareKeyboardController.current
    val searchFocusRequester = remember { FocusRequester() }

    val filteredLinks = state.value.links.filter { link ->
        searchQuery.isEmpty() || link.title?.contains(
            searchQuery,
            ignoreCase = true
        ) ?: false || link.description?.contains(searchQuery, ignoreCase = true) ?: false
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    searchQuery = searchQuery,
                    onSearchQuerychange = { searchQuery = it },
                    onSearchActiveChange = { isSearchActive = !isSearchActive },
                    focusRequester = searchFocusRequester,
                    onClose = {
                        isSearchActive = false
                        searchQuery = ""
                        keyboardController?.hide()
                    }
                )
            } else {
                MainTopBar(
                    title = "Links",
                    isLoading = state.value.isLoading,
                    isSelectionMode = isSelectionMode,
                    selectedCount = selectedLinks.size,
                    onBackClick = { navController.popBackStack() },
                    onSearchClick = {
                        isSearchActive = true
                    },
                    onViewModeChange = {
                        viewMode = if (viewMode == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
                    },
                    onSelectAllClick = {
                        selectedLinks = if (selectedLinks.size == filteredLinks.size) {
                            emptySet()
                        } else {
                            filteredLinks.map { it.id }.toSet()
                        }
                    },
                    onDeleteSelected = {
                        selectedLinks.forEach { id ->
                            viewModel.deleteLinkById(id)
                        }
                        selectedLinks = emptySet()
                    },
                    onClearSelection = {
                        selectedLinks = emptySet()
                    }
                )
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(
                    onClick = {
                        viewModel.createLink("test Link", "this is a test link", "www.google.com")
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "add link")
                }
            }
        }
    ) { padding ->
        if (state.value.isLoading) {
            LoadingShimmer()
        } else {
            when(viewMode){
                ViewMode.LIST -> LinksListView(
                    links = filteredLinks,
                    navController = navController,
                    selectedLinks= selectedLinks,
                    onLinkSelected = {id ->
                        selectedLinks= if(selectedLinks.contains(id)){
                            selectedLinks - id
                        }else{
                            selectedLinks+ id
                        }
                    },
                    modifier = Modifier.padding(padding)
                )
                ViewMode.GRID -> LinksGridView(
                    links = filteredLinks,
                    navController = navController,
                    selectedLinks = selectedLinks,
                    onLinkSelected = {id->
                        selectedLinks = if(selectedLinks.contains(id)) {
                            selectedLinks - id
                        }else{
                            selectedLinks + id
                        }
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }

        if(filteredLinks.isEmpty() && !state.value.isLoading) {
            EmptyLinksState(
                hasSearchQuery = searchQuery.isNotEmpty(),
                onClearSearch = { searchQuery = "" }
            )
        }
    }
}


@Composable
fun EmptyLinksState(hasSearchQuery: Boolean, onClearSearch: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.NoteAdd,
            contentDescription = "No Links found",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (hasSearchQuery) "No links found" else "No links yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasSearchQuery) {
                "Try different search terms or clear search"
            } else {
                "Create your first link by tapping the + button"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )

        if (hasSearchQuery) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onClearSearch) {
                Text("Clear Search")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    title: String,
    isLoading: Boolean,
    isSelectionMode: Boolean,
    selectedCount: Int,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onViewModeChange: () -> Unit,
    onSelectAllClick: () -> Unit,
    onDeleteSelected: () -> Unit,
    onClearSelection: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            if (isSelectionMode) {
                Text(
                    text = "$selectedCount selected",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        navigationIcon = {
            if (isSelectionMode) {
                IconButton(
                    onClick = onClearSelection
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Clear selection")
                }
            } else {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (isSelectionMode) {
                IconButton(onClick = onSelectAllClick) {
                    Icon(Icons.Default.SelectAll, contentDescription = "Select all")
                }
                IconButton(onClick = onDeleteSelected) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                }
            } else {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(onClick = onViewModeChange) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = "Change view mode"
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchQuery: String,
    onSearchQuerychange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
    onClose: () -> Unit
) {
    val keyboardcontroller = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardcontroller?.show()
    }

    CenterAlignedTopAppBar(
        title = {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQuerychange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = { Text("Search notes...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    )
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { keyboardcontroller?.hide() }
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQuerychange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        }
    )
}

@Composable
fun LinksListView(
    links: List<Link>,
    navController: NavController,
    selectedLinks: Set<Long>,
    onLinkSelected: (Long) -> Unit,
    modifier: Modifier,

    ) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(links, key = { it.id }) { link ->

            LinkListItemModern(
                link = link,
                isSelected = selectedLinks.contains(link.id),
                onClick = {
                    if (selectedLinks.isNotEmpty()) {
                        onLinkSelected(link.id)
                    } else {
                        navController.navigate(route = Route.LinkDetail.createRoute(link.id))
                    }
                },
                onLongClick = { onLinkSelected(link.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LinkListItemModern(
    link: Link,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (isSelected) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = link.title ?: link.url,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = link.description ?: link.url,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Updated ${getRelativeTimeString(link.updatedAt)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}


@Composable
fun LinksGridView(
    links: List<Link>,
    navController: NavController,
    selectedLinks: Set<Long>,
    onLinkSelected: (Long) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(links.chunked(2)) { rowNotes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowNotes.forEach { link ->
                    LinkGridItemModern(
                        link = link,
                        isSelected = selectedLinks.contains(link.id),
                        onClick = {
                            if (selectedLinks.isNotEmpty()) {
                                onLinkSelected(link.id)
                            } else {
                                navController.navigate(route = Route.LinkDetail.createRoute(link.id))
                            }
                        },
                        onLongClick = { onLinkSelected(link.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowNotes.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LinkGridItemModern(
    link: Link,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            if (isSelected) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = link.title ?: link.url,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = link.description ?: link.url,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Updated ${getRelativeTimeString(link.updatedAt)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
