package com.nhbhuiyan.nestify.data.sync

import java.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nhbhuiyan.nestify.data.local.AppDataBase
import androidx.room.withTransaction
import com.nhbhuiyan.nestify.data.local.entity.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.crypto.spec.GCMParameterSpec
import java.security.SecureRandom

@Singleton
class SyncManager @Inject constructor(
    private val appDataBase: AppDataBase,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
    private val TAG = "SyncManager"

    // Custom Moshi Adapter for kotlinx.datetime.Instant
    class InstantAdapter {
        @ToJson
        fun toJson(instant: kotlinx.datetime.Instant): String = instant.toString()

        @FromJson
        fun fromJson(value: String): kotlinx.datetime.Instant = kotlinx.datetime.Instant.parse(value)
    }

    private val moshi = Moshi.Builder()
        .add(InstantAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val backupAdapter = moshi.adapter(BackupPayload::class.java)

    /**
     * Serializes, encrypts, and uploads the entire personal workspace to Firestore.
     */
    suspend fun performBackup(): Result<Unit> = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext Result.failure(Exception("No user signed in"))
        Log.d(TAG, "Starting backup for user: $uid")
        try {
            // 1. Gather all data from the 13 personal workspace tables
            val notes = appDataBase.contentDao().getAllNotes().first()
            val links = appDataBase.contentDao().getAllLinks().first()
            val files = appDataBase.contentDao().getAllFIles().first()
            val fileFolders = appDataBase.contentDao().getAllFileFolders().first()
            val linkFolders = appDataBase.contentDao().getAllLinkFolders().first()
            
            val media = appDataBase.mediaDao().getAllMedia().first()
            val libraryItems = appDataBase.libraryItemDao().getAllLibraryItems().first()
            
            val categories = appDataBase.scheduleDao().getAllCategories().first()
            val schedules = appDataBase.scheduleDao().getAllScheduleItems()
            val attachments = appDataBase.scheduleDao().getAllAttachments().first()
            
            val profile = appDataBase.profileDao().getProfile()
            val projectPlans = appDataBase.projectPlanDao().getAllProjectPlans().first()
            val myProjects = appDataBase.myProjectDao().getAllMyProjects().first()

            val payload = BackupPayload(
                notes = notes,
                links = links,
                files = files,
                fileFolders = fileFolders,
                linkFolders = linkFolders,
                media = media,
                libraryItems = libraryItems,
                categories = categories,
                schedules = schedules,
                attachments = attachments,
                profile = profile,
                projectPlans = projectPlans,
                myProjects = myProjects
            )

            Log.d(TAG, "Gathered workspace tables data sizes: " +
                "notes=${notes.size}, links=${links.size}, files=${files.size}, " +
                "folders(file/link)=${fileFolders.size}/${linkFolders.size}, media=${media.size}, " +
                "library=${libraryItems.size}, categories=${categories.size}, schedules=${schedules.size}, " +
                "attachments=${attachments.size}, profile=${if (profile != null) "exists" else "null"}, " +
                "projectPlans=${projectPlans.size}, myProjects=${myProjects.size}")

            // 2. Serialize to JSON
            val json = backupAdapter.toJson(payload)
            Log.d(TAG, "Serialized workspace payload to JSON: ${json.length} characters")

            // 3. Encrypt JSON using a key derived from the user's UID
            val encryptedData = EncryptionUtils.encrypt(json, uid)
            Log.d(TAG, "Encrypted workspace JSON: ${encryptedData.length} characters")

            // 4. Upload to Firestore
            val backupDoc = mapOf(
                "backupData" to encryptedData,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid)
                .collection("workspace").document("backup")
                .set(backupDoc)
                .await()

            Log.d(TAG, "✅ Backup completed successfully for uid: $uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Backup failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Downloads, decrypts, and restores the personal workspace backup from Firestore.
     */
    suspend fun performRestore(): Result<Unit> = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext Result.failure(Exception("No user signed in"))
        Log.d(TAG, "Starting restore for user: $uid")
        try {
            val doc = firestore.collection("users").document(uid)
                .collection("workspace").document("backup")
                .get()
                .await()

            if (!doc.exists()) {
                Log.d(TAG, "No backup found in the cloud for uid: $uid")
                return@withContext Result.success(Unit)
            }

            val encryptedData = doc.getString("backupData") ?: throw Exception("Backup data is empty")
            Log.d(TAG, "Retrieved encrypted backup payload of size: ${encryptedData.length} characters. Last updated: ${doc.getLong("updatedAt")}")
            
            // 1. Decrypt
            val json = EncryptionUtils.decrypt(encryptedData, uid)
            Log.d(TAG, "Decrypted workspace JSON successfully: ${json.length} characters")

            // 2. Deserialize
            val payload = backupAdapter.fromJson(json) ?: throw Exception("Failed to parse backup JSON")
            Log.d(TAG, "Deserialized backup payload successfully: notes=${payload.notes.size}, schedules=${payload.schedules.size}, etc.")

            // 3. Restore in a Room transaction
            appDataBase.withTransaction {
                // We clear and insert everything
                appDataBase.clearAllTables() // Clear everything including cache

                // Insert profile
                payload.profile?.let {
                    appDataBase.profileDao().insertOrUpdate(it)
                }

                // Insert content
                payload.fileFolders.forEach {
                    appDataBase.contentDao().insertFileFolder(it)
                }
                payload.linkFolders.forEach {
                    appDataBase.contentDao().insertLinkFolder(it)
                }
                payload.notes.forEach {
                    appDataBase.contentDao().insertNote(it)
                }
                payload.links.forEach {
                    appDataBase.contentDao().insertLink(it)
                }
                payload.files.forEach {
                    appDataBase.contentDao().insertFile(it)
                }

                // Insert media
                payload.media.forEach {
                    appDataBase.mediaDao().insertMedia(it)
                }

                // Insert library
                payload.libraryItems.forEach {
                    appDataBase.libraryItemDao().insertLibraryItem(it)
                }

                // Insert schedule
                payload.categories.forEach {
                    appDataBase.scheduleDao().insertCategory(it)
                }
                payload.schedules.forEach {
                    appDataBase.scheduleDao().insertScheduleItem(it)
                }
                payload.attachments.forEach {
                    appDataBase.scheduleDao().insertAttachment(it)
                }

                // Insert projects
                payload.projectPlans.forEach {
                    appDataBase.projectPlanDao().insertProjectPlan(it)
                }
                payload.myProjects.forEach {
                    appDataBase.myProjectDao().insertMyProject(it)
                }
            }

            Log.d(TAG, "✅ Restore completed successfully for uid: $uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Restore failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Clears all local Room database tables to ensure per-user data isolation.
     */
    suspend fun clearLocalData() = withContext(Dispatchers.IO) {
        Log.d(TAG, "Clearing all local database tables for user sign-out")
        try {
            appDataBase.clearAllTables()
            com.nhbhuiyan.nestify.data.di.DatabaseKeys.clearKey(context)
            Log.d(TAG, "✅ Local database cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to clear local database: ${e.message}", e)
        }
    }
}

/**
 * Secure AES-GCM encryption helper.
 */
object EncryptionUtils {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val TAG_LENGTH = 128
    private const val IV_LENGTH = 12

    private val APP_SECRET = "NestifyCUET_2026_SecureSaltVal".toByteArray(Charsets.UTF_8)

    private fun deriveKey(uid: String): SecretKeySpec {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        digest.update(uid.toByteArray(Charsets.UTF_8))
        val keyBytes = digest.digest(APP_SECRET)
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    fun encrypt(data: String, key: String): String {
        android.util.Log.d("EncryptionUtils", "Encrypting payload of size: ${data.length} characters for key suffix: ${key.takeLast(4)}")
        val secretKey = deriveKey(key)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(IV_LENGTH)
        SecureRandom().nextBytes(iv)
        val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
        val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
        
        val encryptedString = Base64.getEncoder().encodeToString(combined)
        android.util.Log.d("EncryptionUtils", "Encryption successful. Combined ciphertext + IV size: ${combined.size} bytes. Result length: ${encryptedString.length}")
        return encryptedString
    }

    fun decrypt(encryptedData: String, key: String): String {
        android.util.Log.d("EncryptionUtils", "Decrypting payload of size: ${encryptedData.length} characters for key suffix: ${key.takeLast(4)}")
        val secretKey = deriveKey(key)
        val combined = Base64.getDecoder().decode(encryptedData)
        if (combined.size < IV_LENGTH) {
            throw IllegalArgumentException("Invalid encrypted data size")
        }
        val iv = ByteArray(IV_LENGTH)
        System.arraycopy(combined, 0, iv, 0, iv.size)
        
        val ciphertext = ByteArray(combined.size - iv.size)
        System.arraycopy(combined, iv.size, ciphertext, 0, ciphertext.size)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
        val decryptedBytes = cipher.doFinal(ciphertext)
        val decryptedString = String(decryptedBytes, Charsets.UTF_8)
        android.util.Log.d("EncryptionUtils", "Decryption successful. Decrypted plaintext size: ${decryptedBytes.size} bytes. String length: ${decryptedString.length}")
        return decryptedString
    }
}
