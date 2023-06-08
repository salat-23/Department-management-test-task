package org.logiclettuce.depman.api.admin.employee.dto

import org.logiclettuce.depman.common.domain.userdepartmentjunction.CurrencyType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateEmployeeRequest(
    @NotNull val userId: Long,
    @NotBlank val fullName: String,
    val departments: List<CreateEmployeeDepartmentJunction>
)