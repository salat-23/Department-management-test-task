package org.logiclettuce.depman.common.domain.department

import org.logiclettuce.depman.common.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface DepartmentRepository: JpaRepository<Department, Long> {
    fun findByHead(head: User): Optional<Department>
}