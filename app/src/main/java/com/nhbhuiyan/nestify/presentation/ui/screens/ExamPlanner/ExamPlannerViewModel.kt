package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.data.local.Dao.ExamPlannerDao
import com.nhbhuiyan.nestify.data.local.entity.ClassTestMarkEntity
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.SyllabusTopicEntity
import com.nhbhuiyan.nestify.data.local.entity.TermReportEntity
import com.nhbhuiyan.nestify.domain.manager.UserSessionManager
import com.nhbhuiyan.nestify.domain.repository.ExamDataRepository
import com.nhbhuiyan.nestify.domain.repository.SettingsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.nhbhuiyan.nestify.domain.repository.ClassGroupRepository
import com.nhbhuiyan.nestify.domain.model.ClassGroupRosterItem
import com.nhbhuiyan.nestify.data.local.AppDataBase
import javax.inject.Inject

@HiltViewModel
class ExamPlannerViewModel @Inject constructor(
    private val examDataRepository: ExamDataRepository,
    private val settingsRepo: SettingsRepo,
    private val sessionManager: UserSessionManager,
    private val classGroupRepository: ClassGroupRepository,
    val appDataBase: AppDataBase,
    private val examPlannerDao: ExamPlannerDao // Retained for backup/restore procedures
) : ViewModel() {

    val sessionFlow = sessionManager.sessionFlow

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val classRoster: StateFlow<List<ClassGroupRosterItem>> = sessionFlow.flatMapLatest { session ->
        if (session != null) {
            classGroupRepository.getClassRoster(session.classGroupId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Subjects list flow
    private val _subjects = MutableStateFlow<List<SubjectEntity>>(emptyList())
    val subjects: StateFlow<List<SubjectEntity>> = _subjects.asStateFlow()

    // Class test marks map: subjectId -> list of marks
    private val _classTestMarks = MutableStateFlow<Map<Long, List<ClassTestMarkEntity>>>(emptyMap())
    val classTestMarks: StateFlow<Map<Long, List<ClassTestMarkEntity>>> = _classTestMarks.asStateFlow()

    // Syllabus topics map: subjectId -> list of topics
    private val _syllabusTopics = MutableStateFlow<Map<Long, List<SyllabusTopicEntity>>>(emptyMap())
    val syllabusTopics: StateFlow<Map<Long, List<SyllabusTopicEntity>>> = _syllabusTopics.asStateFlow()

    // Term reports list flow
    private val _termReports = MutableStateFlow<List<TermReportEntity>>(emptyList())
    val termReports: StateFlow<List<TermReportEntity>> = _termReports.asStateFlow()

    // Selected student ID for CR to edit/view marks (defaults to empty meaning own marks)
    private val _selectedStudentIdForCT = MutableStateFlow("")
    val selectedStudentIdForCT: StateFlow<String> = _selectedStudentIdForCT.asStateFlow()

    // PYQs map: topicId -> list of PYQEntity
    private val _pyqs = MutableStateFlow<Map<Long, List<com.nhbhuiyan.nestify.data.local.entity.PYQEntity>>>(emptyMap())
    val pyqs: StateFlow<Map<Long, List<com.nhbhuiyan.nestify.data.local.entity.PYQEntity>>> = _pyqs.asStateFlow()

    private val pyqJobs = mutableMapOf<Long, kotlinx.coroutines.Job>()

    private data class PyqSyncInfo(
        val deptCode: String,
        val semesterId: String,
        val subjectCode: String,
        val topicFirestoreId: String
    )

    fun loadPYQsForTopic(topicId: Long) {
        if (pyqJobs.containsKey(topicId)) return
        val job = viewModelScope.launch {
            // First, collect the local Room list immediately so it shows up instantly if cached
            launch {
                examPlannerDao.getPYQsForTopic(topicId).collect { list ->
                    _pyqs.value = _pyqs.value.toMutableMap().apply {
                        put(topicId, list)
                    }
                }
            }

            // Next, observe the session, subjects, and topics to trigger Firestore sync
            combine(
                sessionFlow,
                subjects,
                syllabusTopics
            ) { session, subjectsList, topicsMap ->
                if (session == null) return@combine null
                
                val topic = topicsMap.values.flatten().find { it.id == topicId }
                val firestoreId = topic?.firestoreId
                if (firestoreId.isNullOrEmpty()) return@combine null

                val subject = subjectsList.find { it.id == topic.subjectId } ?: return@combine null
                val semesterId = "L${subject.level}T${subject.term}"

                PyqSyncInfo(session.departmentCode, semesterId, subject.code, firestoreId)
            }.collectLatest { info ->
                if (info != null) {
                    examDataRepository.getPyqsFlow(
                        deptCode = info.deptCode,
                        semesterId = info.semesterId,
                        subjectCode = info.subjectCode,
                        topicFirestoreId = info.topicFirestoreId,
                        topicLocalId = topicId
                    ).collectLatest { list ->
                        _pyqs.value = _pyqs.value.toMutableMap().apply {
                            put(topicId, list)
                        }
                    }
                }
            }
        }
        pyqJobs[topicId] = job
    }

    fun insertPYQ(pyq: com.nhbhuiyan.nestify.data.local.entity.PYQEntity) {
        viewModelScope.launch {
            examPlannerDao.insertPYQ(pyq)
        }
    }

    fun updatePYQ(pyq: com.nhbhuiyan.nestify.data.local.entity.PYQEntity) {
        viewModelScope.launch {
            examPlannerDao.updatePYQ(pyq)
        }
    }

    fun deletePYQ(pyq: com.nhbhuiyan.nestify.data.local.entity.PYQEntity) {
        viewModelScope.launch {
            examPlannerDao.deletePYQ(pyq)
        }
    }

    val defaultLevel: StateFlow<Int> = settingsRepo.defaultLevel
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 2
        )

    val defaultTerm: StateFlow<Int> = settingsRepo.defaultTerm
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 2
        )

    init {
        loadSubjects()
        loadTermReports()
        observeSharedCTMarks()
    }

    fun selectStudentIdForCT(studentId: String) {
        _selectedStudentIdForCT.value = studentId
    }

    fun setDefaultLevel(level: Int) {
        viewModelScope.launch {
            settingsRepo.setDefaultLevel(level)
        }
    }

    fun setDefaultTerm(term: Int) {
        viewModelScope.launch {
            settingsRepo.setDefaultTerm(term)
        }
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            sessionManager.sessionFlow.collectLatest { session ->
                if (session == null) {
                    _subjects.value = emptyList()
                    return@collectLatest
                }

                val dept = session.departmentCode
                val semesters = listOf("L1T1", "L1T2", "L2T1", "L2T2", "L3T1", "L3T2", "L4T1", "L4T2")

                val flows = semesters.map { sem ->
                    examDataRepository.getSubjectsFlow(dept, sem)
                }

                combine(flows) { arrays ->
                    arrays.flatMap { it }
                }.collectLatest { combinedList ->
                    _subjects.value = combinedList
                    combinedList.forEach { subject ->
                        loadSyllabusTopicsForSubject(dept, subject)
                    }
                }
            }
        }
    }

    private fun loadTermReports() {
        viewModelScope.launch {
            examDataRepository.getTermReports().collectLatest { reportList ->
                _termReports.value = reportList
            }
        }
    }

    private fun loadSyllabusTopicsForSubject(deptCode: String, subject: SubjectEntity) {
        viewModelScope.launch {
            val semesterId = "L${subject.level}T${subject.term}"
            examDataRepository.getTopicsFlow(deptCode, semesterId, subject.code, subject.id)
                .collectLatest { topics ->
                    val currentMap = _syllabusTopics.value.toMutableMap()
                    currentMap[subject.id] = topics
                    _syllabusTopics.value = currentMap
                }
        }
    }

    private fun observeSharedCTMarks() {
        viewModelScope.launch {
            combine(
                sessionManager.sessionFlow,
                _selectedStudentIdForCT,
                defaultLevel,
                defaultTerm
            ) { session, selectedId, level, term ->
                Triple(session, selectedId, "L${level}T${term}")
            }.collectLatest { (session, selectedId, semesterId) ->
                if (session == null) return@collectLatest
                val targetStudentId = selectedId.ifEmpty { session.rollNumber }

                examDataRepository.getSharedCTMarks(session.classGroupId, semesterId, targetStudentId)
                    .collectLatest { sharedMarksMap ->
                        val currentSubjects = _subjects.value
                        val mappedCT = mutableMapOf<Long, List<ClassTestMarkEntity>>()

                        currentSubjects.forEach { subject ->
                            val list = sharedMarksMap[subject.code] ?: listOf(0f, 0f, 0f, 0f, 0f)
                            val entities = (1..4).map { idx ->
                                ClassTestMarkEntity(
                                    subjectId = subject.id,
                                    testIndex = idx,
                                    marks = list.getOrElse(idx - 1) { 0f }
                                )
                            }
                            mappedCT[subject.id] = entities

                            val attendance = list.getOrElse(4) { 0f }
                            if (subject.attendanceMarks != attendance) {
                                viewModelScope.launch {
                                    examDataRepository.updateLocalGradeAndAttendance(
                                        subject.id,
                                        subject.finalGrade,
                                        attendance
                                    )
                                }
                            }
                        }
                        _classTestMarks.value = mappedCT
                    }
            }
        }
    }

    fun updateCTMark(mark: ClassTestMarkEntity) {
        viewModelScope.launch {
            val subject = _subjects.value.firstOrNull { it.id == mark.subjectId } ?: return@launch
            val session = sessionManager.sessionFlow.first() ?: return@launch
            val semesterId = "L${subject.level}T${subject.term}"
            val targetStudentId = _selectedStudentIdForCT.value.ifEmpty { session.rollNumber }

            val currentList = _classTestMarks.value[subject.id]?.toMutableList() ?: mutableListOf(
                ClassTestMarkEntity(subjectId = subject.id, testIndex = 1, marks = 0f),
                ClassTestMarkEntity(subjectId = subject.id, testIndex = 2, marks = 0f),
                ClassTestMarkEntity(subjectId = subject.id, testIndex = 3, marks = 0f),
                ClassTestMarkEntity(subjectId = subject.id, testIndex = 4, marks = 0f)
            )

            val existingIdx = currentList.indexOfFirst { it.testIndex == mark.testIndex }
            if (existingIdx != -1) {
                currentList[existingIdx] = mark
            }

            val floatList = listOf(
                currentList.firstOrNull { it.testIndex == 1 }?.marks ?: 0f,
                currentList.firstOrNull { it.testIndex == 2 }?.marks ?: 0f,
                currentList.firstOrNull { it.testIndex == 3 }?.marks ?: 0f,
                currentList.firstOrNull { it.testIndex == 4 }?.marks ?: 0f,
                subject.attendanceMarks
            )

            val currentSharedMap = mutableMapOf<String, List<Float>>()
            _subjects.value.forEach { sub ->
                val subList = _classTestMarks.value[sub.id]?.map { it.marks } ?: listOf(0f, 0f, 0f, 0f)
                val subAtt = sub.attendanceMarks
                currentSharedMap[sub.code] = subList + subAtt
            }
            currentSharedMap[subject.code] = floatList

            examDataRepository.saveCTMarks(session.classGroupId, semesterId, targetStudentId, currentSharedMap)
        }
    }

    fun updateSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            examDataRepository.updateLocalGradeAndAttendance(subject.id, subject.finalGrade, subject.attendanceMarks)

            val session = sessionManager.sessionFlow.first() ?: return@launch
            val semesterId = "L${subject.level}T${subject.term}"
            val targetStudentId = _selectedStudentIdForCT.value.ifEmpty { session.rollNumber }

            val currentList = _classTestMarks.value[subject.id]?.map { it.marks } ?: listOf(0f, 0f, 0f, 0f)
            val floatList = currentList + subject.attendanceMarks

            val currentSharedMap = mutableMapOf<String, List<Float>>()
            _subjects.value.forEach { sub ->
                val subList = _classTestMarks.value[sub.id]?.map { it.marks } ?: listOf(0f, 0f, 0f, 0f)
                val subAtt = if (sub.id == subject.id) subject.attendanceMarks else sub.attendanceMarks
                currentSharedMap[sub.code] = subList + subAtt
            }

            examDataRepository.saveCTMarks(session.classGroupId, semesterId, targetStudentId, currentSharedMap)
        }
    }

    fun updateSyllabusTopic(topic: SyllabusTopicEntity) {
        viewModelScope.launch {
            examDataRepository.updateLocalTopicProgress(topic.id, topic.isCompleted, topic.isRevised, topic.priority)
        }
    }

    fun insertSyllabusTopic(topic: SyllabusTopicEntity) {
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.first() ?: return@launch
            val subject = _subjects.value.firstOrNull { it.id == topic.subjectId } ?: return@launch
            val semesterId = "L${subject.level}T${subject.term}"
            
            val academicTopic = com.nhbhuiyan.nestify.domain.model.AcademicTopic(
                id = java.util.UUID.randomUUID().toString(),
                subjectCode = subject.code,
                section = topic.section,
                title = topic.title
            )
            examDataRepository.saveTopic(session.departmentCode, semesterId, subject.code, academicTopic)
        }
    }

    fun deleteSyllabusTopic(topic: SyllabusTopicEntity) {
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.first() ?: return@launch
            val subject = _subjects.value.firstOrNull { it.id == topic.subjectId } ?: return@launch
            val semesterId = "L${subject.level}T${subject.term}"
            
            examDataRepository.deleteTopic(session.departmentCode, semesterId, subject.code, topic.title, topic.section)
        }
    }

    fun insertSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.first() ?: return@launch
            val semesterId = "L${subject.level}T${subject.term}"
            examDataRepository.saveSubject(session.departmentCode, semesterId, subject)
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.first() ?: return@launch
            val semesterId = "L${subject.level}T${subject.term}"
            examDataRepository.deleteSubject(session.departmentCode, semesterId, subject.code)
        }
    }

    fun insertTermReport(report: TermReportEntity) {
        viewModelScope.launch {
            examDataRepository.saveTermReport(report)
        }
    }

    fun deleteTermReport(report: TermReportEntity) {
        viewModelScope.launch {
            examDataRepository.deleteTermReport(report)
        }
    }

    fun restoreSemesterArchive(
        archive: com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner.PersonalDataArchive,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Restore term report
                archive.termReport?.let { termRep ->
                    val termReportEntity = TermReportEntity(
                        level = termRep.level,
                        term = termRep.term,
                        gpa = termRep.gpa,
                        pdfLocalPath = termRep.pdfFileName ?: "",
                        timestamp = termRep.timestamp
                    )
                    examPlannerDao.insertTermReport(termReportEntity)
                }

                // Restore subjects and class test marks
                for (subArc in archive.subjects) {
                    val subjectEntity = SubjectEntity(
                        code = subArc.code,
                        name = subArc.name,
                        credits = subArc.credits,
                        level = subArc.level,
                        term = subArc.term,
                        examDate = subArc.examDate,
                        finalGrade = subArc.finalGrade,
                        attendanceMarks = subArc.attendanceMarks
                    )
                    val insertedId = examPlannerDao.insertSubject(subjectEntity)

                    // Restore class test marks for this subject
                    for (ctArc in subArc.classTestMarks) {
                        val ctEntity = ClassTestMarkEntity(
                            subjectId = insertedId,
                            testIndex = ctArc.testIndex,
                            marks = ctArc.marks
                        )
                        examPlannerDao.insertCTMark(ctEntity)
                    }
                }
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.localizedMessage ?: "Unknown restore error")
            }
        }
    }
}
