package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.domain.model.ClassGroup
import com.nhbhuiyan.nestify.domain.model.ClassGroupRosterItem
import kotlinx.coroutines.flow.Flow

interface ClassGroupRepository {
    fun getClassGroup(groupId: String): Flow<ClassGroup?>
    fun getClassRoster(groupId: String): Flow<List<ClassGroupRosterItem>>
    suspend fun updateClassSettings(groupId: String, level: Int, term: Int): Result<Unit>
}
