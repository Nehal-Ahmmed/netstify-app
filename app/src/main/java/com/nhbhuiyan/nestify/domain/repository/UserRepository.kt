package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.domain.model.AcademicUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAcademicUser(uid: String): Flow<AcademicUser?>
    suspend fun updateAcademicUser(uid: String, displayName: String, photoUrl: String): Result<Unit>
    suspend fun updateAcademicUserRole(uid: String, role: String): Result<Unit>
}
