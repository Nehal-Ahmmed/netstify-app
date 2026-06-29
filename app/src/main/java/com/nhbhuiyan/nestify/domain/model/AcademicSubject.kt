package com.nhbhuiyan.nestify.domain.model

data class AcademicSubject(
    val code: String,
    val name: String,
    val credits: Float,
    val examDate: String = ""
)

data class AcademicTopic(
    val id: String,
    val subjectCode: String,
    val section: String, // "A" or "B"
    val title: String
)

data class AcademicPYQ(
    val id: String,
    val topicId: String,
    val questionText: String? = null,
    val answerText: String? = null,
    val questionImagePath: String? = null,
    val answerImagePath: String? = null,
    val repeatCount: Int = 1,
    val yearsSeen: List<String> = emptyList(),
    val marks: String? = null,
    val contributedBy: String? = null
)
