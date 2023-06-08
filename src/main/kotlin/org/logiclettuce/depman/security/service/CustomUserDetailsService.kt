package org.logiclettuce.depman.security.service

import org.logiclettuce.depman.common.domain.user.UserRepository
import org.logiclettuce.depman.security.configuration.CustomUserDetails
import org.springframework.context.annotation.Primary
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Primary
@Service
class CustomUserDetailsService(
    protected val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByLogin(username ?: "")
            .orElseThrow { UsernameNotFoundException("User [username: $username] not found") }
        return CustomUserDetails(user)
    }
}

