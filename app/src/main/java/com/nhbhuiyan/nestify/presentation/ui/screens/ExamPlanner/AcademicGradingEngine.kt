package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

object AcademicGradingEngine {

    // Standard Academic Grade Points
    val gradeScale = mapOf(
        "A+" to 4.00f,
        "A" to 3.75f,
        "A-" to 3.50f,
        "B+" to 3.25f,
        "B" to 3.00f,
        "B-" to 2.75f,
        "C+" to 2.50f,
        "C" to 2.25f,
        "D" to 2.00f,
        "F" to 0.00f
    )

    /**
     * Map letter grade to grade point
     */
    fun gradeToGp(grade: String): Float {
        return gradeScale[grade.uppercase()] ?: 0.00f
    }

    /**
     * Extract best 3 of 4 CT marks
     */
    fun calculateBestThreeCTSum(ctList: List<Float>): Float {
        if (ctList.isEmpty()) return 0f
        // Sort descending and take top 3
        val sortedList = ctList.sortedDescending()
        return sortedList.take(3).sum()
    }

    /**
     * Get credit-based scaling factor
     * 3 credits -> ratio = 1.0 (CT max = 60, Att max = 30, Written max = 210)
     * 4 credits -> ratio = 1.3333 (CT max = 80, Att max = 40, Written max = 280)
     * 1.5 credits -> ratio = 0.5 (CT max = 30, Att max = 15, Written max = 105)
     */
    fun getCreditScalingRatio(credits: Float): Float {
        return when {
            credits >= 4.0f -> 1.33333f
            credits <= 1.5f -> 0.5f
            else -> 1.0f // Defaults to 3 credits
        }
    }

    /**
     * Calculate internal total score (CT + Attendance)
     */
    fun calculateInternalTotal(ctList: List<Float>, attendance: Float, credits: Float): Float {
        val ratio = getCreditScalingRatio(credits)
        val bestThreeSum = calculateBestThreeCTSum(ctList)
        val earnedCT = bestThreeSum * ratio
        return earnedCT + attendance
    }

    data class RequiredMarksResult(
        val needed: Float,
        val maxWritten: Float,
        val isPossible: Boolean,
        val isSecured: Boolean
    )

    /**
     * Predict final written marks required to secure a specific grade percentage threshold
     */
    fun predictRequiredWrittenMarks(
        internalMarks: Float,
        credits: Float,
        targetPercentage: Float
    ): RequiredMarksResult {
        val ratio = getCreditScalingRatio(credits)
        val maxCourseMarks = credits * 100f
        
        val maxCT = 60f * ratio
        val maxAtt = 30f * ratio
        val maxWritten = maxCourseMarks - (maxCT + maxAtt)

        val totalNeeded = maxCourseMarks * targetPercentage
        val needed = totalNeeded - internalMarks

        return when {
            needed <= 0f -> RequiredMarksResult(0f, maxWritten, isPossible = true, isSecured = true)
            needed > maxWritten -> RequiredMarksResult(needed, maxWritten, isPossible = false, isSecured = false)
            else -> RequiredMarksResult(needed, maxWritten, isPossible = true, isSecured = false)
        }
    }

    data class CourseGradeInfo(val credits: Float, val gp: Float)

    /**
     * Calculate Term GPA out of 4.0
     */
    fun calculateTermGpa(courses: List<CourseGradeInfo>): Float {
        val activeCourses = courses.filter { it.gp >= 0f }
        val totalCredits = activeCourses.map { it.credits }.sum()
        if (totalCredits <= 0f) return 0.00f
        val weightedSum = activeCourses.map { it.credits * it.gp }.sum()
        return weightedSum / totalCredits
    }

    data class FutureGpaResult(
        val requiredGpaPerTerm: Float,
        val isPossible: Boolean,
        val maxPossible: Float
    )

    /**
     * Project remaining term GPA targets to reach a target cumulative CGPA
     */
    fun projectRequiredFutureGpa(
        completedTermsGpa: List<Float>,
        targetCgpa: Float
    ): FutureGpaResult {
        val completedCount = completedTermsGpa.size
        val remainingCount = 8 - completedCount
        
        val currentSum = completedTermsGpa.sum()
        val currentAvg = if (completedCount > 0) currentSum / completedCount else 0f

        val maxPossible = if (remainingCount > 0) {
            (currentSum + (4.00f * remainingCount)) / 8f
        } else {
            currentAvg
        }

        if (remainingCount <= 0) {
            return FutureGpaResult(0f, isPossible = currentAvg >= targetCgpa, maxPossible = currentAvg)
        }

        val requiredGpa = ((targetCgpa * 8) - currentSum) / remainingCount

        return when {
            requiredGpa > 4.0f -> FutureGpaResult(requiredGpa, isPossible = false, maxPossible = maxPossible)
            requiredGpa <= 0f -> FutureGpaResult(0.00f, isPossible = true, maxPossible = maxPossible)
            else -> FutureGpaResult(requiredGpa, isPossible = true, maxPossible = maxPossible)
        }
    }
}
