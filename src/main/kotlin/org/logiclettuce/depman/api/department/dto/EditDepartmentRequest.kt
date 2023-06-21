package org.logiclettuce.depman.api.department.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class EditDepartmentRequest(
    @NotBlank val name: String,
    @NotBlank val code: String,
    @NotNull val headId: Long
)