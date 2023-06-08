package org.logiclettuce.depman.api.user.auth.dto

import org.logiclettuce.depman.common.domain.role.Role
import org.logiclettuce.depman.common.domain.user.User

class WhoAmIResponse(val login: String, val roles: List<String>) {
    companion object {
        fun fromUser(user: User) = WhoAmIResponse(
            login = user.login,
            roles = user.roles.map(Role::toString)
        )
    }
}

