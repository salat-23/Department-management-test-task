package org.logiclettuce.depman.service.authentication

import org.logiclettuce.depman.api.user.auth.dto.AuthenticationRequest
import org.logiclettuce.depman.api.user.auth.dto.TokenResponse
import org.logiclettuce.depman.api.user.auth.dto.WhoAmIResponse
import org.logiclettuce.depman.error.exception.UserNotFoundException
import org.logiclettuce.depman.security.service.JwtAccessService
import org.logiclettuce.depman.security.service.JwtRefreshService
import org.logiclettuce.depman.service.user.UserService
import org.logiclettuce.depman.util.principalDelegate
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    protected val jwtAccessService: JwtAccessService,
    protected val jwtRefreshService: JwtRefreshService,
    protected val userDetailsService: UserDetailsService,
    protected val userService: UserService,
    protected val authManager: AuthenticationManager,
) {
    fun whoAmI(): WhoAmIResponse {
        val principal by principalDelegate()
        val login = principal.login
        val user =
            userService.findUserByLogin(login)
                .orElseThrow { UserNotFoundException("User by specified login [$login] not found") }
        return WhoAmIResponse.fromUser(user)
    }

    fun authenticate(dto: AuthenticationRequest) = authenticate(dto.login, dto.password)
    fun authenticate(email: String, password: String): TokenResponse {
        // throws exception if failed
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                email, password
            )
        )
        return generateTokensByEmail(email)
    }

    fun generateTokensByEmail(email: String): TokenResponse {
        // load userDetails from database
        val userDetails = userDetailsService.loadUserByUsername(email)
        // generate access token
        val accessToken: String = jwtAccessService.generateToken(userDetails)
        // generate longer lasting refresh token
        val refreshToken: String = jwtRefreshService.generateToken(userDetails)

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun generateTokensFromJwsRefreshToken(jws: String): TokenResponse {
        // throw exception if token is invalid
        if (!jwtRefreshService.validateToken(jws))
            throw BadCredentialsException("Invalid refresh token")

        // fetch userDetails by subject parsed from refresh token
        val subject = jwtRefreshService.getClaimsFromToken(jws)?.subject
        val userDetails = userDetailsService.loadUserByUsername(subject)

        // throw if user's account is disabled otherwise return new token
        if (!userDetails.isEnabled) throw DisabledException("Account disabled")

        return TokenResponse(
            accessToken = jwtAccessService.generateToken(userDetails),
            refreshToken = jwtRefreshService.generateToken(userDetails)
        )
    }
}
