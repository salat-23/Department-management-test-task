package org.logiclettuce.depman.api.department.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class EditDepartmentRequest(
    @NotBlank val name: String,
    @NotBlank val code: String,
    @NotNull val headId: Long
)