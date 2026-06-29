package com.nhbhuiyan.nestify.domain.model

data class UserSession(
    val uid: String,
    val email: String,
    val role: UserRole,
    val classGroupId: String,
    val departmentCode: String,
    val rollNumber: String,
    val displayName: String,
    val photoUrl: String,
    val linkedEmails: List<String> = emptyList(),
    val providers: List<String> = emptyList()
)
