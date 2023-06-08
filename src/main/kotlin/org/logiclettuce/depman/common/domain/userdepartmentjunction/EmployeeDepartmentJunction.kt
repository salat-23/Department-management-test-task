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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmployeeDepartmentJunction) return false

        if (wageRate != other.wageRate) return false
        if (currency != other.currency) return false
        if (id != other.id) return false
        if (employee != other.employee) return false
        if (department != other.department) return false
        return payProps == other.payProps
    }

    override fun hashCode(): Int {
        var result = wageRate.hashCode()
        result = 31 * result + currency.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + employee.hashCode()
        result = 31 * result + department.hashCode()
        result = 31 * result + payProps.hashCode()
        return result
    }

}