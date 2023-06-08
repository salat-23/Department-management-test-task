package org.logiclettuce.depman.api.user.auth.dto

import javax.validation.constraints.NotBlank

class RefreshRequest(
    @field:NotBlank(message = "(Refresh) token field cannot be blank")
    val token: String
)
