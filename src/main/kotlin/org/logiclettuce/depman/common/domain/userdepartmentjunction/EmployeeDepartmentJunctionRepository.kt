package org.logiclettuce.depman.common.domain.userdepartmentjunction

import org.logiclettuce.depman.common.domain.department.Department
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface EmployeeDepartmentJunctionRepository: JpaRepository<EmployeeDepartmentJunction, Long> {
    fun findByDepartmentAndEmployeeId(department: Department, employeeId: Long): Optional<EmployeeDepartmentJunction>
    fun existsByDepartmentAndEmployeeId(department: Department, employeeId: Long): Boolean
}