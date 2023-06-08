package org.logiclettuce.depman.api.employee.dto

import org.logiclettuce.depman.api.common.dto.UserGenericResponse
import org.logiclettuce.depman.common.domain.employee.Employee

data class EmployeeGenericResponse(
    val id: Long,
    val user: UserGenericResponse,
    val fullName: String,
    val departmentJunctions: List<EmployeeDepartmentJunctionResponse>
) {
    constructor(employee: Employee) : this(
        employee.id ?: -1,
        UserGenericResponse(employee.user),
        employee.fullName,
        employee.junctions.map { EmployeeDepartmentJunctionResponse(it) })
}