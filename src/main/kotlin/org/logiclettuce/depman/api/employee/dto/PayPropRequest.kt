package org.logiclettuce.depman.api.employee.dto

import org.logiclettuce.depman.common.domain.payprop.PayMethod

data class PayPropRequest (
    val value: String,
    val method: PayMethod
)