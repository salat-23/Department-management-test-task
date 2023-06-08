package org.logiclettuce.depman.api.employee.dto

import org.logiclettuce.depman.common.domain.userdepartmentjunction.CurrencyType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateEmployeeDepartmentJunction(
    @NotBlank val departmentId: Long,
    @NotBlank val wageRate: Long,
    @NotBlank val currency: CurrencyType,
    @NotNull val payProps: List<PayPropRequest>
)
