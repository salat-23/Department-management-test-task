package org.logiclettuce.depman.common.domain.employee.data

import org.logiclettuce.depman.common.domain.payprop.PayMethod

data class PayPropResult(
    val id: Long,
    val value: String,
    val type: PayMethod
)