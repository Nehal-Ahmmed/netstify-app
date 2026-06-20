package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.TypeConverter
import com.nhbhuiyan.nestify.domain.model.AttachmentType
import com.nhbhuiyan.nestify.domain.model.RepeatStrategy
import com.nhbhuiyan.nestify.domain.model.ReminderType
import kotlinx.datetime.Instant

class Converters {
    @TypeConverter
    fun fromInstant (instant: Instant): Long = instant.toEpochMilliseconds()

    @TypeConverter
    fun toInstant (millis : Long) : Instant = Instant.fromEpochMilliseconds(millis)

    @TypeConverter
    fun fromStringList(list: List<String>) : String = list.joinToString(",")

    @TypeConverter
    fun toStringList(data : String) : List<String> = if(data.isEmpty()) emptyList() else data.split(",")

    @TypeConverter
    fun fromIntList(list: List<Int>): String = list.joinToString(",")

    @TypeConverter
    fun toIntList(data: String): List<Int> = if (data.isEmpty()) emptyList() else data.split(",").map { it.toInt() }

    @TypeConverter
    fun fromRepeatStrategy(value: RepeatStrategy) = value.name
    @TypeConverter
    fun toRepeatStrategy(value: String) = RepeatStrategy.valueOf(value)

    @TypeConverter
    fun fromReminderType(value: ReminderType) = value.name
    @TypeConverter
    fun toReminderType(value: String) = ReminderType.valueOf(value)

    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
    @TypeConverter
    fun toAttachmentType(value: String) = AttachmentType.valueOf(value)

    @TypeConverter
    fun fromMediaType(value: MediaType) = value.name
    @TypeConverter
    fun toMediaType(value: String) = MediaType.valueOf(value)

    @TypeConverter
    fun fromGalleryCategory(value: GalleryCategory) = value.name
    @TypeConverter
    fun toGalleryCategory(value: String) = GalleryCategory.valueOf(value)

    @TypeConverter
    fun fromLibraryItemStatus(value: LibraryItemStatus) = value.name
    @TypeConverter
    fun toLibraryItemStatus(value: String) = LibraryItemStatus.valueOf(value)

    @TypeConverter
    fun fromLibraryItemType(value: LibraryItemType) = value.name
    @TypeConverter
    fun toLibraryItemType(value: String) = LibraryItemType.valueOf(value)

    @TypeConverter
    fun fromStringMap(map: Map<String, String>): String {
        val jsonObject = org.json.JSONObject()
        for ((key, value) in map) {
            jsonObject.put(key, value)
        }
        return jsonObject.toString()
    }

    @TypeConverter
    fun toStringMap(data: String): Map<String, String> {
        if (data.isEmpty()) return emptyMap()
        val map = mutableMapOf<String, String>()
        try {
            val jsonObject = org.json.JSONObject(data)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = jsonObject.getString(key)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }
}