package org.logiclettuce.depman.common.domain.department

import java.util.Optional

interface DepartmentDao {
    fun findDepartmentByHeadId(headId: Long): Optional<Department>
}