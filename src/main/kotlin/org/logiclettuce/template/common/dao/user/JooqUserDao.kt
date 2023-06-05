package org.logiclettuce.template.common.dao.user

import org.logiclettuce.database.Tables.*
import org.jooq.DSLContext
import org.jooq.SelectConditionStep
import org.jooq.SelectJoinStep
import org.logiclettuce.template.security.configuration.RoleEnum
import org.springframework.stereotype.Component

@Component
class JooqUserDao(
    val context: DSLContext
) : UserDao {
    override fun findByEmail(email: String): User? =
        getOneUserBySingleFilter { context -> context.where(USERS.EMAIL.eq(email)) }
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
            .fetchArray().map { RoleEnum.valueOf(it.value1()) }
        user.roleList = userRoles.toMutableList()
        return user
    }
}
