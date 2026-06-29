package com.nhbhuiyan.nestify

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NestifyApplication : Application(){

    @Inject
    lateinit var testDataInitializer: com.nhbhuiyan.nestify.data.init.FirebaseTestDataInitializer

    override fun onCreate() {
        super.onCreate()
        
        // Configure Firestore offline persistence explicitly
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
            )
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        // Seed mock/demo test data once — debug builds only (kept out of release).
        if (BuildConfig.DEBUG) {
            testDataInitializer.initializeData()
        }
    }
}