package com.nhbhuiyan.nestify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhbhuiyan.nestify.data.local.Dao.ExamPlannerDao
import com.nhbhuiyan.nestify.data.local.entity.ClassTestMarkEntity
import com.nhbhuiyan.nestify.data.local.entity.PYQEntity
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.SyllabusTopicEntity
import com.nhbhuiyan.nestify.data.local.entity.TermReportEntity
import com.nhbhuiyan.nestify.domain.repository.DepartmentRepository
import com.nhbhuiyan.nestify.domain.repository.ExamDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamDataRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val departmentRepository: DepartmentRepository,
    private val examPlannerDao: ExamPlannerDao
) : ExamDataRepository {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun getSubjectsFlow(deptCode: String, semesterId: String): Flow<List<SubjectEntity>> {
        android.util.Log.d("ExamDataRepository", "getSubjectsFlow called for deptCode=$deptCode, semesterId=$semesterId")
        val level = when {
            semesterId.startsWith("L1") -> 1
            semesterId.startsWith("L2") -> 2
            semesterId.startsWith("L3") -> 3
            else -> 4
        }
        val term = when {
            semesterId.endsWith("T1") -> 1
            else -> 2
        }

        return combine(
            departmentRepository.getSubjects(deptCode, semesterId),
            examPlannerDao.getAllSubjects()
        ) { sharedList, localList ->
            android.util.Log.d("ExamDataRepository", "getSubjectsFlow combined: Firestore sharedList size=${sharedList.size}, Local localList size=${localList.size}")
            sharedList.map { shared ->
                val local = localList.firstOrNull { it.code == shared.code }
                if (local == null) {
                    // Create local placeholder in Room asynchronously to store personal grades/attendance
                    scope.launch {
                        examPlannerDao.insertSubject(
                            SubjectEntity(
                                code = shared.code,
                                name = shared.name,
                                credits = shared.credits,
                                level = level,
                                term = term,
                                examDate = shared.examDate,
                                finalGrade = "Pending",
                                attendanceMarks = 0f
                            )
                        )
                    }
                    SubjectEntity(
                        id = 0,
                        code = shared.code,
                        name = shared.name,
                        credits = shared.credits,
                        level = level,
                        term = term,
                        examDate = shared.examDate
                    )
                } else {
                    local.copy(
                        name = shared.name,
                        credits = shared.credits,
                        examDate = shared.examDate
                    )
                }
            }
        }
    }

    override fun getTopicsFlow(
        deptCode: String,
        semesterId: String,
        subjectCode: String,
        subjectId: Long
    ): Flow<List<SyllabusTopicEntity>> {
        return combine(
            departmentRepository.getTopics(deptCode, semesterId, subjectCode),
            examPlannerDao.getSyllabusTopicsForSubject(subjectId)
        ) { sharedList, localList ->
            sharedList.map { shared ->
                val local = localList.firstOrNull { it.title == shared.title && it.section == shared.section }
                if (local == null) {
                    scope.launch {
                        examPlannerDao.insertSyllabusTopic(
                            SyllabusTopicEntity(
                                subjectId = subjectId,
                                section = shared.section,
                                title = shared.title,
                                isCompleted = false,
                                isRevised = false,
                                priority = 3,
                                firestoreId = shared.id
                            )
                        )
                    }
                    SyllabusTopicEntity(
                        id = 0,
                        subjectId = subjectId,
                        section = shared.section,
                        title = shared.title,
                        firestoreId = shared.id
                    )
                } else {
                    if (local.firestoreId != shared.id) {
                        scope.launch {
                            examPlannerDao.updateSyllabusTopic(local.copy(firestoreId = shared.id))
                        }
                        local.copy(firestoreId = shared.id)
                    } else {
                        local
                    }
                }
            }
        }
    }

    override fun getPyqsFlow(
        deptCode: String,
        semesterId: String,
        subjectCode: String,
        topicFirestoreId: String,
        topicLocalId: Long
    ): Flow<List<PYQEntity>> {
        return combine(
            departmentRepository.getPyqs(deptCode, semesterId, subjectCode, topicFirestoreId),
            examPlannerDao.getPYQsForTopic(topicLocalId)
        ) { sharedList, localList ->
            sharedList.map { shared ->
                val local = localList.firstOrNull { it.firestoreId == shared.id }
                if (local == null) {
                    val newEntity = PYQEntity(
                        topicId = topicLocalId,
                        questionText = shared.questionText,
                        questionImagePath = shared.questionImagePath,
                        answerText = shared.answerText,
                        answerImagePath = shared.answerImagePath,
                        repeatCount = shared.repeatCount,
                        yearsSeen = shared.yearsSeen.joinToString(", "),
                        marks = shared.marks,
                        firestoreId = shared.id
                    )
                    scope.launch {
                        examPlannerDao.insertPYQ(newEntity)
                    }
                    newEntity
                } else {
                    val hasChanged = local.questionText != shared.questionText ||
                            local.questionImagePath != shared.questionImagePath ||
                            local.answerText != shared.answerText ||
                            local.answerImagePath != shared.answerImagePath ||
                            local.repeatCount != shared.repeatCount ||
                            local.yearsSeen != shared.yearsSeen.joinToString(", ") ||
                            local.marks != shared.marks

                    if (hasChanged) {
                        val updated = local.copy(
                            questionText = shared.questionText,
                            questionImagePath = shared.questionImagePath,
                            answerText = shared.answerText,
                            answerImagePath = shared.answerImagePath,
                            repeatCount = shared.repeatCount,
                            yearsSeen = shared.yearsSeen.joinToString(", "),
                            marks = shared.marks
                        )
                        scope.launch {
                            examPlannerDao.updatePYQ(updated)
                        }
                        updated
                    } else {
                        local
                    }
                }
            }
        }
    }

    override fun getCTMarksFlow(subjectId: Long): Flow<List<ClassTestMarkEntity>> {
        return examPlannerDao.getCTMarksForSubject(subjectId)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getSharedCTMarks(
        groupId: String,
        semesterId: String,
        studentId: String
    ): Flow<Map<String, List<Float>>> = callbackFlow {
        val docRef = firestore.collection("classGroups").document(groupId)
            .collection("ctMarks").document(semesterId)
            .collection(studentId).document("marks")

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val resultMap = mutableMapOf<String, List<Float>>()
            if (snapshot != null && snapshot.exists()) {
                val data = snapshot.data ?: emptyMap()
                data.forEach { (subjectCode, value) ->
                    val map = value as? Map<String, Any>
                    if (map != null) {
                        val ct1 = (map["ct1"] as? Number)?.toFloat() ?: 0f
                        val ct2 = (map["ct2"] as? Number)?.toFloat() ?: 0f
                        val ct3 = (map["ct3"] as? Number)?.toFloat() ?: 0f
                        val ct4 = (map["ct4"] as? Number)?.toFloat() ?: 0f
                        val attendance = (map["attendance"] as? Number)?.toFloat() ?: 0f
                        resultMap[subjectCode] = listOf(ct1, ct2, ct3, ct4, attendance)
                    }
                }
            }
            trySend(resultMap)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun saveCTMarks(
        groupId: String,
        semesterId: String,
        studentId: String,
        marks: Map<String, List<Float>>
    ): Result<Unit> {
        return try {
            val docRef = firestore.collection("classGroups").document(groupId)
                .collection("ctMarks").document(semesterId)
                .collection(studentId).document("marks")

            val payload = mutableMapOf<String, Any>()
            marks.forEach { (subjectCode, list) ->
                if (list.size >= 5) {
                    payload[subjectCode] = mapOf(
                        "ct1" to list[0],
                        "ct2" to list[1],
                        "ct3" to list[2],
                        "ct4" to list[3],
                        "attendance" to list[4]
                    )
                }
            }
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLocalGradeAndAttendance(
        subjectId: Long,
        finalGrade: String,
        attendance: Float
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = examPlannerDao.getSubjectById(subjectId)
            if (existing != null) {
                examPlannerDao.updateSubject(
                    existing.copy(
                        finalGrade = finalGrade,
                        attendanceMarks = attendance
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLocalTopicProgress(
        topicId: Long,
        isCompleted: Boolean,
        isRevised: Boolean,
        priority: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = examPlannerDao.getSyllabusTopicById(topicId)
            if (existing != null) {
                examPlannerDao.updateSyllabusTopic(
                    existing.copy(
                        isCompleted = isCompleted,
                        isRevised = isRevised,
                        priority = priority
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveSubject(
        deptCode: String,
        semesterId: String,
        subject: SubjectEntity
    ): Result<Unit> {
        val academicSubject = com.nhbhuiyan.nestify.domain.model.AcademicSubject(
            code = subject.code,
            name = subject.name,
            credits = subject.credits,
            examDate = subject.examDate
        )
        return departmentRepository.saveSubject(deptCode, semesterId, academicSubject)
    }

    override suspend fun deleteSubject(
        deptCode: String,
        semesterId: String,
        subjectCode: String
    ): Result<Unit> {
        return departmentRepository.deleteSubject(deptCode, semesterId, subjectCode)
    }

    override suspend fun saveTopic(
        deptCode: String,
        semesterId: String,
        subjectCode: String,
        topic: com.nhbhuiyan.nestify.domain.model.AcademicTopic
    ): Result<Unit> {
        return departmentRepository.saveTopic(deptCode, semesterId, subjectCode, topic)
    }

    override suspend fun deleteTopic(
        deptCode: String,
        semesterId: String,
        subjectCode: String,
        title: String,
        section: String
    ): Result<Unit> {
        return departmentRepository.deleteTopic(deptCode, semesterId, subjectCode, title, section)
    }

    override fun getTermReports(): Flow<List<TermReportEntity>> {
        return examPlannerDao.getAllTermReports()
    }

    override suspend fun saveTermReport(report: TermReportEntity): Result<Unit> {
        return try {
            examPlannerDao.insertTermReport(report)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTermReport(report: TermReportEntity): Result<Unit> {
        return try {
            examPlannerDao.deleteTermReport(report)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
