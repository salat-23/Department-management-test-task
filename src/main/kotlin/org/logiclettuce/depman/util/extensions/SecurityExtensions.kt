package org.logiclettuce.depman.util.extensions

import org.springframework.security.config.web.servlet.CorsDsl
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import jakarta.servlet.http.HttpServletRequest

inline fun CorsDsl.configurationSource(crossinline block: (HttpServletRequest) -> CorsConfiguration?) {
    this.configurationSource = CorsConfigurationSource {
        block(it)
    }
}

inline fun corsConfiguration(block: CorsConfiguration.() -> Unit) =
    CorsConfiguration().apply {
        block()
    }
