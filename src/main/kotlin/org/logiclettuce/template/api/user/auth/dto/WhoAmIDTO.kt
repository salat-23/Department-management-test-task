package org.logiclettuce.template.api.user.auth.dto

import org.logiclettuce.template.common.dao.user.User
import org.logiclettuce.template.security.configuration.RoleEnum

class WhoAmIDTO(val email: String, val roles: List<String>) {
    companion object {
        fun fromUser(user: User) = org.logiclettuce.template.api.user.auth.dto.WhoAmIDTO(
            email = user.email,
            roles = user.roleList.map(RoleEnum::toString)
        )
    }
}

