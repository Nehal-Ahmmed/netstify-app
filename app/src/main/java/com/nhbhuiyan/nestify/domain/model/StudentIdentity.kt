package com.nhbhuiyan.nestify.domain.model

import java.util.Locale

/**
 * Parses and validates CUET student email addresses: uXXYYZZZ@student.cuet.ac.bd
 * Example: u2304097@student.cuet.ac.bd
 * - XX (match[1]): Batch short year (e.g. "23")
 * - YY (match[2]): Department code (e.g. "04")
 * - ZZZ (match[3]): Roll number (e.g. "097")
 */
data class StudentIdentity(
    val email: String,
    val studentId: String,
    val batchYear: String,
    val departmentCode: String,
    val rollNumber: String,
    val classGroupId: String
) {
    companion object {
        private val EMAIL_REGEX = Regex("^u(\\d{2})(\\d{2})(\\d{3})@student\\.cuet\\.ac\\.bd$", RegexOption.IGNORE_CASE)

        private val DEPT_MAP = mapOf(
            "04" to "CSE",
            "08" to "EEE",
            "01" to "CE",
            "02" to "ME",
            "03" to "IPE"
        )

        /**
         * Validates email format and parses it into [StudentIdentity].
         * Returns null if formatting is invalid.
         */
        fun parse(email: String): StudentIdentity? {
            val matchResult = EMAIL_REGEX.matchEntire(email.trim()) ?: return null
            val batchShort = matchResult.groupValues[1]
            val deptCode = matchResult.groupValues[2]
            val roll = matchResult.groupValues[3]

            val batchYear = "20$batchShort"
            val departmentCode = DEPT_MAP[deptCode] ?: deptCode
            val classGroupId = "CUET-$departmentCode-$batchShort"
            val studentId = "$batchShort$deptCode$roll"

            return StudentIdentity(
                email = email.trim().lowercase(Locale.ROOT),
                studentId = studentId,
                batchYear = batchYear,
                departmentCode = departmentCode,
                rollNumber = roll,
                classGroupId = classGroupId
            )
        }
    }
}
