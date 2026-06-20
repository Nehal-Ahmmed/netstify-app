package com.nhbhuiyan.nestify.data.local.Dao

import androidx.room.*
import com.nhbhuiyan.nestify.data.local.entity.ClassTestMarkEntity
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.SyllabusTopicEntity
import com.nhbhuiyan.nestify.data.local.entity.TermReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamPlannerDao {
    // Subject Queries
    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE level = :level AND term = :term")
    fun getSubjectsByTerm(level: Int, term: Int): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Long): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    // Class Test Marks Queries
    @Query("SELECT * FROM class_test_marks WHERE subjectId = :subjectId")
    fun getCTMarksForSubject(subjectId: Long): Flow<List<ClassTestMarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCTMark(mark: ClassTestMarkEntity)

    @Query("DELETE FROM class_test_marks WHERE subjectId = :subjectId")
    suspend fun deleteCTMarksForSubject(subjectId: Long)

    // Syllabus Topics Queries
    @Query("SELECT * FROM syllabus_topics WHERE subjectId = :subjectId")
    fun getSyllabusTopicsForSubject(subjectId: Long): Flow<List<SyllabusTopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabusTopic(topic: SyllabusTopicEntity)

    @Update
    suspend fun updateSyllabusTopic(topic: SyllabusTopicEntity)

    @Delete
    suspend fun deleteSyllabusTopic(topic: SyllabusTopicEntity)

    // Term Reports Queries
    @Query("SELECT * FROM term_reports")
    fun getAllTermReports(): Flow<List<TermReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTermReport(report: TermReportEntity)

    @Delete
    suspend fun deleteTermReport(report: TermReportEntity)
}
