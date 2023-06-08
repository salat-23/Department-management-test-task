package org.logiclettuce.depman.api.department.dto

import org.logiclettuce.depman.api.common.dto.UserGenericResponse
import org.logiclettuce.depman.common.domain.department.Department

data class DepartmentGenericResponse(
    val id: Long,
    val name: String,
    val code: String,
    val head: UserGenericResponse
) {
    constructor(department: Department) : this(
        department.id ?: -1,
        department.name,
        department.code,
        UserGenericResponse(department.head)
    )
}