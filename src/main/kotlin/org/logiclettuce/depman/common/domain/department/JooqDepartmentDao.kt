package org.logiclettuce.depman.common.domain.department

import org.jooq.DSLContext
import org.logiclettuce.depman.common.domain.department.data.DepartmentResultForEmployee
import org.logiclettuce.depman.database.Tables.*
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JooqDepartmentDao(
    val context: DSLContext
) : DepartmentDao {
    override fun findDepartmentByHeadId(headId: Long): Optional<Department> {
        val optDepartment =
            context
                .select()
                .from(DEPARTMENTS)
                .where(DEPARTMENTS.HEAD_ID.eq(headId))
                .fetchOne()?.into(Department::class.java)
        return Optional.ofNullable(optDepartment)
    }

    override fun findAllForEmployee(): List<DepartmentResultForEmployee> {
        val foundDepartments = context
            .select(DEPARTMENTS.ID, DEPARTMENTS.NAME, DEPARTMENTS.CODE, USERS.LOGIN)
            .from(DEPARTMENTS)
            .join(USERS).on(USERS.ID.eq(DEPARTMENTS.HEAD_ID))
            .fetch()

        val departments = mutableListOf<DepartmentResultForEmployee>()
        for (record in foundDepartments)
            departments += DepartmentResultForEmployee(
                record.value1(),
                record.value2(),
                record.value3(),
                record.value4()
            )
        return departments
    }
}