package com.nhbhuiyan.nestify.data.di

import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.CreateFileUseCase
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.CreateLinkUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.CreateNoteUseCase
import com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases.CreateRoutineUsecases
import com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases.GetAllClassRoutinesUsecases
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetAllFilesUseCase
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.GetAllLinksUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.GetAllNotesUseCase
import com.nhbhuiyan.nestify.presentation.viewModel.HomeViewModel
import com.nhbhuiyan.nestify.presentation.viewModel.ShareViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewmodelModule {

    @Provides
    @ViewModelScoped
    fun provideHomeViewModel(
        getAllNotesUseCase: GetAllNotesUseCase,
        getAllLinksUseCase: GetAllLinksUseCase,
        getAllFilesUseCase: GetAllFilesUseCase,
        getAllClassRoutinesUsecases: GetAllClassRoutinesUsecases
    ): HomeViewModel{
        return HomeViewModel(getAllFilesUseCase,getAllLinksUseCase,getAllNotesUseCase,getAllClassRoutinesUsecases)
    }

//    @Provides
//    @ViewModelScoped
//    fun provideNotesViewModel(
//        getAllNotesUseCase: GetAllNotesUseCase,
//        createNoteUseCase: CreateNoteUseCase
//    ): NotesViewModel{
//        return NotesViewModel(getAllNotesUseCase,createNoteUseCase)
//    }

    @Provides
    @ViewModelScoped
    fun provideShareViewModel(
        createNoteUseCase: CreateNoteUseCase,
        createLinkUseCase: CreateLinkUseCase,
        createFileUseCase: CreateFileUseCase,
        createRoutineUsecases: CreateRoutineUsecases
    ): ShareViewModel{
        return ShareViewModel(createNoteUseCase,createLinkUseCase,createFileUseCase,createRoutineUsecases)
    }
}