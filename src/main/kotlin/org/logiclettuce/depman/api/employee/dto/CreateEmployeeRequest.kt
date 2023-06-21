package org.logiclettuce.depman.api.employee.dto

import org.logiclettuce.depman.common.domain.userdepartmentjunction.CurrencyType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateEmployeeRequest(
    @NotNull val userId: Long,
    @NotBlank val fullName: String,
    val departments: List<CreateEmployeeDepartmentJunction>
)