package org.logiclettuce.depman.common.domain.employee.data

data class EmployeeResultForHead(
    val id: Long,
    val fullName: String,
    val payProps: List<PayPropResult>
)
