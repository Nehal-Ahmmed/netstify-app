package com.nhbhuiyan.nestify.presentation.ui.screens.ProjectPlanner

data class ProjectPlanModel(
    val id: String,
    val name: String,
    val category: String, // e.g., "Mobile App", "Web Dev", "AI/ML", "Backend"
    val motive: String,
    val progress: Float, // 0.0 to 1.0
    val startDate: String,
    val deadline: String,
    val phases: List<ProjectPhase>,
    val techStack: List<String>,
    val priority: String = "Medium" // Low, Medium, High
)

data class ProjectPhase(
    val name: String,
    val isCompleted: Boolean,
    val tasks: List<ProjectTask>
)

data class ProjectTask(
    val name: String,
    val isDone: Boolean
)

val mockProjectPlans = listOf(
    ProjectPlanModel(
        id = "1",
        name = "Nestify 2.0 overhaul",
        category = "Mobile App",
        motive = "Modernizing the UI and improving performance.",
        progress = 0.45f,
        startDate = "June 01, 2026",
        deadline = "August 15, 2026",
        techStack = listOf("Jetpack Compose", "Kotlin", "Room"),
        phases = listOf(
            ProjectPhase("Research", true, listOf(ProjectTask("Competitor analysis", true))),
            ProjectPhase("Design", false, listOf(ProjectTask("Figma mockups", true), ProjectTask("Color palette", false)))
        ),
        priority = "High"
    ),
    ProjectPlanModel(
        id = "2",
        name = "AI Smart Assistant",
        category = "AI/ML",
        motive = "Integrating Gemini API for smart suggestions.",
        progress = 0.15f,
        startDate = "June 10, 2026",
        deadline = "September 30, 2026",
        techStack = listOf("Python", "TensorFlow", "FastAPI"),
        phases = listOf(
            ProjectPhase("Data Collection", false, listOf(ProjectTask("Gather datasets", false)))
        ),
        priority = "Medium"
    )
)
