package org.logiclettuce.depman.security.configuration

import org.logiclettuce.depman.common.domain.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(user: User) : UserDetails {
    val login: String = user.login
    private val password: String = user.password
    private val active = user.active
    private val authorities: MutableList<GrantedAuthority> = user.roles
        .map { role -> SimpleGrantedAuthority(UserRole.getRoleIdentifier(role)) }
        .toMutableList()

    override fun getUsername(): String = this.login

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = this.authorities

    override fun getPassword(): String = this.password

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = active
}
