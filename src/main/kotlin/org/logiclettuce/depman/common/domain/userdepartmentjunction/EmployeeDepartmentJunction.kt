package org.logiclettuce.depman.common.domain.userdepartmentjunction

import org.logiclettuce.depman.common.domain.department.Department
import org.logiclettuce.depman.common.domain.employee.Employee
import org.logiclettuce.depman.common.domain.payprop.PayProp
import javax.persistence.*

@Entity
@Table(name = "employee_department_junctions")
class EmployeeDepartmentJunction(
    @Column(name = "wage_rate")
    var wageRate: Long,
    @Enumerated(EnumType.STRING)
    var currency: CurrencyType,
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null
) {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    lateinit var employee: Employee

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    lateinit var department: Department

    @OneToMany(mappedBy = "junction")
    var payProps: MutableList<PayProp> = mutableListOf()

}