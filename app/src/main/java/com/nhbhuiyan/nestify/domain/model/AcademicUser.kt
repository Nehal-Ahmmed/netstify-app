package com.nhbhuiyan.nestify.domain.model

data class AcademicUser(
    val uid: String,
    val email: String,
    val role: UserRole,
    val classGroupId: String,
    val departmentCode: String,
    val rollNumber: String,
    val displayName: String,
    val photoUrl: String,
    val studentId: String,
    val batchYear: String,
    val pendingReview: Boolean
)
