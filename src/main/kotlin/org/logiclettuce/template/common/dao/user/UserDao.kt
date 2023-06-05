@file:JvmName("UserDao")

package org.logiclettuce.template.common.dao.user

interface UserDao {
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?

}
