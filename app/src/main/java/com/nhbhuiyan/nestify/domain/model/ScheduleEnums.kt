package com.nhbhuiyan.nestify.domain.model

enum class RepeatStrategy {
    ONCE,
    DAILY,
    WEEKLY,
    MONTHLY,
    ANNUALLY,
    DATE_RANGE
}

enum class ReminderType {
    NONE,
    NOTIFICATION,
    ALARM,
    BOTH
}

enum class AttachmentType {
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT
}
