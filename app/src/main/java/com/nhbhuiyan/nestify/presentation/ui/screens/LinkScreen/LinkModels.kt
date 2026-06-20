package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

data class LinkCategory(
    val id: String,
    val name: String,
    val color: Int, // Color resource or hex
    val itemCount: Int
)

data class LinkItem(
    val id: String,
    val title: String,
    val description: String,
    val imageRes: Int? = null,
    val links: List<SubLink>
)

data class SubLink(
    val name: String,
    val url: String
)

val mockLinkCategories = listOf(
    LinkCategory("1", "Study Links", 0xFF3498DB.toInt(), 12),
    LinkCategory("2", "Development Tools", 0xFF27AE60.toInt(), 8),
    LinkCategory("3", "Entertainment", 0xFFE67E22.toInt(), 5),
    LinkCategory("4", "Personal", 0xFF9B59B6.toInt(), 3)
)

val mockLinkItems = listOf(
    LinkItem(
        "101",
        "Level 2 Term 2 Drive",
        "All course materials, PDFs, and books for this term.",
        null,
        listOf(
            SubLink("Main Drive", "https://drive.google.com/test"),
            SubLink("Backup Folder", "https://drive.google.com/backup")
        )
    ),
    LinkItem(
        "102",
        "Ostad Course Link",
        "Direct link to the interactive learning dashboard.",
        null,
        listOf(
            SubLink("Course Dashboard", "https://ostad.app/dashboard")
        )
    ),
    LinkItem(
        "103",
        "Ostad Note 1",
        "Handwritten notes for Module 1.",
        null,
        listOf(
            SubLink("Module 1 PDF", "https://ostad.app/notes/1")
        )
    )
)
