package com.nhbhuiyan.nestify.domain.model

import java.util.Locale

enum class UserRole(val roleName: String, val rank: Int) {
    PENDING("pending", 0),
    STUDENT("student", 1),
    CR("cr", 2),
    ADMIN("admin", 3),
    SUPERADMIN("superadmin", 4);

    companion object {
        fun fromName(name: String?): UserRole {
            if (name == null) return STUDENT
            return values().firstOrNull { it.roleName.equals(name.trim(), ignoreCase = true) } ?: STUDENT
        }
    }
}
