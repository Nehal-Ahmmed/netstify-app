package com.nhbhuiyan.nestify.data.di

import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.CreateFileUseCase
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.CreateLinkUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.CreateNoteUseCase
import com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases.CreateRoutineUsecases
import com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases.GetAllClassRoutinesUsecases
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetAllFilesUseCase
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.GetAllLinksUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.GetAllNotesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object DomainModule {

    @Provides
    @Singleton
    fun provideCreateNoteUseCase(repository: ContentRepository) : CreateNoteUseCase {
        return CreateNoteUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAllNotesUseCase(repository: ContentRepository): GetAllNotesUseCase {
        return GetAllNotesUseCase(repository)
    }



    @Provides
    @Singleton
    fun provideCreateLinkUseCase(repository: ContentRepository) : CreateLinkUseCase {
        return CreateLinkUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAllLinksUseCase(repository: ContentRepository): GetAllLinksUseCase {
        return GetAllLinksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCreateFileUseCase(repository: ContentRepository) : CreateFileUseCase {
        return CreateFileUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAllFilesUseCase(repository: ContentRepository): GetAllFilesUseCase {
        return GetAllFilesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCreateClassRoutineUseCase(repository: ContentRepository) : CreateRoutineUsecases {
        return CreateRoutineUsecases(repository)
    }

    @Provides
    @Singleton
    fun provideGetAllClassRoutinesUseCase(repository: ContentRepository): GetAllClassRoutinesUsecases {
        return GetAllClassRoutinesUsecases(repository)
    }
}