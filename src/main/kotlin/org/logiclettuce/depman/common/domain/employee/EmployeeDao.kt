package org.logiclettuce.depman.common.domain.employee

import org.logiclettuce.depman.common.domain.employee.data.EmployeeResultForHead

interface EmployeeDao {
    fun findAllByDepartmentIdForHead(departmentId: Long): List<EmployeeResultForHead>
}