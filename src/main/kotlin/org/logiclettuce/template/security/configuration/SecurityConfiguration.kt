package org.logiclettuce.template.security.configuration

import org.logiclettuce.template.security.filter.JwtAuthFilter
import org.logiclettuce.template.security.service.CustomUserDetailsService
import org.logiclettuce.template.util.extensions.configurationSource
import org.logiclettuce.template.util.extensions.corsConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfiguration(
    private val userDetailsService: CustomUserDetailsService,
    private val jwtAuthenticationFilter: JwtAuthFilter,
    private val jwtAuthEntryPoint: JwtAuthEntryPoint,
) : WebSecurityConfigurerAdapter() {

    @Bean(name = ["delegatingPasswordEncoder"])
    fun delegatingPasswordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(delegatingPasswordEncoder())
    }

    override fun configure(http: HttpSecurity) {
        http {
            cors {
                configurationSource {
                    corsConfiguration {
                        allowedOrigins = listOf("*")
                        allowedHeaders = listOf("*")
                        allowedMethods = listOf(
                            HttpMethod.GET,
                            HttpMethod.HEAD,
                            HttpMethod.POST,
                            HttpMethod.DELETE,
                            HttpMethod.PUT,
                            HttpMethod.OPTIONS
                        ).map(HttpMethod::toString)
                    }
                }
            }
            csrf { disable() }
            httpBasic { disable() }
            logout { disable() }
            authorizeRequests {
                authorize("/api/auth/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            exceptionHandling {
                authenticationEntryPoint = jwtAuthEntryPoint
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
        }
    }

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
        )
    }

}
