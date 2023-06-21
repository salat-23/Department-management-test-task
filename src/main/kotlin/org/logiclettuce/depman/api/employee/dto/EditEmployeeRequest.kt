package org.logiclettuce.depman.api.employee.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class EditEmployeeRequest(
    @NotNull val userId: Long,
    @NotBlank val fullName: String,
    val junctions: List<CreateEmployeeDepartmentJunction>
)