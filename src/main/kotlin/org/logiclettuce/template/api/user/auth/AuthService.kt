package org.logiclettuce.template.api.user.auth

import org.logiclettuce.template.api.user.auth.dto.AuthenticationRequestDTO
import org.logiclettuce.template.api.user.auth.dto.TokenResponseDTO
import org.logiclettuce.template.error.exception.UserNotFoundException
import org.logiclettuce.template.security.service.JwtAccessService
import org.logiclettuce.template.security.service.JwtRefreshService
import org.logiclettuce.template.util.principalDelegate
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AuthService(
    protected val jwtAccessService: JwtAccessService,
    protected val jwtRefreshService: JwtRefreshService,
    protected val userDetailsService: UserDetailsService,
    protected val userService: org.logiclettuce.template.api.user.auth.UserCachingService,
    protected val authManager: AuthenticationManager,
) {
    fun whoAmI(): org.logiclettuce.template.api.user.auth.dto.WhoAmIDTO {
        val principal by principalDelegate()
        val email = principal.email
        val user =
            userService.findUserByEmail(email)
                ?: throw UserNotFoundException("User by specified email [$email] not found")
        return org.logiclettuce.template.api.user.auth.dto.WhoAmIDTO.fromUser(user)
    }

    fun authenticate(dto: AuthenticationRequestDTO) = authenticate(dto.email, dto.password)
    fun authenticate(email: String, password: String): TokenResponseDTO {
        // throws exception if failed
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                email, password
            )
        )
        return generateTokensByEmail(email)
    }

    fun generateTokensByEmail(email: String): TokenResponseDTO {
        // load userDetails from database
        val userDetails = userDetailsService.loadUserByUsername(email)
        // generate access token
        val accessToken: String = jwtAccessService.generateToken(userDetails)
        // generate longer lasting refresh token
        val refreshToken: String = jwtRefreshService.generateToken(userDetails)

        return org.logiclettuce.template.api.user.auth.dto.TokenResponseDTO(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun generateTokensFromJwsRefreshToken(jws: String): org.logiclettuce.template.api.user.auth.dto.TokenResponseDTO {
        // throw exception if token is invalid
        if (!jwtRefreshService.validateToken(jws))
            throw BadCredentialsException("Invalid refresh token")

        // fetch userDetails by subject parsed from refresh token
        val subject = jwtRefreshService.getClaimsFromToken(jws)?.subject
        val userDetails = userDetailsService.loadUserByUsername(subject)

        // throw if user's account is disabled otherwise return new token
        if (!userDetails.isEnabled) throw DisabledException("Account disabled")

        return org.logiclettuce.template.api.user.auth.dto.TokenResponseDTO(
            accessToken = jwtAccessService.generateToken(userDetails),
            refreshToken = jwtRefreshService.generateToken(userDetails)
        )
    }
}
