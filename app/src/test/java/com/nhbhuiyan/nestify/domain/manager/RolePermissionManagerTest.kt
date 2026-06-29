package com.nhbhuiyan.nestify.domain.manager

import com.nhbhuiyan.nestify.domain.model.UserRole
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RolePermissionManagerTest {

    @Test
    fun hasPermission_student_authorizedCorrectly() {
        // Students should have basic read/write access to their own content
        assertTrue(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.ViewAcademics))
        assertTrue(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.CreateOwnContent))
        assertTrue(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.SubmitMergeRequest))
        assertTrue(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.LikeComment))

        // Students should NOT have any moderation or management rights
        assertFalse(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.ModeratePost))
        assertFalse(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.PostAnnouncement))
        assertFalse(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.EditSubject))
        assertFalse(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.ViewCtMarks))
        assertFalse(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.EditCtMarks))
        assertFalse(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.ResolveMergeRequest))
        assertFalse(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.PromoteDemoteCr))
        assertFalse(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.ManageAdmin))
        assertFalse(RolePermissionManager.hasPermission(UserRole.STUDENT, Permission.ManageGlobalConfig))
    }

    @Test
    fun hasPermission_cr_authorizedCorrectly() {
        // CRs should have student permissions
        assertTrue(RolePermissionManager.hasPermission(UserRole.CR, Permission.ViewAcademics))
        assertTrue(RolePermissionManager.hasPermission(UserRole.CR, Permission.CreateOwnContent))

        // CRs should have class-wide governance permissions
        assertTrue(RolePermissionManager.hasPermission(UserRole.CR, Permission.ModeratePost))
        assertTrue(RolePermissionManager.hasPermission(UserRole.CR, Permission.PostAnnouncement))
        assertTrue(RolePermissionManager.hasPermission(UserRole.CR, Permission.EditSubject))
        assertTrue(RolePermissionManager.hasPermission(UserRole.CR, Permission.ViewCtMarks))
        assertTrue(RolePermissionManager.hasPermission(UserRole.CR, Permission.EditCtMarks))
        assertTrue(RolePermissionManager.hasPermission(UserRole.CR, Permission.ResolveMergeRequest))
        assertTrue(RolePermissionManager.hasPermission(UserRole.CR, Permission.ViewAuditLog))

        // CRs should NOT have admin-level role management or global configuration rights
        assertFalse(RolePermissionManager.hasPermission(UserRole.CR, Permission.PromoteDemoteCr))
        assertFalse(RolePermissionManager.hasPermission(UserRole.CR, Permission.ManageAdmin))
        assertFalse(RolePermissionManager.hasPermission(UserRole.CR, Permission.ManageGlobalConfig))
    }

    @Test
    fun hasPermission_admin_authorizedCorrectly() {
        // Admins should have CR and student permissions
        assertTrue(RolePermissionManager.hasPermission(UserRole.ADMIN, Permission.EditSubject))
        assertTrue(RolePermissionManager.hasPermission(UserRole.ADMIN, Permission.ResolveMergeRequest))

        // Admins should have CR promotion/demotion capabilities
        assertTrue(RolePermissionManager.hasPermission(UserRole.ADMIN, Permission.PromoteDemoteCr))

        // Admins should NOT have superadmin-level platform rights
        assertFalse(RolePermissionManager.hasPermission(UserRole.ADMIN, Permission.ManageAdmin))
        assertFalse(RolePermissionManager.hasPermission(UserRole.ADMIN, Permission.ManageGlobalConfig))
    }

    @Test
    fun hasPermission_superadmin_authorizedCorrectly() {
        // Superadmins should have all permissions
        assertTrue(RolePermissionManager.hasPermission(UserRole.SUPERADMIN, Permission.ViewAcademics))
        assertTrue(RolePermissionManager.hasPermission(UserRole.SUPERADMIN, Permission.PromoteDemoteCr))
        assertTrue(RolePermissionManager.hasPermission(UserRole.SUPERADMIN, Permission.ManageAdmin))
        assertTrue(RolePermissionManager.hasPermission(UserRole.SUPERADMIN, Permission.ManageGlobalConfig))
    }
}
