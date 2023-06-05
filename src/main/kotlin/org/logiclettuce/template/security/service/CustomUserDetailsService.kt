package org.logiclettuce.template.security.service

import org.logiclettuce.template.common.dao.user.UserDao
import org.logiclettuce.template.security.configuration.CustomUserDetails
import org.springframework.context.annotation.Primary
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Primary
@Service
class CustomUserDetailsService(
    protected val repo: UserDao,
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = username?.let { u -> repo.findByEmail(u) }
            ?: throw UsernameNotFoundException("User [username: $username] not found")
        return CustomUserDetails(user)
    }
}

