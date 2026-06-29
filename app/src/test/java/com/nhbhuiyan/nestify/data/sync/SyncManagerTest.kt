package com.nhbhuiyan.nestify.data.sync

import com.nhbhuiyan.nestify.data.local.entity.NoteEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SyncManagerTest {

    @Test
    fun testEncryptionDecryption_isCorrect() {
        val originalText = "Hello, Nestify! This is a secure database backup payload string."
        val uidKey = "test_user_uid_12345"

        // Encrypt
        val encrypted = EncryptionUtils.encrypt(originalText, uidKey)
        assertNotNull(encrypted)

        // Decrypt
        val decrypted = EncryptionUtils.decrypt(encrypted, uidKey)
        assertEquals(originalText, decrypted)
    }

    @Test
    fun testBackupPayloadSerialization_isLossless() {
        // Setup Moshi with Instant adapter
        val moshi = Moshi.Builder()
            .add(SyncManager.InstantAdapter())
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(BackupPayload::class.java)

        // Create a mock payload
        val mockNote = NoteEntity(
            id = 1,
            title = "Test Note",
            content = "This is a test content.",
            createdAt = Instant.parse("2026-06-28T12:00:00Z"),
            updatedAt = Instant.parse("2026-06-28T12:05:00Z"),
            isArchived = false,
            isBookmarked = true,
            tags = listOf("work", "nestify")
        )
        val originalPayload = BackupPayload(
            notes = listOf(mockNote),
            links = emptyList(),
            files = emptyList()
        )

        // Serialize
        val json = adapter.toJson(originalPayload)
        assertNotNull(json)

        // Deserialize
        val deserializedPayload = adapter.fromJson(json)
        assertNotNull(deserializedPayload)
        assertEquals(1, deserializedPayload!!.notes.size)

        val deserializedNote = deserializedPayload.notes[0]
        assertEquals(mockNote.id, deserializedNote.id)
        assertEquals(mockNote.title, deserializedNote.title)
        assertEquals(mockNote.content, deserializedNote.content)
        assertEquals(mockNote.createdAt, deserializedNote.createdAt)
        assertEquals(mockNote.updatedAt, deserializedNote.updatedAt)
        assertEquals(mockNote.isBookmarked, deserializedNote.isBookmarked)
        assertEquals(mockNote.tags, deserializedNote.tags)
    }
}
