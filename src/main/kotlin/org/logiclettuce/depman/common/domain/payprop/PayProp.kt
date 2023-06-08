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
}