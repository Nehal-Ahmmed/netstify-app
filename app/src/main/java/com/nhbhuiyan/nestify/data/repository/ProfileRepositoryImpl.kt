package com.nhbhuiyan.nestify.data.repository

import com.nhbhuiyan.nestify.data.local.Dao.ProfileDao
import com.nhbhuiyan.nestify.data.mapper.toProfileEntity
import com.nhbhuiyan.nestify.data.mapper.toUserProfile
import com.nhbhuiyan.nestify.domain.model.UserProfile
import com.nhbhuiyan.nestify.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileRepository {

    override fun getProfileFlow(): Flow<UserProfile?> {
        return profileDao.getProfileFlow().map { it?.toUserProfile() }
    }

    override suspend fun getProfile(): UserProfile? {
        return profileDao.getProfile()?.toUserProfile()
    }

    override suspend fun saveProfile(profile: UserProfile) {
        profileDao.insertOrUpdate(profile.toProfileEntity())
    }
}
