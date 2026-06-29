package com.nhbhuiyan.nestify.data.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nhbhuiyan.nestify.data.datastore.SettingDatastore
import com.nhbhuiyan.nestify.data.local.AppDataBase
import com.nhbhuiyan.nestify.data.local.migrations.ALL_MIGRATIONS
import com.nhbhuiyan.nestify.data.local.Dao.ContentDao
import com.nhbhuiyan.nestify.data.local.Dao.ProfileDao
import com.nhbhuiyan.nestify.data.repository.ContentRepositoryImpl
import com.nhbhuiyan.nestify.data.repository.ProfileRepositoryImpl
import com.nhbhuiyan.nestify.data.repository.SettingsRepoImpl
import com.nhbhuiyan.nestify.domain.manager.AppSettingManager
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import com.nhbhuiyan.nestify.domain.repository.ProfileRepository
import com.nhbhuiyan.nestify.domain.repository.SettingsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

object DatabaseKeys {
    private const val TAG = "DatabaseKeys"
    fun getPassphrase(context: Context): ByteArray {
        val prefs = context.getSharedPreferences("nestify_secure_prefs", Context.MODE_PRIVATE)
        var key = prefs.getString("db_enc_key", null)
        if (key == null) {
            Log.d(TAG, "No encryption key found. Generating a new key...")
            key = java.util.UUID.randomUUID().toString()
            prefs.edit().putString("db_enc_key", key).apply()
            Log.d(TAG, "New key generated and saved successfully.")
        } else {
            Log.d(TAG, "Retrieved existing encryption key.")
        }
        return key.toByteArray()
    }

