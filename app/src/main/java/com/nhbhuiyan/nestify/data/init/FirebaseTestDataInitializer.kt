package com.nhbhuiyan.nestify.data.init

import android.content.Context
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds a large body of dummy data into Firestore for development/demo:
 *
 *  - 4 departments (CSE, EEE, CE, ME), each with the full universal "data box":
 *    8 semesters (L1T1 … L4T2), each holding a bundle of subjects, and every
 *    subject holding an exam plan (topics split into Section A / Section B with
 *    several previous-year questions + answers each).
 *      => 4 depts * 8 semesters = 32 subject-bundles + exam plans.
 *
 *  - 16 class groups (4 batches: 21/22/23/24  x  4 departments).
 *    Each group has 10 students; the first 4 of every group are CRs.
 *
 *  - Users + roster bindings, announcements, CT marks and merge requests so the
 *    whole app has something to show out of the box.
 *
 * The departments/semesters/subjects/topics/pyqs tree is the "universal" box —
 * it is shared by everyone who is promoted to that level + term, exactly matching
 * what [com.nhbhuiyan.nestify.data.repository.DepartmentRepositoryImpl] reads.
 */
@Singleton
class FirebaseTestDataInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    // --- Static config -------------------------------------------------------

    private val departments = linkedMapOf(
        "CSE" to "Computer Science and Engineering",
        "EEE" to "Electrical and Electronic Engineering",
        "CE" to "Civil Engineering",
        "ME" to "Mechanical Engineering"
    )

    /** Numeric codes used inside CUET emails (uXXYYZZZ@student.cuet.ac.bd, YY = dept). */
    private val deptNumeric = mapOf(
        "CSE" to "04",
        "EEE" to "08",
        "CE" to "01",
        "ME" to "02"
    )

    /** Batch short years. 24 = freshest (Level 1) … 21 = oldest (Level 4). */
    private val batches = listOf("21", "22", "23", "24")

    /** The 8 universal semesters: 4 levels x 2 terms. */
    private val semesters = listOf("L1T1", "L1T2", "L2T1", "L2T2", "L3T1", "L3T2", "L4T1", "L4T2")

    /** 32 subjects per department (4 per semester x 8 semesters). */
    private val subjectPools: Map<String, List<String>> = mapOf(
        "CSE" to listOf(
            "Structured Programming", "Discrete Mathematics", "Electrical Circuits", "Calculus & Differential Equations",
            "Data Structures", "Digital Logic Design", "Object Oriented Programming", "Linear Algebra & Statistics",
            "Algorithms", "Computer Architecture", "Database Systems", "Electronic Devices & Circuits",
            "Theory of Computation", "Operating Systems", "Microprocessors & Microcontrollers", "Numerical Methods",
            "Computer Networks", "Software Engineering", "Artificial Intelligence", "Compiler Design",
            "Data Communication", "Web Engineering", "Machine Learning", "Digital Signal Processing",
            "Computer Graphics", "Distributed Systems", "Information Security", "Mobile Application Development",
            "Pattern Recognition", "Cloud Computing", "VLSI Design", "Big Data Analytics"
        ),
        "EEE" to listOf(
            "Electrical Circuit Analysis", "Engineering Mathematics I", "Physics for Engineers", "Engineering Drawing",
            "Electronics I", "Engineering Mathematics II", "Electrical Machines I", "Signals and Systems",
            "Electromagnetic Fields", "Electronics II", "Electrical Machines II", "Numerical Analysis",
            "Power System I", "Digital Electronics", "Control Systems", "Communication Theory",
            "Power System II", "Microprocessor and Interfacing", "Power Electronics", "Digital Signal Processing",
            "High Voltage Engineering", "Electrical Drives", "Renewable Energy Systems", "VLSI Circuit Design",
            "Power System Protection", "Microwave Engineering", "Optical Fiber Communication", "Switchgear and Protection",
            "Biomedical Instrumentation", "Smart Grid Technology", "Embedded Systems", "Robotics and Automation"
        ),
        "CE" to listOf(
            "Engineering Mechanics", "Engineering Mathematics I", "Surveying I", "Engineering Geology",
            "Strength of Materials", "Engineering Mathematics II", "Fluid Mechanics", "Surveying II",
            "Structural Analysis I", "Concrete Technology", "Geotechnical Engineering I", "Hydrology",
            "Structural Analysis II", "Reinforced Concrete Design", "Geotechnical Engineering II", "Transportation Engineering I",
            "Steel Structure Design", "Environmental Engineering I", "Foundation Engineering", "Transportation Engineering II",
            "Environmental Engineering II", "Irrigation Engineering", "Prestressed Concrete", "Earthquake Engineering",
            "Bridge Engineering", "Water Resources Engineering", "Construction Management", "Pavement Design",
            "Coastal Engineering", "Structural Dynamics", "Traffic Engineering", "Solid Waste Management"
        ),
        "ME" to listOf(
            "Engineering Mechanics", "Engineering Mathematics I", "Engineering Drawing", "Workshop Practice",
            "Thermodynamics", "Engineering Mathematics II", "Materials Science", "Manufacturing Process I",
            "Fluid Mechanics", "Mechanics of Materials", "Manufacturing Process II", "Kinematics of Machinery",
            "Heat Transfer", "Dynamics of Machinery", "Machine Design I", "Internal Combustion Engines",
            "Machine Design II", "Fluid Machinery", "Refrigeration and Air Conditioning", "Control Engineering",
            "Power Plant Engineering", "Mechanical Vibration", "Finite Element Analysis", "Automobile Engineering",
            "Robotics", "Computational Fluid Dynamics", "Industrial Engineering", "Renewable Energy Technology",
            "Mechatronics", "Tribology", "Gas Dynamics", "Production Planning and Control"
        )
    )

    private val studentNames = listOf(
        "Tanvir Ahmed", "Sadia Islam", "Rakibul Hasan", "Nusrat Jahan", "Mahir Tajwar",
        "Farhana Akter", "Sabbir Rahman", "Maliha Chowdhury", "Imran Kabir", "Tasnim Ferdous"
    )

    private val sectionATopics = listOf(
        "Introduction & Basic Concepts",
        "Core Theory and Principles",
        "Analytical Methods"
    )
    private val sectionBTopics = listOf(
        "Design and Applications",
        "Advanced Topics",
        "Case Studies & Problem Solving"
    )

    // --- Entry point ---------------------------------------------------------

    fun initializeData() {
        val prefs = context.getSharedPreferences("firebase_seeding_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("seeded_v2", false)) return

        scope.launch {
            try {
                val writer = BatchWriter()

                seedUniversalDataBox(writer)
                seedClassGroupsAndUsers(writer)

                writer.flush()

                prefs.edit().putBoolean("seeded_v2", true).apply()
                android.util.Log.d(
                    "FirebaseTestData",
                    "✅ Seeding completed: ${departments.size} departments, " +
                        "${departments.size * semesters.size} subject bundles, " +
                        "${batches.size * departments.size} class groups."
                )
            } catch (e: Exception) {
                android.util.Log.e("FirebaseTestData", "❌ Seeding failed: ${e.message}", e)
            }
        }
    }

    // --- Universal "data box": departments -> semesters -> subjects -> topics -> pyqs ---

    private suspend fun seedUniversalDataBox(writer: BatchWriter) {
        for ((deptCode, deptName) in departments) {
            val deptRef = firestore.collection("departments").document(deptCode)
            writer.set(deptRef, mapOf("name" to deptName))

            val pool = subjectPools.getValue(deptCode)

            semesters.forEachIndexed { semIndex, semesterId ->
                val level = semIndex / 2 + 1
                val term = semIndex % 2 + 1

                val semRef = deptRef.collection("semesters").document(semesterId)
                writer.set(semRef, mapOf("semesterId" to semesterId))

                // 4 subjects for this semester.
                for (s in 0 until 4) {
                    val globalIndex = semIndex * 4 + s
                    val subjectName = pool[globalIndex]
                    val subjectCode = "$deptCode-$level$term${(s + 1)}"
                    val credits = when (globalIndex % 4) {
                        0 -> 3.0f
                        1 -> 4.0f
                        2 -> 3.0f
                        else -> 1.5f
                    }
                    val examDate = String.format(
                        "2026-%02d-%02d",
                        (semIndex % 12) + 1,
                        10 + s * 3
                    )

                    val subjectRef = semRef.collection("subjects").document(subjectCode)
                    writer.set(
                        subjectRef,
                        mapOf(
                            "name" to subjectName,
                            "credits" to credits,
                            "examDate" to examDate
                        )
                    )

                    seedExamPlan(writer, subjectRef, deptCode, subjectName, subjectCode)
                }
            }
        }
    }

    /** Topics (Section A / Section B) + several previous-year questions & answers per topic. */
    private suspend fun seedExamPlan(
        writer: BatchWriter,
        subjectRef: DocumentReference,
        deptCode: String,
        subjectName: String,
        subjectCode: String
    ) {
        var topicCounter = 0
        // Two topics per section keeps the bundle rich but bounded.
        val plan = listOf("A" to sectionATopics.take(2), "B" to sectionBTopics.take(2))

        for ((section, themes) in plan) {
            for (theme in themes) {
                topicCounter++
                val topicId = "${subjectCode.lowercase()}-t$topicCounter"
                val topicTitle = "$subjectName — $theme"

                writer.set(
                    subjectRef.collection("topics").document(topicId),
                    mapOf(
                        "section" to section,
                        "title" to topicTitle
                    )
                )

                // 3 previous-year questions per topic, each with an answer.
                for (q in 1..3) {
                    val pyqId = "$topicId-q$q"
                    val marks = when (q) {
                        1 -> "5"
                        2 -> "10"
                        else -> "15"
                    }
                    val yearsSeen = when (q) {
                        1 -> listOf("2021", "2023")
                        2 -> listOf("2020", "2022", "2024")
                        else -> listOf("2019", "2024")
                    }
                    writer.set(
                        subjectRef.collection("topics").document(topicId)
                            .collection("pyqs").document(pyqId),
                        mapOf(
                            "questionText" to buildQuestion(q, theme, subjectName, marks),
                            "answerText" to buildAnswer(q, theme, subjectName),
                            "questionImagePath" to null,
                            "answerImagePath" to null,
                            "repeatCount" to yearsSeen.size,
                            "yearsSeen" to yearsSeen,
                            "marks" to marks,
                            "contributedBy" to "seed-$deptCode-bank"
                        )
                    )
                }
            }
        }
    }

    private fun buildQuestion(q: Int, theme: String, subjectName: String, marks: String): String = when (q) {
        1 -> "Define the key terms related to \"$theme\" in $subjectName and explain their significance. [$marks marks]"
        2 -> "Discuss \"$theme\" with appropriate diagrams/examples and compare it with related approaches in $subjectName. [$marks marks]"
        else -> "A real-world scenario based on \"$theme\" is given. Analyse it, derive the required result step by step, and justify your assumptions for $subjectName. [$marks marks]"
    }

    private fun buildAnswer(q: Int, theme: String, subjectName: String): String = when (q) {
        1 -> "\"$theme\" refers to the foundational ideas of $subjectName. The core terms describe how the system behaves under standard conditions; each is defined precisely and illustrated with a short example so the relationship between them is clear."
        2 -> "To answer this, we outline \"$theme\" in $subjectName, present the governing relations, and walk through a worked example. We then contrast it with alternative methods, noting where each performs better in terms of accuracy, cost, and complexity."
        else -> "We model the scenario using the principles of \"$theme\". Step 1: list the knowns and assumptions. Step 2: apply the relevant equations of $subjectName. Step 3: solve and verify the result against the boundary conditions, confirming the answer is consistent."
    }

    // --- Class groups, users, roster, announcements, CT marks, merge requests ---

    private suspend fun seedClassGroupsAndUsers(writer: BatchWriter) {
        for ((deptCode, _) in departments) {
            val numeric = deptNumeric.getValue(deptCode)

            for (batch in batches) {
                val groupId = "CUET-$deptCode-$batch"
                // 24 -> Level 1 … 21 -> Level 4.
                val level = 5 - (batch.toInt() - 20) // 24->1, 23->2, 22->3, 21->4
                val term = 1
                val currentSemester = "L${level}T$term"

                val uids = (1..10).map { roll -> "seed-$batch-$numeric-${String.format("%03d", roll)}" }
                val crUids = uids.take(4)

                writer.set(
                    firestore.collection("classGroups").document(groupId),
                    mapOf(
                        "classGroupId" to groupId,
                        "departmentCode" to deptCode,
                        "batch" to "20$batch",
                        "currentLevel" to level,
                        "currentTerm" to term,
                        "crList" to crUids,
                        "adminList" to listOf(crUids.first())
                    )
                )

                // Subjects of the group's current semester, used for CT marks.
                val semIndex = semesters.indexOf(currentSemester).coerceAtLeast(0)
                val pool = subjectPools.getValue(deptCode)
                val currentSubjectCodes = (0 until 4).map { s -> "$deptCode-$level$term${s + 1}" }

                for (rollIdx in 1..10) {
                    val roll = String.format("%03d", rollIdx)
                    val uid = "seed-$batch-$numeric-$roll"
                    val studentId = "$batch$numeric$roll"
                    val email = "u$studentId@student.cuet.ac.bd"
                    val role = if (rollIdx <= 4) "cr" else "student"
                    val displayName = studentNames[rollIdx - 1]

                    writer.set(
                        firestore.collection("users").document(uid),
                        mapOf(
                            "uid" to uid,
                            "displayName" to displayName,
                            "email" to email,
                            "role" to role,
                            "classGroupId" to groupId,
                            "departmentCode" to deptCode,
                            "rollNumber" to roll,
                            "studentId" to studentId,
                            "batchYear" to "20$batch",
                            "photoUrl" to "",
                            "pendingReview" to false
                        )
                    )

                    writer.set(
                        firestore.collection("classGroups").document(groupId)
                            .collection("roster").document(uid),
                        mapOf(
                            "uid" to uid,
                            "displayName" to displayName,
                            "rollNumber" to roll,
                            "role" to role
                        )
                    )

                    // CT marks for the current semester subjects.
                    val marksPayload = mutableMapOf<String, Any>()
                    currentSubjectCodes.forEachIndexed { sIdx, code ->
                        marksPayload[code] = mapOf(
                            "ct1" to (6 + (rollIdx + sIdx) % 5).toFloat(),
                            "ct2" to (5 + (rollIdx * 2 + sIdx) % 6).toFloat(),
                            "ct3" to (7 + (rollIdx + sIdx * 2) % 4).toFloat(),
                            "ct4" to (4 + (rollIdx + sIdx) % 7).toFloat(),
                            "attendance" to (3 + (rollIdx + sIdx) % 3).toFloat()
                        )
                    }
                    writer.set(
                        firestore.collection("classGroups").document(groupId)
                            .collection("ctMarks").document(currentSemester)
                            .collection(studentId).document("marks"),
                        marksPayload
                    )
                }

                seedAnnouncements(writer, groupId, crUids.first(), deptCode, currentSemester)
                seedMergeRequest(writer, groupId, deptCode, currentSemester, pool, semIndex, uids[4])
                // Demo Network feed posts — debug builds only (gated out of release).
                if (com.nhbhuiyan.nestify.BuildConfig.DEBUG) {
                    seedNetworkPosts(writer, groupId, deptCode, batch, uids)
                }
            }
        }
    }

    private suspend fun seedAnnouncements(
        writer: BatchWriter,
        groupId: String,
        crUid: String,
        deptCode: String,
        currentSemester: String
    ) {
        val anns = listOf(
            Triple(
                "Welcome to $groupId",
                "This is the official class group for $deptCode ($currentSemester). CRs will post all updates here.",
                "high"
            ),
            Triple(
                "Class Test Schedule",
                "CT-1 for $currentSemester subjects starts next week. Please check the exam plan in the planner.",
                "medium"
            ),
            Triple(
                "Assignment Reminder",
                "Submit your lab reports before Thursday. Late submissions will lose marks.",
                "low"
            )
        )
        anns.forEachIndexed { i, (title, body, priority) ->
            writer.set(
                firestore.collection("classGroups").document(groupId)
                    .collection("announcements").document("ann-${i + 1}"),
                mapOf(
                    "title" to title,
                    "body" to body,
                    "createdBy" to crUid,
                    "priority" to priority,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
        }
    }

    /** A few sample Network feed posts so the Network tab has live content for the pilot. */
    private suspend fun seedNetworkPosts(
        writer: BatchWriter,
        groupId: String,
        deptCode: String,
        batch: String,
        uids: List<String>
    ) {
        // (type key, title, body, tags) — keys mirror PostCategory in the feed screen.
        val posts = listOf(
            PostSeed(
                authorIdx = 0,
                type = "academic_aid",
                title = "Full handwritten notes for this term's core subjects",
                body = "Sharing my complete note set covering the major topics with solved previous-year " +
                    "questions for both sections. Hope it helps before the CTs!",
                tags = listOf("Notes", deptCode, "CT")
            ),
            PostSeed(
                authorIdx = 1,
                type = "hackathon",
                title = "CUET Hackathon 2026 — registration is open!",
                body = "48-hour hackathon on applied AI & embedded systems. Teams of 3–4. Looking for " +
                    "teammates who love ML and hardware. Deadline next Friday.",
                tags = listOf("Hackathon", "AI", "TeamUp")
            ),
            PostSeed(
                authorIdx = 2,
                type = "project_invite",
                title = "Looking for 2 collaborators on a semester project",
                body = "Building a small campus utility app. Need teammates comfortable with Kotlin and " +
                    "Firebase. Great for your portfolio.",
                tags = listOf("Collab", "AndroidDev", deptCode)
            ),
        )

        posts.forEachIndexed { i, p ->
            writer.set(
                firestore.collection("classGroups").document(groupId)
                    .collection("posts").document("seed-post-${batch}-${i + 1}"),
                mapOf(
                    "authorUid" to uids[p.authorIdx],
                    "authorName" to studentNames[p.authorIdx],
                    "authorMeta" to "$deptCode · Roll ${String.format("%03d", p.authorIdx + 1)}",
                    "type" to p.type,
                    "title" to p.title,
                    "body" to p.body,
                    "tags" to p.tags,
                    "likeCount" to 0L,
                    "commentCount" to 0L,
                    "likedBy" to emptyList<String>(),
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
        }
    }

    private data class PostSeed(
        val authorIdx: Int,
        val type: String,
        val title: String,
        val body: String,
        val tags: List<String>
    )

    private suspend fun seedMergeRequest(
        writer: BatchWriter,
        groupId: String,
        deptCode: String,
        currentSemester: String,
        pool: List<String>,
        semIndex: Int,
        submitterUid: String
    ) {
        val proposedName = pool.getOrElse(semIndex * 4 + 3) { "Elective Course" }
        writer.set(
            firestore.collection("classGroups").document(groupId)
                .collection("mergeRequests").document("mr-1"),
            mapOf(
                "type" to "subject",
                "target" to "$deptCode-NEW",
                "semesterId" to currentSemester,
                "departmentCode" to deptCode,
                "status" to "pending",
                "submitterName" to "Student Contributor",
                "submittedBy" to submitterUid,
                "data" to mapOf("name" to proposedName, "credits" to 3.0f, "examDate" to ""),
                "submittedAt" to FieldValue.serverTimestamp()
            )
        )
    }

    // --- Batched writer (Firestore caps a batch at 500 ops) ------------------

    private inner class BatchWriter {
        private var batch: WriteBatch = firestore.batch()
        private var count = 0

        suspend fun set(ref: DocumentReference, data: Map<String, Any?>) {
            batch.set(ref, data)
            count++
            if (count >= 450) flush()
        }

        suspend fun flush() {
            if (count == 0) return
            batch.commit().await()
            batch = firestore.batch()
            count = 0
        }
    }
}
