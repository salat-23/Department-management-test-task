package org.logiclettuce.depman.api.account.dto

import org.logiclettuce.depman.security.configuration.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateAccountRequest(
    @NotBlank val login: String,
    @NotBlank @Email val email: String,
    @NotBlank val password: String,
    @NotNull val roles: List<UserRole>
)