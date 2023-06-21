package org.logiclettuce.depman.common.domain.department

import org.logiclettuce.depman.common.domain.user.User
import org.logiclettuce.depman.common.domain.userdepartmentjunction.EmployeeDepartmentJunction
import jakarta.persistence.*

@Entity
@Table(name = "departments")
class Department(
    var name: String,
    var code: String,
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null
) {
    // todo: this relation looks weird but ok
    @OneToOne
    @JoinColumn(name = "head_id")
    lateinit var head: User

    @OneToMany(mappedBy = "department")
    var junctions: MutableList<EmployeeDepartmentJunction> = mutableListOf()
}