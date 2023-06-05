package org.logiclettuce.template.security.filter

import org.logiclettuce.template.security.service.JwtAccessService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Custom JWT Authentication filter
 *
 * Custom implementation which parses Bearer token from Authorization header
 */
@Component
class JwtAuthFilter(
    private val jwtAccessService: JwtAccessService,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // if no token found, continue the filter chain
        val token = request.extractJwt() ?: return filterChain.doFilter(request, response)
        try {
            // if token is invalid, throw exception
            if (!jwtAccessService.validateToken(token)) throw BadCredentialsException("Invalid token")
            // extract email otherwise throw exception (previous validation should be covering this, but just in case)
            val claims =
                jwtAccessService.getClaimsFromToken(token) ?: throw BadCredentialsException("Invalid token")

            val userDetails: UserDetails = userDetailsService.loadUserByUsername(claims.subject)

            if (!userDetails.isEnabled) throw DisabledException("Account disabled")

            val usernameAndPasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.authorities
            )
            SecurityContextHolder.getContext().authentication = usernameAndPasswordAuthenticationToken
        } catch (ex: Exception) {
            /**
             * Setting attribute so that [org.logiclettuce.template.security.configuration.JwtAuthEntryPoint]
             * can proceed with error handling
             */
            request.setAttribute("exception", ex)
        }
        filterChain.doFilter(request, response)
    }
}

private fun HttpServletRequest.extractJwt(): String? {
    // Return null if authorization header is null
    val authorizationHeader: String = this.getHeader("Authorization") ?: return null
    // We need to check if header is in Bearer {token} form
    return if (authorizationHeader.startsWith("Bearer ")) {
        authorizationHeader.substring(7, authorizationHeader.length)
    } else null
}
