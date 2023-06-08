package org.logiclettuce.depman.common.domain.user

import org.logiclettuce.depman.common.domain.role.Role
import org.logiclettuce.depman.security.configuration.UserRole
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "users")
class User(
    var login: String,
    var email: String,
    var password: String,
    var active: Boolean,
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null
) {
    @Transient
    var roleList: MutableList<UserRole> = mutableListOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    var roles: MutableList<Role> = mutableListOf()

    companion object {
        fun hasRole(userRoles: List<String>, role: UserRole): Boolean {
            return userRoles.contains(UserRole.getRoleIdentifier(role))
        }
    }

    fun addRole(role: Role) {
        roles.add(role)
        role.user = this
    }

    fun addRole(role: UserRole) {
        addRole(this createRole role)
    }

    fun removeRole(role: UserRole) {
        roles.removeIf { roleItem ->
            val userRole = roleItem.id?.role ?: return@removeIf false
            return@removeIf userRole == role
        }
    }

    fun removeRole(role: Role) {
        if (role.id?.role != null) removeRole(role.id!!.role!!)
    }

    fun reassignRoles(roles: List<UserRole>) {
        this.roles.clear()
        for (role in roles) addRole(role)
    }

    infix fun createRole(userRole: UserRole): Role = Role(Role.UserRoleId(this.id, userRole))
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (login != other.login) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (active != other.active) return false
        if (id != other.id) return false
        if (roleList != other.roleList) return false
        return roles == other.roles
    }

    override fun hashCode(): Int {
        var result = login.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + active.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + roleList.hashCode()
        result = 31 * result + roles.hashCode()
        return result
    }
}
