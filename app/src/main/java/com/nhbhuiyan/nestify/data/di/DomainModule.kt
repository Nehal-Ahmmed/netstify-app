package com.nhbhuiyan.nestify.data.di

import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.CopyFileToAppStorageUseCase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.CreateFileUseCase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.FileUploadUseCases
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.CreateLinkUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.CreateNoteUseCase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetAllFilesUseCase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetAppFilesDirectoryUseCase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.MoveFileToAppStorageUseCase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.fileFolderUsescases.createFolderUsecase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.fileFolderUsescases.getFilesByFolderUsecase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.fileFolderUsescases.getFoldersByCategoryUsecase
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
    fun provideUploadFileUseCase(repository: ContentRepository): FileUploadUseCases {
        return FileUploadUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideCopyFileToAppStorageUseCase(repository: ContentRepository): CopyFileToAppStorageUseCase {
        return CopyFileToAppStorageUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideMoveFileToAppStorageUseCase(repository: ContentRepository): MoveFileToAppStorageUseCase {
        return MoveFileToAppStorageUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAppFilesDirectoryUseCase(repository: ContentRepository): GetAppFilesDirectoryUseCase {
        return GetAppFilesDirectoryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCreateFolderUsecase(repository: ContentRepository) : createFolderUsecase{
        return createFolderUsecase(repository = repository)
    }

    @Provides
    @Singleton
    fun providegetFoldersByCategoryUsecase(repository: ContentRepository) : getFoldersByCategoryUsecase {
        return getFoldersByCategoryUsecase(repository = repository)
    }

    @Provides
    @Singleton
    fun providegetFilesByFolderUsecase(repository: ContentRepository) : getFilesByFolderUsecase {
        return getFilesByFolderUsecase(repository = repository)
    }
}