    fun clearKey(context: Context) {
        Log.d(TAG, "Clearing database encryption key...")
        val prefs = context.getSharedPreferences("nestify_secure_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("db_enc_key").apply()
        Log.d(TAG, "Database encryption key cleared successfully.")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context : Context) : AppDataBase {
        Log.d("DataModule", "Initializing secure database...")
        try {
            System.loadLibrary("sqlcipher")
            Log.d("DataModule", "sqlcipher library loaded successfully.")
        } catch (e: Exception) {
            Log.e("DataModule", "Failed to load sqlcipher library: ${e.message}", e)
        }
        val passphrase = DatabaseKeys.getPassphrase(context)
        val factory = SupportOpenHelperFactory(passphrase)
        Log.d("DataModule", "SupportOpenHelperFactory created with passphrase.")

        Log.d("DataModule", "Building Room AppDatabase...")
        fun buildDatabase(factoryHelper: SupportOpenHelperFactory): AppDataBase {
            return Room.databaseBuilder(
                context,
                AppDataBase::class.java,
                "nestify.db"
            )
                .openHelperFactory(factoryHelper)
                .addMigrations(*ALL_MIGRATIONS)
                .fallbackToDestructiveMigrationOnDowngrade()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.d("DATABASE", "✅ Database was created!")
                        db.execSQL("INSERT INTO schedule_categories (id, name, colorHex) VALUES (1, 'Weekly', '#3498DB')")
                        db.execSQL("INSERT INTO schedule_categories (id, name, colorHex) VALUES (2, 'Monthly', '#9B59B6')")
                        db.execSQL("INSERT INTO schedule_categories (id, name, colorHex) VALUES (3, 'Yearly', '#27AE60')")
                    }
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        Log.d("DATABASE", "✅ Database was opened!")
                    }
                })
                .build()
        }

        var db = buildDatabase(factory)

        // Force open to test/verify encryption health and capture exceptions
        try {
            db.openHelper.writableDatabase
            Log.d("DataModule", "✅ Database verified and opened successfully via writableDatabase check.")
        } catch (e: Exception) {
            Log.e("DataModule", "❌ Secure database failed to open (possibly unencrypted or corrupt). Wiping and recreating...", e)
            try {
                db.close()
            } catch (ex: Exception) {
                // ignore closing issues of unopenable database
            }
            context.deleteDatabase("nestify.db")
            DatabaseKeys.clearKey(context)
            
            val newPassphrase = DatabaseKeys.getPassphrase(context)
            val newFactory = SupportOpenHelperFactory(newPassphrase)
            
            Log.d("DataModule", "Rebuilding Room AppDatabase from scratch...")
            db = buildDatabase(newFactory)
            
            try {
                db.openHelper.writableDatabase
                Log.d("DataModule", "✅ Recreated database verified and opened successfully.")
            } catch (retryEx: Exception) {
                Log.e("DataModule", "❌ CRITICAL ERROR: Failed to open recreated database: ${retryEx.message}", retryEx)
            }
        }

        return db
    }

    @Provides
    @Singleton
    fun provideContentDao(appDataBase: AppDataBase) : ContentDao{
        return appDataBase.contentDao()
    }

    @Provides
    @Singleton
    fun provideContentRepository(@ApplicationContext context: Context,contentDao: ContentDao) : ContentRepository{
        return ContentRepositoryImpl(contentDao = contentDao, context = context)
    }

    @Provides
    @Singleton
    fun provideSettingsDatastore(@ApplicationContext context: Context) : SettingDatastore{
        return SettingDatastore(context= context)
    }

    @Provides
    @Singleton
    fun provideSettingRepoImpl(settingDatastore: SettingDatastore) : SettingsRepo{
        return SettingsRepoImpl(settingsDatastore = settingDatastore)
    }

    @Provides
    @Singleton
    fun provideAppSettingsManager(settingsRepo : SettingsRepo) : AppSettingManager = AppSettingManager(settingRepo = settingsRepo)

    @Provides
    @Singleton
    fun provideMediaDao(appDataBase: AppDataBase): com.nhbhuiyan.nestify.data.local.Dao.MediaDao {
        return appDataBase.mediaDao()
    }

    @Provides
    @Singleton
    fun provideLibraryItemDao(appDataBase: AppDataBase): com.nhbhuiyan.nestify.data.local.Dao.LibraryItemDao {
        return appDataBase.libraryItemDao()
    }

    @Provides
    @Singleton
    fun provideMediaRepository(mediaDao: com.nhbhuiyan.nestify.data.local.Dao.MediaDao): com.nhbhuiyan.nestify.domain.repository.MediaRepository {
        return com.nhbhuiyan.nestify.data.repository.MediaRepositoryImpl(mediaDao)
    }

    @Provides
    @Singleton
    fun provideLibraryRepository(libraryItemDao: com.nhbhuiyan.nestify.data.local.Dao.LibraryItemDao): com.nhbhuiyan.nestify.domain.repository.LibraryRepository {
        return com.nhbhuiyan.nestify.data.repository.LibraryRepositoryImpl(libraryItemDao)
    }

    @Provides
    @Singleton
    fun provideScheduleDao(appDataBase: AppDataBase): com.nhbhuiyan.nestify.data.local.Dao.ScheduleDao {
        return appDataBase.scheduleDao()
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(scheduleDao: com.nhbhuiyan.nestify.data.local.Dao.ScheduleDao): com.nhbhuiyan.nestify.domain.repository.ScheduleRepository {
        return com.nhbhuiyan.nestify.data.repository.ScheduleRepositoryImpl(scheduleDao)
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): com.nhbhuiyan.nestify.domain.alarm.AlarmScheduler {
        return com.nhbhuiyan.nestify.data.alarm.AndroidAlarmScheduler(context)
    }

    @Provides
    @Singleton
    fun provideProfileDao(appDataBase: AppDataBase): ProfileDao {
        return appDataBase.profileDao()
    }

    @Provides
    @Singleton
    fun provideProfileRepository(profileDao: ProfileDao): ProfileRepository {
        return ProfileRepositoryImpl(profileDao)
    }

    @Provides
    @Singleton
    fun provideProjectPlanDao(appDataBase: AppDataBase): com.nhbhuiyan.nestify.data.local.Dao.ProjectPlanDao {
        return appDataBase.projectPlanDao()
    }

    @Provides
    @Singleton
    fun provideProjectPlanRepository(dao: com.nhbhuiyan.nestify.data.local.Dao.ProjectPlanDao): com.nhbhuiyan.nestify.projectplans.domain.repo.ProjectPlanRepository {
        return com.nhbhuiyan.nestify.projectplans.data.repository.ProjectPlanRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideMyProjectDao(appDataBase: AppDataBase): com.nhbhuiyan.nestify.data.local.Dao.MyProjectDao {
        return appDataBase.myProjectDao()
    }

    @Provides
    @Singleton
    fun provideMyProjectRepository(dao: com.nhbhuiyan.nestify.data.local.Dao.MyProjectDao): com.nhbhuiyan.nestify.domain.repository.MyProjectRepository {
        return com.nhbhuiyan.nestify.data.repository.MyProjectRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideExamPlannerDao(appDataBase: AppDataBase): com.nhbhuiyan.nestify.data.local.Dao.ExamPlannerDao {
        return appDataBase.examPlannerDao()
    }
}