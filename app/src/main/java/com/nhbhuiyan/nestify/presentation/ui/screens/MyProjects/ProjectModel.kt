package com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects

import com.nhbhuiyan.nestify.R

data class ProjectModel(
    val id: String,
    val name: String,
    val motive: String,
    val description: String,
    val features: List<String>,
    val specialities: List<String>,
    val techStack: List<String>,
    val libraries: List<String>,
    val sources: Map<String, String>, // e.g., "GitHub" to "url"
    val brandLogo: Int, // Drawable resource
    val demoImages: List<Int>, // Drawable resources
    val videoUrl: String? = null,
    val whereToFind: String,
    val category: String,
    val status: String = "Completed"
)

val mockProjects = listOf(
    ProjectModel(
        id = "1",
        name = "Nestify",
        motive = "All-in-one productivity suite for students and developers.",
        description = "Nestify is a comprehensive personal management application designed to streamline daily tasks, project planning, and academic tracking. It integrates note-taking, link management, and schedule organization into a single, cohesive experience.",
        features = listOf("Smart Notes", "Link Archiving", "Project Roadmap", "Exam Countdown", "File Organization"),
        specialities = listOf("Offline-first Architecture", "Material 3 Design", "Custom Navigation Graph", "Highly Scalable"),
        techStack = listOf("Kotlin", "Jetpack Compose", "Dagger Hilt", "Room DB", "Coroutines"),
        libraries = listOf("Compose Navigation", "Retrofit", "Kotlinx Serialization", "Coil"),
        sources = mapOf("GitHub" to "https://github.com/nhbhuiyan/nestify"),
        brandLogo = R.drawable.nestifyappicon,
        demoImages = listOf(R.drawable.banner, R.drawable.profile2),
        videoUrl = "https://youtube.com/demo",
        whereToFind = "Play Store / GitHub",
        category = "Productivity"
    ),
    ProjectModel(
        id = "2",
        name = "Schedule Engine",
        motive = "Efficient time management with dynamic scheduling.",
        description = "A powerful engine that generates optimized daily routines based on user preferences and priority levels. It handles recurring tasks and provides smart notifications.",
        features = listOf("Dynamic Routine Generation", "Priority Sorting", "Notification System", "Analytics Dashboard"),
        specialities = listOf("Algorithm-driven Scheduling", "Low Latency", "Extensive Customization"),
        techStack = listOf("Kotlin", "Flow", "Live Data", "WorkManager"),
        libraries = listOf("Timber", "Klock", "Moshi"),
        sources = mapOf("GitLab" to "https://gitlab.com/engine"),
        brandLogo = R.drawable.routine,
        demoImages = listOf(R.drawable.routine),
        videoUrl = null,
        whereToFind = "Internal Tool",
        category = "Utility"
    )
)
