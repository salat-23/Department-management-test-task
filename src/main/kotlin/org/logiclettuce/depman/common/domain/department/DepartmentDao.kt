package org.logiclettuce.depman.common.domain.department

import org.logiclettuce.depman.common.domain.department.data.DepartmentResultForEmployee
import org.logiclettuce.depman.common.domain.employee.data.EmployeeResultForEmployee
import java.util.Optional

interface DepartmentDao {
    fun findDepartmentByHeadId(headId: Long): Optional<Department>
    fun findAllForEmployee(): List<DepartmentResultForEmployee>
}