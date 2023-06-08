package org.logiclettuce.depman.api.employee.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class EditEmployeeRequest(
    @NotNull val userId: Long,
    @NotBlank val fullName: String,
    val junctions: List<CreateEmployeeDepartmentJunction>
)