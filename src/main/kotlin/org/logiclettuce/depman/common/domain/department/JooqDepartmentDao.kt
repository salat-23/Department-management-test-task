package org.logiclettuce.depman.common.domain.department

import org.jooq.DSLContext
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
}