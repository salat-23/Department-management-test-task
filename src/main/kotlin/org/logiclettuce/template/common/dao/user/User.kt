package org.logiclettuce.template.common.dao.user

import org.logiclettuce.template.security.configuration.RoleEnum

data class User(
    val id: Long,
    var email: String,
    var password: String,
    var active: Boolean,
) {
    var roleList: MutableList<RoleEnum> = mutableListOf()
}
