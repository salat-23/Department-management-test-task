package org.logiclettuce.depman.common.domain.user

import org.jooq.DSLContext
import org.jooq.Query
import org.jooq.SelectConditionStep
import org.jooq.SelectJoinStep
import org.logiclettuce.depman.database.Tables.*
import org.logiclettuce.depman.security.configuration.UserRole
import org.logiclettuce.depman.util.loggerDelegate
import org.springframework.stereotype.Repository

@Repository
class JooqUserDao(
    val context: DSLContext
) : UserDao {

    private val logger by loggerDelegate()
    override fun findByLogin(login: String): User? =
        getOneUserBySingleFilter { context -> context.where(USERS.LOGIN.eq(login)) }

    override fun createUser(user: User): User {
        val insertedUser = context.insertInto(USERS)
            .columns(USERS.LOGIN, USERS.EMAIL, USERS.PASSWORD, USERS.ACTIVE)
            .values(user.login, user.email, user.password, user.active)
            .returning().fetchOne()!!.into(User::class.java)

        val queries = mutableListOf<Query>()
        for (role in user.roleList) {
            context.insertInto(USERS_ROLES)
                .columns(USERS_ROLES.USER_ID, USERS_ROLES.ROLE)
                .values(insertedUser.id, role.toString())
        }
        context.batch(queries).execute()
        insertedUser.roleList = user.roleList
        return insertedUser
    }

    override fun isHeadOfDepartment(user: User): Boolean {
        return context.fetchExists(
            context.select().from(DEPARTMENTS).where(DEPARTMENTS.HEAD_ID.eq(user.id))
        )
    }

    override fun isEmployee(user: User): Boolean {
        return context.fetchExists(
            context.select().from(EMPLOYEES).where(EMPLOYEES.USER_ID.eq(user.id))
        )
    }

    override fun findById(id: Long): User? =
        getOneUserBySingleFilter { context -> context.where(USERS.ID.eq(id)) }

    private fun getOneUserBySingleFilter(filter: (step: SelectJoinStep<*>) -> SelectConditionStep<*>): User? {
        val joinStep = context
            .select().from(USERS)
        val user = filter(joinStep)
            .fetchOne()?.into(User::class.java) ?: return null

        val userRoles = context
            .select(USERS_ROLES.ROLE).from(USERS_ROLES)
            .where(USERS_ROLES.USER_ID.eq(user.id))
            .fetchArray().map { UserRole.valueOf(it.value1()) }
        user.roleList = userRoles.toMutableList()

        logger.info(user.roleList.joinToString(" "))

        return user
    }
}
