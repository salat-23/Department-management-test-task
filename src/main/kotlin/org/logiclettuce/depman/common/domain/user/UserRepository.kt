package org.logiclettuce.depman.common.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository: JpaRepository<User, Long> {

    fun findByIdAndActive(id: Long, active: Boolean = true): Optional<User>
    fun findByLogin(login: String): Optional<User>
    fun existsByLoginOrEmail(login: String, email: String): Boolean
}