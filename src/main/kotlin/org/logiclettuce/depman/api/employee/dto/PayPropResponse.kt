package org.logiclettuce.depman.api.employee.dto

import org.logiclettuce.depman.common.domain.payprop.PayMethod
import org.logiclettuce.depman.common.domain.payprop.PayProp

data class PayPropResponse(
    val value: String,
    val method: PayMethod
) {
    constructor(payProp: PayProp): this(payProp.value, payProp.method)
}