package com.nhbhuiyan.nestify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhbhuiyan.nestify.domain.model.AcademicPYQ
import com.nhbhuiyan.nestify.domain.model.AcademicSubject
import com.nhbhuiyan.nestify.domain.model.AcademicTopic
import android.util.Log
import com.nhbhuiyan.nestify.domain.repository.DepartmentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DepartmentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DepartmentRepository {

    private val TAG = "DepartmentRepo"

    override fun getSubjects(deptCode: String, semesterId: String): Flow<List<AcademicSubject>> = callbackFlow {
        Log.d(TAG, "getSubjects flow started for deptCode=$deptCode, semesterId=$semesterId")
        val query = firestore.collection("departments").document(deptCode)
            .collection("semesters").document(semesterId)
            .collection("subjects")
            
        val listener = query.addSnapshotListener { snapshot, error ->
            Log.d(TAG, "getSubjects snapshot listener triggered. Success=${snapshot != null}, error=${error?.message}")
            if (error != null) {
                Log.e(TAG, "getSubjects snapshot error: ${error.message}", error)
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.map { doc ->
                    AcademicSubject(
                        code = doc.id,
                        name = doc.getString("name") ?: "",
                        credits = doc.getDouble("credits")?.toFloat() ?: 0f,
                        examDate = doc.getString("examDate") ?: ""
                    )
                }
                trySend(list)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    override fun getTopics(deptCode: String, semesterId: String, subjectCode: String): Flow<List<AcademicTopic>> = callbackFlow {
        val query = firestore.collection("departments").document(deptCode)
            .collection("semesters").document(semesterId)
            .collection("subjects").document(subjectCode)
            .collection("topics")

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.map { doc ->
                    AcademicTopic(
                        id = doc.id,
                        subjectCode = subjectCode,
                        section = doc.getString("section") ?: "",
                        title = doc.getString("title") ?: ""
                    )
                }
                trySend(list)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getPyqs(deptCode: String, semesterId: String, subjectCode: String, topicId: String): Flow<List<AcademicPYQ>> = callbackFlow {
        val query = firestore.collection("departments").document(deptCode)
            .collection("semesters").document(semesterId)
            .collection("subjects").document(subjectCode)
            .collection("topics").document(topicId)
            .collection("pyqs")

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.map { doc ->
                    AcademicPYQ(
                        id = doc.id,
                        topicId = topicId,
                        questionText = doc.getString("questionText"),
                        answerText = doc.getString("answerText"),
                        questionImagePath = doc.getString("questionImagePath"),
                        answerImagePath = doc.getString("answerImagePath"),
                        repeatCount = doc.getLong("repeatCount")?.toInt() ?: 1,
                        yearsSeen = (doc.get("yearsSeen") as? List<String>) ?: emptyList(),
                        marks = doc.getString("marks"),
                        contributedBy = doc.getString("contributedBy")
                    )
                }
                trySend(list)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun saveSubject(deptCode: String, semesterId: String, subject: AcademicSubject): Result<Unit> {
        Log.d(TAG, "Saving subject ${subject.code} to $deptCode/$semesterId")
        return try {
            firestore.collection("departments").document(deptCode)
                .collection("semesters").document(semesterId)
                .collection("subjects").document(subject.code)
                .set(
                    mapOf(
                        "name" to subject.name,
                        "credits" to subject.credits,
                        "examDate" to subject.examDate
                    )
                ).await()
            Log.d(TAG, "✅ Subject ${subject.code} saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to save subject ${subject.code}: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteSubject(deptCode: String, semesterId: String, subjectCode: String): Result<Unit> {
        Log.d(TAG, "Deleting subject $subjectCode from $deptCode/$semesterId")
        return try {
            firestore.collection("departments").document(deptCode)
                .collection("semesters").document(semesterId)
                .collection("subjects").document(subjectCode)
                .delete().await()
            Log.d(TAG, "✅ Subject $subjectCode deleted")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to delete subject $subjectCode: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun saveTopic(deptCode: String, semesterId: String, subjectCode: String, topic: AcademicTopic): Result<Unit> {
        Log.d(TAG, "Saving topic '${topic.title}' to $deptCode/$semesterId/$subjectCode")
        return try {
            firestore.collection("departments").document(deptCode)
                .collection("semesters").document(semesterId)
                .collection("subjects").document(subjectCode)
                .collection("topics").document(topic.id)
                .set(
                    mapOf(
                        "section" to topic.section,
                        "title" to topic.title
                    )
                ).await()
            Log.d(TAG, "✅ Topic '${topic.title}' saved")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to save topic '${topic.title}': ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteTopic(
        deptCode: String,
        semesterId: String,
        subjectCode: String,
        title: String,
        section: String
    ): Result<Unit> {
        Log.d(TAG, "Deleting topic '$title' (section=$section) from $subjectCode")
        return try {
            val querySnapshot = firestore.collection("departments").document(deptCode)
                .collection("semesters").document(semesterId)
                .collection("subjects").document(subjectCode)
                .collection("topics")
                .whereEqualTo("title", title)
                .whereEqualTo("section", section)
                .get().await()

            for (doc in querySnapshot.documents) {
                doc.reference.delete().await()
            }
            Log.d(TAG, "✅ Deleted ${querySnapshot.size()} topic doc(s) matching '$title'")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to delete topic '$title': ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun savePyq(deptCode: String, semesterId: String, subjectCode: String, topicId: String, pyq: AcademicPYQ): Result<Unit> {
        Log.d(TAG, "Saving PYQ ${pyq.id} to $subjectCode/$topicId")
        return try {
            firestore.collection("departments").document(deptCode)
                .collection("semesters").document(semesterId)
                .collection("subjects").document(subjectCode)
                .collection("topics").document(topicId)
                .collection("pyqs").document(pyq.id)
                .set(
                    mapOf(
                        "questionText" to pyq.questionText,
                        "answerText" to pyq.answerText,
                        "questionImagePath" to pyq.questionImagePath,
                        "answerImagePath" to pyq.answerImagePath,
                        "repeatCount" to pyq.repeatCount,
                        "yearsSeen" to pyq.yearsSeen,
                        "marks" to pyq.marks,
                        "contributedBy" to pyq.contributedBy
                    )
                ).await()
            Log.d(TAG, "✅ PYQ ${pyq.id} saved")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to save PYQ ${pyq.id}: ${e.message}", e)
            Result.failure(e)
        }
    }
}
