package org.logiclettuce.template.security.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.logiclettuce.template.error.dto.ApiError
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        val ex = request.getAttribute("exception") as? Exception ?: authException

        val body: ByteArray = ObjectMapper().writeValueAsBytes(
            ApiError.fromException(ex)
        )

        response.outputStream.write(body)
    }
}
