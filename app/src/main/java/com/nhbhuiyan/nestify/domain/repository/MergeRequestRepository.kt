package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.domain.model.MergeRequest
import kotlinx.coroutines.flow.Flow

interface MergeRequestRepository {
    suspend fun submitMergeRequest(groupId: String, mr: MergeRequest): Result<Unit>
    fun getMergeRequests(groupId: String): Flow<List<MergeRequest>>
    suspend fun resolveMergeRequest(groupId: String, mrId: String, status: String, reviewerUid: String, reviewNote: String?): Result<Unit>
}
