package com.nhbhuiyan.nestify.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class StudentIdentityTest {

    @Test
    fun parse_validCseEmail_returnsCorrectIdentity() {
        val email = "u2304097@student.cuet.ac.bd"
        val identity = StudentIdentity.parse(email)

        assertNotNull(identity)
        identity?.let {
            assertEquals("u2304097@student.cuet.ac.bd", it.email)
            assertEquals("2304097", it.studentId)
            assertEquals("2023", it.batchYear)
            assertEquals("CSE", it.departmentCode)
            assertEquals("097", it.rollNumber)
            assertEquals("CUET-CSE-23", it.classGroupId)
        }
    }

    @Test
    fun parse_validEeeEmail_returnsCorrectIdentity() {
        val email = "u2208001@student.cuet.ac.bd"
        val identity = StudentIdentity.parse(email)

        assertNotNull(identity)
        identity?.let {
            assertEquals("u2208001@student.cuet.ac.bd", it.email)
            assertEquals("2208001", it.studentId)
            assertEquals("2022", it.batchYear)
            assertEquals("EEE", it.departmentCode)
            assertEquals("001", it.rollNumber)
            assertEquals("CUET-EEE-22", it.classGroupId)
        }
    }

    @Test
    fun parse_uppercaseEmail_parsesSuccessfully() {
        val email = "U2304097@STUDENT.CUET.AC.BD"
        val identity = StudentIdentity.parse(email)

        assertNotNull(identity)
        assertEquals("u2304097@student.cuet.ac.bd", identity?.email)
        assertEquals("CSE", identity?.departmentCode)
    }

    @Test
    fun parse_invalidDomain_returnsNull() {
        val email = "u2304097@gmail.com"
        val identity = StudentIdentity.parse(email)
        assertNull(identity)
    }

    @Test
    fun parse_invalidRollFormat_returnsNull() {
        // Missing the 'u' prefix
        assertNull(StudentIdentity.parse("2304097@student.cuet.ac.bd"))
        // Too short roll number
        assertNull(StudentIdentity.parse("u23049@student.cuet.ac.bd"))
        // Too long roll number
        assertNull(StudentIdentity.parse("u23040978@student.cuet.ac.bd"))
    }
}
