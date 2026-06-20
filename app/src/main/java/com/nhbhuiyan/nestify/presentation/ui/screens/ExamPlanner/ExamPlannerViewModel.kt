package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.data.local.Dao.ExamPlannerDao
import com.nhbhuiyan.nestify.data.local.entity.ClassTestMarkEntity
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.SyllabusTopicEntity
import com.nhbhuiyan.nestify.data.local.entity.TermReportEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExamPlannerViewModel @Inject constructor(
    private val examPlannerDao: ExamPlannerDao
) : ViewModel() {

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

    init {
        loadSubjects()
        loadTermReports()
        prepopulateDatabaseIfEmpty()
    }

    private fun prepopulateDatabaseIfEmpty() {
        viewModelScope.launch {
            try {
                // Check subjects
                val existingSubjects = examPlannerDao.getAllSubjects().first()
                if (existingSubjects.isEmpty()) {
                    insertSubject(SubjectEntity(code = "CSE-221", name = "Database Systems", credits = 3.0f, level = 2, term = 2, attendanceMarks = 28f))
                    insertSubject(SubjectEntity(code = "CSE-223", name = "Software Engineering", credits = 3.0f, level = 2, term = 2, attendanceMarks = 26f))
                    insertSubject(SubjectEntity(code = "CSE-225", name = "Microprocessors & Interfacing", credits = 3.0f, level = 2, term = 2, attendanceMarks = 25f))
                    insertSubject(SubjectEntity(code = "CSE-227", name = "Algorithms Design & Analysis", credits = 3.0f, level = 2, term = 2, attendanceMarks = 29f))
                    insertSubject(SubjectEntity(code = "CSE-226", name = "Microprocessors Lab", credits = 1.5f, level = 2, term = 2, attendanceMarks = 14f))
                }

                // Check reports
                val existingReports = examPlannerDao.getAllTermReports().first()
                if (existingReports.isEmpty()) {
                    insertTermReport(
                        TermReportEntity(
                            level = 1,
                            term = 1,
                            gpa = 3.61f,
                            pdfLocalPath = "cache/REP_001.pdf",
                            timestamp = System.currentTimeMillis() - 86400000L * 300L
                        )
                    )
                    insertTermReport(
                        TermReportEntity(
                            level = 1,
                            term = 2,
                            gpa = 3.71f,
                            pdfLocalPath = "cache/REP_002.pdf",
                            timestamp = System.currentTimeMillis() - 86400000L * 180L
                        )
                    )
                    insertTermReport(
                        TermReportEntity(
                            level = 2,
                            term = 1,
                            gpa = 3.87f,
                            pdfLocalPath = "cache/REP_003.pdf",
                            timestamp = System.currentTimeMillis() - 86400000L * 60L
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            examPlannerDao.getAllSubjects().collectLatest { subList ->
                _subjects.value = subList
                // Load CT and syllabus topics for each subject
                subList.forEach { subject ->
                    loadCTMarksForSubject(subject.id)
                    loadSyllabusTopicsForSubject(subject.id)
                }
            }
        }
    }

    private fun loadTermReports() {
        viewModelScope.launch {
            examPlannerDao.getAllTermReports().collectLatest { reportList ->
                _termReports.value = reportList
            }
        }
    }

    private fun loadCTMarksForSubject(subjectId: Long) {
        viewModelScope.launch {
            examPlannerDao.getCTMarksForSubject(subjectId).collectLatest { marks ->
                val currentMap = _classTestMarks.value.toMutableMap()
                currentMap[subjectId] = marks
                _classTestMarks.value = currentMap
            }
        }
    }

    private fun loadSyllabusTopicsForSubject(subjectId: Long) {
        viewModelScope.launch {
            examPlannerDao.getSyllabusTopicsForSubject(subjectId).collectLatest { topics ->
                val currentMap = _syllabusTopics.value.toMutableMap()
                currentMap[subjectId] = topics
                _syllabusTopics.value = currentMap
            }
        }
    }

    // Insert / update / delete operations
    fun insertSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            val subjectId = examPlannerDao.insertSubject(subject)
            // Pre-insert 4 empty CT marks for this subject
            for (i in 1..4) {
                examPlannerDao.insertCTMark(
                    ClassTestMarkEntity(
                        subjectId = subjectId,
                        testIndex = i,
                        marks = 0f
                    )
                )
            }
        }
    }

    fun updateSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            examPlannerDao.updateSubject(subject)
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            examPlannerDao.deleteSubject(subject)
        }
    }

    fun updateCTMark(mark: ClassTestMarkEntity) {
        viewModelScope.launch {
            examPlannerDao.insertCTMark(mark) // REPLACE will trigger update
        }
    }

    fun insertSyllabusTopic(topic: SyllabusTopicEntity) {
        viewModelScope.launch {
            examPlannerDao.insertSyllabusTopic(topic)
        }
    }

    fun updateSyllabusTopic(topic: SyllabusTopicEntity) {
        viewModelScope.launch {
            examPlannerDao.updateSyllabusTopic(topic)
        }
    }

    fun deleteSyllabusTopic(topic: SyllabusTopicEntity) {
        viewModelScope.launch {
            examPlannerDao.deleteSyllabusTopic(topic)
        }
    }

    fun insertTermReport(report: TermReportEntity) {
        viewModelScope.launch {
            examPlannerDao.insertTermReport(report)
        }
    }

    fun deleteTermReport(report: TermReportEntity) {
        viewModelScope.launch {
            examPlannerDao.deleteTermReport(report)
        }
    }
}
