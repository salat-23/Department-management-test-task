package org.logiclettuce.depman.common.domain.role

import org.logiclettuce.depman.common.domain.user.User
import org.logiclettuce.depman.security.configuration.UserRole
import java.io.Serializable
import javax.persistence.*

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
    ) : Serializable

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    override fun toString(): String {
        return id!!.role.toString()
    }
}