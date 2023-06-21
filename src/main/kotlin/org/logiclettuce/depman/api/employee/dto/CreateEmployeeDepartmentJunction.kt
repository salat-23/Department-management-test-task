package org.logiclettuce.depman.api.employee.dto

import org.logiclettuce.depman.common.domain.userdepartmentjunction.CurrencyType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateEmployeeDepartmentJunction(
    @NotBlank val departmentId: Long,
    @NotBlank val wageRate: Long,
    @NotBlank val currency: CurrencyType,
    @NotNull val payProps: List<PayPropRequest>
)
