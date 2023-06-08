package org.logiclettuce.depman.api.employee.dto

import org.logiclettuce.depman.common.domain.employee.Employee
import org.logiclettuce.depman.common.domain.userdepartmentjunction.EmployeeDepartmentJunction

data class EmployeeHeadResponse(
    val id: Long,
    val fullName: String,
    val departmentJunction: EmployeeDepartmentJunctionResponse
) {
    constructor(employee: Employee, departmentJunction: EmployeeDepartmentJunction) :
            this(
                employee.id!!,
                employee.fullName,
                EmployeeDepartmentJunctionResponse(departmentJunction)
            )
}