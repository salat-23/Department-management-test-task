package org.logiclettuce.depman.common.domain.payprop

import org.logiclettuce.depman.common.domain.userdepartmentjunction.EmployeeDepartmentJunction
import javax.persistence.*

@Entity
@Table(name = "pay_props")
class PayProp(
    @Enumerated(EnumType.STRING)
    @Column(name="type")
    var method: PayMethod,
    var value: String,
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null
) {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_department_junction_id", nullable = false)
    lateinit var junction: EmployeeDepartmentJunction
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PayProp) return false

        if (method != other.method) return false
        if (value != other.value) return false
        if (id != other.id) return false
        return junction == other.junction
    }

    override fun hashCode(): Int {
        var result = method.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + junction.hashCode()
        return result
    }
}