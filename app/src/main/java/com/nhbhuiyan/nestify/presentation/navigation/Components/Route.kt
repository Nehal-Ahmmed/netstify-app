package com.nhbhuiyan.nestify.presentation.navigation.Components

sealed class Route(val route: String){
    object SplashScreen: Route("splashScreen")

    object Home : Route("home")
    object Search: Route("search")
    //notes
    object Notes : Route("notes")
    object NoteDetail : Route("noteDetail/{noteId}"){
        fun createRoute(noteId: Long?) = "noteDetail/$noteId"
    }
    object createNote: Route("createNote")

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

    //archive
    object Archive : Route("archive")

    //favorites
    object Favorites : Route("favorites")

    //Bookmarks
    object Bookmarks: Route("bookmark")


    object Academics: Route("academics")
    object Results : Route("results")
    object Settings : Route("settings")

    object AppStartNav : Route("appStartNav")
    object InAppNav1 : Route("inAppNav")
    object InAppNav2 : Route("InAppNav2")
    object mediaNav: Route("mediaNav")
    object mediaNav2: Route("mediaNav2")
    object BottomNavBarNav: Route("bottomNavBarNav")
}