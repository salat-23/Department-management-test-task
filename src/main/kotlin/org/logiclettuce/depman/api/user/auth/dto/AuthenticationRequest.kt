package org.logiclettuce.depman.api.user.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class AuthenticationRequest(
    @field:NotBlank(message = "Login field cannot be blank")
    val login: String,

    @field:NotBlank(message = "Password field cannot be blank")
    val password: String
)
