package com.nhbhuiyan.nestify.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import com.nhbhuiyan.nestify.domain.manager.UserSessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions {
        return FirebaseFunctions.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        sessionManager: UserSessionManager,
        syncManager: com.nhbhuiyan.nestify.data.sync.SyncManager
    ): com.nhbhuiyan.nestify.domain.repository.AuthRepository {
        return com.nhbhuiyan.nestify.data.repository.AuthRepositoryImpl(auth, firestore, sessionManager, syncManager)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore
    ): com.nhbhuiyan.nestify.domain.repository.UserRepository {
        return com.nhbhuiyan.nestify.data.repository.UserRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideClassGroupRepository(
        firestore: FirebaseFirestore
    ): com.nhbhuiyan.nestify.domain.repository.ClassGroupRepository {
        return com.nhbhuiyan.nestify.data.repository.ClassGroupRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideDepartmentRepository(
        firestore: FirebaseFirestore
    ): com.nhbhuiyan.nestify.domain.repository.DepartmentRepository {
        return com.nhbhuiyan.nestify.data.repository.DepartmentRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideMergeRequestRepository(
        firestore: FirebaseFirestore
    ): com.nhbhuiyan.nestify.domain.repository.MergeRequestRepository {
        return com.nhbhuiyan.nestify.data.repository.MergeRequestRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideAnnouncementRepository(
        firestore: FirebaseFirestore
    ): com.nhbhuiyan.nestify.domain.repository.AnnouncementRepository {
        return com.nhbhuiyan.nestify.data.repository.AnnouncementRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun providePostRepository(
        firestore: FirebaseFirestore
    ): com.nhbhuiyan.nestify.domain.repository.PostRepository {
        return com.nhbhuiyan.nestify.data.repository.PostRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideExamDataRepository(
        firestore: FirebaseFirestore,
        departmentRepository: com.nhbhuiyan.nestify.domain.repository.DepartmentRepository,
        examPlannerDao: com.nhbhuiyan.nestify.data.local.Dao.ExamPlannerDao
    ): com.nhbhuiyan.nestify.domain.repository.ExamDataRepository {
        return com.nhbhuiyan.nestify.data.repository.ExamDataRepositoryImpl(
            firestore,
            departmentRepository,
            examPlannerDao
        )
    }
}
