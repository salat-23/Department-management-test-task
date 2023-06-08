package org.logiclettuce.depman.api.admin.department.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateDepartmentRequest(
    @NotBlank val name: String,
    @NotBlank val code: String,
    @NotNull val headId: Long
)