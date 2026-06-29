package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.data.local.entity.ClassTestMarkEntity
import com.nhbhuiyan.nestify.data.local.entity.PYQEntity
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.SyllabusTopicEntity
import com.nhbhuiyan.nestify.data.local.entity.TermReportEntity
import kotlinx.coroutines.flow.Flow

interface ExamDataRepository {
    fun getSubjectsFlow(deptCode: String, semesterId: String): Flow<List<SubjectEntity>>
    fun getTopicsFlow(deptCode: String, semesterId: String, subjectCode: String, subjectId: Long): Flow<List<SyllabusTopicEntity>>
    fun getPyqsFlow(deptCode: String, semesterId: String, subjectCode: String, topicFirestoreId: String, topicLocalId: Long): Flow<List<PYQEntity>>
    fun getCTMarksFlow(subjectId: Long): Flow<List<ClassTestMarkEntity>>
    fun getSharedCTMarks(groupId: String, semesterId: String, studentId: String): Flow<Map<String, List<Float>>>
    
    suspend fun saveCTMarks(groupId: String, semesterId: String, studentId: String, marks: Map<String, List<Float>>): Result<Unit>
    suspend fun saveSubject(deptCode: String, semesterId: String, subject: SubjectEntity): Result<Unit>
    suspend fun deleteSubject(deptCode: String, semesterId: String, subjectCode: String): Result<Unit>
    suspend fun saveTopic(deptCode: String, semesterId: String, subjectCode: String, topic: com.nhbhuiyan.nestify.domain.model.AcademicTopic): Result<Unit>
    suspend fun deleteTopic(deptCode: String, semesterId: String, subjectCode: String, title: String, section: String): Result<Unit>
    suspend fun updateLocalGradeAndAttendance(subjectId: Long, finalGrade: String, attendance: Float): Result<Unit>
    suspend fun updateLocalTopicProgress(topicId: Long, isCompleted: Boolean, isRevised: Boolean, priority: Int): Result<Unit>
    
    // Term reports (purely local Room)
    fun getTermReports(): Flow<List<TermReportEntity>>
    suspend fun saveTermReport(report: TermReportEntity): Result<Unit>
    suspend fun deleteTermReport(report: TermReportEntity): Result<Unit>
}
