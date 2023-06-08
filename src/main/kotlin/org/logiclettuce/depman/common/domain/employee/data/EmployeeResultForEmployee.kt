package org.logiclettuce.depman.common.domain.employee.data

data class EmployeeResultForEmployee (
    val id: Long,
    val fullName: String,
    val departments: List<EmployeeDepartmentResult>
)