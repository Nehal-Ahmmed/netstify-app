package com.nhbhuiyan.nestify.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.CreateFileUseCase
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.CreateLinkUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.CreateNoteUseCase
import com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases.CreateRoutineUsecases
import com.nhbhuiyan.nestify.presentation.state.ShareEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val createNoteUseCase: CreateNoteUseCase,
    private val createLinkUseCase: CreateLinkUseCase,
    private val createFileUseCase: CreateFileUseCase,
    private val createRoutineUsecases: CreateRoutineUsecases
) : ViewModel(){
    private val _shareEvent = MutableSharedFlow<ShareEvent>()
    val shareEvent: SharedFlow<ShareEvent> = _shareEvent

    fun handleSharedText(title: String,description: String,url: String){
        viewModelScope.launch {
            if(isValidUrl(url)){
                createLinkUseCase(title,description,url)
                _shareEvent.emit(ShareEvent.LinkSaved)
            }else{
                createNoteUseCase("Shared Note",url)
                _shareEvent.emit(ShareEvent.NoteSaved)
            }
        }
    }
    private fun isValidUrl(text: String) : Boolean {
        return text.startsWith("http://") || text.startsWith("https://")
    }
}