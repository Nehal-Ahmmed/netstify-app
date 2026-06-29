package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.domain.model.AcademicPYQ
import com.nhbhuiyan.nestify.domain.model.AcademicSubject
import com.nhbhuiyan.nestify.domain.model.AcademicTopic
import kotlinx.coroutines.flow.Flow

interface DepartmentRepository {
    fun getSubjects(deptCode: String, semesterId: String): Flow<List<AcademicSubject>>
    fun getTopics(deptCode: String, semesterId: String, subjectCode: String): Flow<List<AcademicTopic>>
    fun getPyqs(deptCode: String, semesterId: String, subjectCode: String, topicId: String): Flow<List<AcademicPYQ>>
    
    suspend fun saveSubject(deptCode: String, semesterId: String, subject: AcademicSubject): Result<Unit>
    suspend fun deleteSubject(deptCode: String, semesterId: String, subjectCode: String): Result<Unit>
    suspend fun saveTopic(deptCode: String, semesterId: String, subjectCode: String, topic: AcademicTopic): Result<Unit>
    suspend fun deleteTopic(deptCode: String, semesterId: String, subjectCode: String, title: String, section: String): Result<Unit>
    suspend fun savePyq(deptCode: String, semesterId: String, subjectCode: String, topicId: String, pyq: AcademicPYQ): Result<Unit>
}
