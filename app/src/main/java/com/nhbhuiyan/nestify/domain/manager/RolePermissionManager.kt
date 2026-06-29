package com.nhbhuiyan.nestify.domain.manager

import com.nhbhuiyan.nestify.domain.model.UserRole

sealed interface Permission {
    object ViewAcademics : Permission
    object CreateOwnContent : Permission
    object SubmitMergeRequest : Permission
    object LikeComment : Permission
    object ModeratePost : Permission
    object PostAnnouncement : Permission
    object EditSubject : Permission
    object ViewCtMarks : Permission
    object EditCtMarks : Permission
    object ResolveMergeRequest : Permission
    object PromoteDemoteCr : Permission
    object ManageAdmin : Permission
    object ManageGlobalConfig : Permission
    object ViewAuditLog : Permission
}

object RolePermissionManager {

    /**
     * Checks if the given role is allowed to perform the specific permission.
     */
    fun hasPermission(role: UserRole, permission: Permission): Boolean {
        return when (permission) {
            // Student+ capabilities
            Permission.ViewAcademics -> role.rank >= UserRole.STUDENT.rank
            Permission.CreateOwnContent -> role.rank >= UserRole.STUDENT.rank
            Permission.SubmitMergeRequest -> role.rank >= UserRole.STUDENT.rank
            Permission.LikeComment -> role.rank >= UserRole.STUDENT.rank

            // CR+ capabilities
            Permission.ModeratePost -> role.rank >= UserRole.CR.rank
            Permission.PostAnnouncement -> role.rank >= UserRole.CR.rank
            Permission.EditSubject -> role.rank >= UserRole.CR.rank
            Permission.ViewCtMarks -> role.rank >= UserRole.CR.rank
            Permission.EditCtMarks -> role.rank >= UserRole.CR.rank
            Permission.ResolveMergeRequest -> role.rank >= UserRole.CR.rank
            Permission.ViewAuditLog -> role.rank >= UserRole.CR.rank

            // Admin+ capabilities
            Permission.PromoteDemoteCr -> role.rank >= UserRole.ADMIN.rank

            // Superadmin-only capabilities
            Permission.ManageAdmin -> role.rank >= UserRole.SUPERADMIN.rank
            Permission.ManageGlobalConfig -> role.rank >= UserRole.SUPERADMIN.rank
        }
    }
}
