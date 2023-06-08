package org.logiclettuce.depman.api.common.dto

import org.logiclettuce.depman.common.domain.user.User

data class UserGenericResponse (
    val id: Long,
    val login: String,
    val email: String,
    val roles: List<String>,
) {
    constructor(user: User): this(
        user.id ?: -1,
        user.login,
        user.email,
        user.roles.filter { role -> role.id != null }.map { role -> role.id!!.role.toString() }
    )
}