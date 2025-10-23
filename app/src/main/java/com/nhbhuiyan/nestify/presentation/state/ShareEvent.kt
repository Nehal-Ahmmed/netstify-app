package com.nhbhuiyan.nestify.presentation.state

sealed class ShareEvent {
    object NoteSaved : ShareEvent()
    object LinkSaved : ShareEvent()
    data class Error(val message : String) : ShareEvent()
}