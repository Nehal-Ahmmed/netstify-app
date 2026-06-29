package com.nhbhuiyan.nestify.domain.model

data class ClassGroup(
    val groupId: String,
    val currentLevel: Int,
    val currentTerm: Int,
    val crList: List<String>,
    val adminList: List<String>
)

data class ClassGroupRosterItem(
    val rollNumber: String,
    val uid: String,
    val displayName: String
)
