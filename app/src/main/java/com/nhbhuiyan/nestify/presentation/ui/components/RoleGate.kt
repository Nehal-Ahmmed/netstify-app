package com.nhbhuiyan.nestify.presentation.ui.components

import androidx.compose.runtime.Composable
import com.nhbhuiyan.nestify.domain.manager.Permission
import com.nhbhuiyan.nestify.domain.manager.RolePermissionManager
import com.nhbhuiyan.nestify.domain.model.UserRole

@Composable
fun RoleGate(
    currentRole: UserRole,
    requiredRole: UserRole,
    fallback: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (currentRole.rank >= requiredRole.rank) {
        content()
    } else {
        fallback?.invoke()
    }
}

@Composable
fun PermissionGate(
    currentRole: UserRole,
    requiredPermission: Permission,
    fallback: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (RolePermissionManager.hasPermission(currentRole, requiredPermission)) {
        content()
    } else {
        fallback?.invoke()
    }
}
