package org.logiclettuce.depman.security.configuration

import org.logiclettuce.depman.common.domain.role.Role


enum class UserRole {
    MEMBER,
    EMPLOYEE,
    HEAD,
    ADMIN;

    companion object {
        const val ROLE_PREFIX_VALUE = "ROLE_"

        fun getRoleIdentifier(userRole: UserRole): String {
            return "${UserRole.ROLE_PREFIX_VALUE}${userRole}"
        }

        fun getRoleIdentifier(role: Role): String {
            return "${UserRole.ROLE_PREFIX_VALUE}${role.id!!.role!!}"
        }
    }
}
