package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.TypeConverter
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
    fun fromDay(day: Day) : String{
        return day.name
    }

    @TypeConverter
    fun toDay(name: String) : Day{
        return Day.valueOf(name)
    }

    @TypeConverter
    fun fromContent(content: Content) : String{
        return "${content.startClass.toEpochMilliseconds()},${content.endClass.toEpochMilliseconds()},${content.subject},${content.teacher}"
    }

    @TypeConverter
    fun toContent(data: String) : Content {
        val list = data.split(",")
        return Content(
            startClass = Instant.fromEpochMilliseconds(list[0].toLong()),
            endClass = Instant.fromEpochMilliseconds(list[0].toLong()),
            subject = list[1],
            teacher = list[2]
        )
    }
}