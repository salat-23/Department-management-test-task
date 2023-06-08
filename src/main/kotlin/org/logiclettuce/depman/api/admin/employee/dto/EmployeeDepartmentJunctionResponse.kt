package org.logiclettuce.depman.api.admin.employee.dto

import org.logiclettuce.depman.api.admin.department.dto.DepartmentGenericResponse
import org.logiclettuce.depman.common.domain.userdepartmentjunction.CurrencyType
import org.logiclettuce.depman.common.domain.userdepartmentjunction.EmployeeDepartmentJunction

data class EmployeeDepartmentJunctionResponse (
    val wageRate: Long,
    val currency: CurrencyType,
    val department: DepartmentGenericResponse,
    val payProps: List<PayPropResponse>
) {
    constructor(departmentJunction: EmployeeDepartmentJunction): this(
        departmentJunction.wageRate,
        departmentJunction.currency,
        DepartmentGenericResponse(departmentJunction.department),
        departmentJunction.payProps.map { PayPropResponse(it) }
    )
}