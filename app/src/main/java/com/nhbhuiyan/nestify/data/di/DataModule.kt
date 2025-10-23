package com.nhbhuiyan.nestify.data.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nhbhuiyan.nestify.data.local.AppDataBase
import com.nhbhuiyan.nestify.data.local.Dao.ContentDao
import com.nhbhuiyan.nestify.data.repository.ContentRepositoryImpl
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context : Context) : AppDataBase {
        return Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "nestify.db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d("DATABASE", "✅ Database was created!")
                }
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    Log.d("DATABASE", "✅ Database was opened!")
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideContentDao(appDataBase: AppDataBase) : ContentDao{
        return appDataBase.contentDao()
    }

    @Provides
    @Singleton
    fun provideContentRepository(contentDao: ContentDao) : ContentRepository{
        return ContentRepositoryImpl(contentDao)
    }


}