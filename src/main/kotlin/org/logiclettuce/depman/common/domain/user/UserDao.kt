package org.logiclettuce.depman.common.domain.user

interface UserDao {
    fun findById(id: Long): User?
    fun findByLogin(login: String): User?
    fun createUser(user: User): User
    fun isHeadOfDepartment(user: User): Boolean
    fun isEmployee(user: User): Boolean

}
