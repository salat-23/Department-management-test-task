package org.logiclettuce.template.util.extensions

import org.springframework.security.config.web.servlet.CorsDsl
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import javax.servlet.http.HttpServletRequest

inline fun CorsDsl.configurationSource(crossinline block: (HttpServletRequest) -> CorsConfiguration?) {
    this.configurationSource = CorsConfigurationSource {
        block(it)
    }
}

inline fun corsConfiguration(block: CorsConfiguration.() -> Unit) =
    CorsConfiguration().apply {
        block()
    }
