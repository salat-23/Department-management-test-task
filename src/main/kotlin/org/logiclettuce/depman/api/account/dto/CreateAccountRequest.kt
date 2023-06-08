package org.logiclettuce.depman.api.account.dto

import org.logiclettuce.depman.security.configuration.UserRole
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateAccountRequest(
    @NotBlank val login: String,
    @NotBlank @Email val email: String,
    @NotBlank val password: String,
    @NotNull val roles: List<UserRole>
)