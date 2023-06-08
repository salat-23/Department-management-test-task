package org.logiclettuce.depman.api.admin.account.dto

import org.logiclettuce.depman.security.configuration.UserRole
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class EditAccountRequest (
    @NotBlank val login: String,
    @NotBlank @Email val email: String,
    @NotNull val roles: List<UserRole>
)