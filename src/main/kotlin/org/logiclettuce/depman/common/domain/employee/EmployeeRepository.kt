package org.logiclettuce.depman.common.domain.employee

import org.logiclettuce.depman.common.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository: JpaRepository<Employee, Long> {

    fun existsByUser(user: User): Boolean

}