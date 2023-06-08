package org.logiclettuce.depman.common.domain.employee

import org.jooq.DSLContext
import org.jooq.impl.DSL.multisetAgg
import org.logiclettuce.depman.common.domain.employee.data.EmployeeResultForHead
import org.logiclettuce.depman.common.domain.employee.data.PayPropResult
import org.logiclettuce.depman.common.domain.payprop.PayMethod
import org.logiclettuce.depman.database.Tables.*
import org.springframework.stereotype.Repository

@Repository
class JooqEmployeeDao(
    val context: DSLContext
) : EmployeeDao {
    override fun findAllByDepartmentIdForHead(departmentId: Long): List<EmployeeResultForHead> {
        val employeesFound =
            context
                //EMPLOYEES.ID, EMPLOYEES.FULL_NAME, DSL.array(PAY_PROPS.asterisk())
                .select(
                    EMPLOYEES.ID,
                    EMPLOYEES.FULL_NAME,
                    multisetAgg(PAY_PROPS.ID, PAY_PROPS.VALUE, PAY_PROPS.TYPE).`as`("pay_props")
                )
                .from(EMPLOYEES)
                .join(EMPLOYEE_DEPARTMENT_JUNCTIONS).on(EMPLOYEE_DEPARTMENT_JUNCTIONS.EMPLOYEE_ID.eq(EMPLOYEES.ID))
                .join(DEPARTMENTS).on(DEPARTMENTS.ID.eq(EMPLOYEE_DEPARTMENT_JUNCTIONS.DEPARTMENT_ID))
                .join(PAY_PROPS).on(PAY_PROPS.EMPLOYEE_DEPARTMENT_JUNCTION_ID.eq(EMPLOYEE_DEPARTMENT_JUNCTIONS.ID))
                .where(DEPARTMENTS.ID.eq(departmentId))
                .groupBy(EMPLOYEES.ID, EMPLOYEES.FULL_NAME)
                .fetch()


        val employees = mutableListOf<EmployeeResultForHead>()
        for (record in employeesFound) {
            val payProps = record.value3().map { PayPropResult(it[0] as Long, it[1] as String, PayMethod.valueOf(it[2] as String)) }
            val employee = EmployeeResultForHead(record.value1(), record.value2(), payProps)
            employees += employee
        }
        return employees
    }


}