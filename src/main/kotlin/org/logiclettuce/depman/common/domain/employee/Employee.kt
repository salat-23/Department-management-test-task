package org.logiclettuce.depman.common.domain.employee

import org.logiclettuce.depman.common.domain.user.User
import org.logiclettuce.depman.common.domain.userdepartmentjunction.EmployeeDepartmentJunction
import javax.persistence.*

@Entity
@Table(name = "employees")
class Employee(
    @Column(name = "full_name")
    var fullName: String,
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null
) {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    @OneToMany(mappedBy = "employee")
    var junctions: MutableList<EmployeeDepartmentJunction> = mutableListOf()
}