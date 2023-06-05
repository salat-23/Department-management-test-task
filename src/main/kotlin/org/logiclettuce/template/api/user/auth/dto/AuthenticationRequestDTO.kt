package org.logiclettuce.template.api.user.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class AuthenticationRequestDTO(
    @field:Schema(description = "Valid formatted e-mail address")
    @field:NotBlank(message = "E-Mail field cannot be blank")
    @field:Email
    val email: String,

    @field:NotBlank(message = "Password field cannot be blank")
    val password: String
)
