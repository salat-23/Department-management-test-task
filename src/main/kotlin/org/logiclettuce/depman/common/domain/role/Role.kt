package org.logiclettuce.depman.common.domain.role

import org.logiclettuce.depman.common.domain.user.User
import org.logiclettuce.depman.security.configuration.UserRole
import java.io.Serializable
import jakarta.persistence.*

@Entity
@Table(name = "users_roles")
class Role(
    @EmbeddedId
    val id: UserRoleId? = null
) {
    @Embeddable
    class UserRoleId(
        var userId: Long? = null,
        @Enumerated(EnumType.STRING)
        var role: UserRole? = null
    ) : Serializable {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UserRoleId

            if (userId != other.userId) return false
            return role == other.role
        }

        override fun hashCode(): Int {
            var result = userId?.hashCode() ?: 0
            result = 31 * result + (role?.hashCode() ?: 0)
            return result
        }
    }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    override fun toString(): String {
        return id!!.role.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Role

        if (id != other.id) return false
        return user == other.user
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + user.hashCode()
        return result
    }
}