package com.nhbhuiyan.nestify.presentation.navigation.Components

sealed class Route(val route: String){
    object SplashScreen: Route("splashScreen")

    object Home : Route("home")
    object Library : Route("library")
    object Services : Route("services")
    object Profile : Route("profile")

    // BrainSton remake — new bottom-nav tabs
    object Network : Route("network")   // center, uplifted — the academic social feed
    object MySpace : Route("mySpace")   // personal-productivity hub (Notes/Links/Files/Schedules/Projects)
    object Search: Route("search")
    //notes
    object Notes : Route("notes")
    object NoteDetail : Route("noteDetail/{noteId}"){
        fun createRoute(noteId: Long?) = "noteDetail/$noteId"
    }
    object createNote: Route("createNote/{noteId}"){
        fun createRoute(noteId: Long?) = "createNote/$noteId"
    }

    //links
    object LinkCategories : Route("linkCategories")
    object CategorySpreadSheet: Route("categorySpreadSheet/{categoryId}"){
        fun createRoute(categoryId: String) = "categorySpreadSheet/$categoryId"
    }
    object LinkGroupDetail: Route("linkGroupDetail/{groupId}"){
        fun createRoute(groupId: String) = "linkGroupDetail/$groupId"
    }
    object LinkDetail: Route("linkDetail/{linkId}"){
        fun createRoute(linkId: Long?) = "linkDetail/$linkId"
    }

    //project plans
    object  ProjectPlans: Route("projectPlans")
    object ProjectPlanDetail: Route("projectPlanDetail/{planId}"){
        fun createRoute(planId: String) = "projectPlanDetail/$planId"
    }

    //files
    object FolderScreen: Route("folderScreen")
    //filesScreen
    object Files: Route("files/{folderId}"){
        fun createFolder(folderId: Long) = "files/$folderId"
    }
    object FileDetail: Route("fileDetail/{fileId}"){
        fun createRoute(fileId: Long?) = "fileDetail/$fileId"
    }

    //archive
    object Archive : Route("archive")

    //favorites
    object Favorites : Route("favorites")

    //Bookmarks
    object Bookmarks: Route("bookmark")

    object ExamPlanner: Route("examPlanner")
    object ExamDetail: Route("examDetail/{subjectName}") {
        fun createRoute(subjectName: String) = "examDetail/$subjectName"
    }
    object ReadingRoom: Route("readingRoom/{topicId}") {
        fun createRoute(topicId: Long) = "readingRoom/$topicId"
    }
    object MyProjects: Route("myProjects")
    object ProjectDetail: Route("projectDetail/{projectId}"){
        fun createRoute(projectId: String) = "projectDetail/$projectId"
    }

    object Academics: Route("academics")
    object Results : Route("results")
    object Schedule : Route("schedule")
    object Settings : Route("settings")
    object Auth : Route("auth")

    object AppStartNav : Route("appStartNav")
    object InAppNav1 : Route("inAppNav")
    object InAppNav2 : Route("InAppNav2")
    object mediaNav: Route("mediaNav")
    object mediaNav2: Route("mediaNav2")
    object BottomNavBarNav: Route("bottomNavBarNav")

    // Management Hub
    object Management : Route("management")
    object MergeRequests : Route("mergeRequests")
    object RoleManagement : Route("roleManagement")
    object Announcements : Route("announcements")
}