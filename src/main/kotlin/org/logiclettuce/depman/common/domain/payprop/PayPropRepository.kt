package org.logiclettuce.depman.common.domain.payprop

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PayPropRepository: JpaRepository<PayProp, Long> {
}