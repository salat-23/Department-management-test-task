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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Employee) return false

        if (fullName != other.fullName) return false
        if (id != other.id) return false
        if (user != other.user) return false
        return junctions == other.junctions
    }

    override fun hashCode(): Int {
        var result = fullName.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + user.hashCode()
        result = 31 * result + junctions.hashCode()
        return result
    }
}