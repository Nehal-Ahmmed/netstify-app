package com.nhbhuiyan.nestify.presentation.navigation.Components

sealed class Route(val route: String){
    object SplashScreen: Route("splashScreen")

    object Home : Route("home")
    //notes
    object Notes : Route("notes")
    object NoteDetail : Route("noteDetail/{noteId}"){
        fun createRoute(noteId: Long?) = "noteDetail/$noteId"
    }

    //links
    object Links : Route("links")
    object LinkDetail: Route("linkDetail/{linkId}"){
        fun createRoute(linkId: Long?) = "linkDetail/$linkId"
    }

    //files
    object Files: Route("files")
    object FileDetail: Route("fileDetail/{fileId}"){
        fun createRoute(fileId: Long?) = "fileDetail/$fileId"
    }

    //routines
    object Routines : Route("routines")
    object RoutineDetail : Route("routineDetail/{routineId}") {
        fun createRoute(routineId: Long?) = "routineDetail/$routineId"
    }

    object Academics: Route("academics")
    object Results : Route("results")
    object Settings : Route("settings")

    object AppStartNav : Route("appStartNav")
    object InAppNav : Route("inAppNav")
    object mediaNav: Route("mediaNav")
}