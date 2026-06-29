package com.nhbhuiyan.nestify.domain.model

data class MergeRequest(
    val id: String,
    val type: String, // "subject", "topic", "pyq"
    val target: String, // target code/id key
    val semesterId: String,
    val departmentCode: String,
    val data: Map<String, Any?>,
    val submittedBy: String,
    val submitterName: String,
    val status: String = "pending", // "pending", "accepted", "rejected", "cancelled"
    val reviewedBy: String? = null,
    val reviewNote: String? = null,
    val submittedAt: Long? = null,
    val reviewedAt: Long? = null
)
