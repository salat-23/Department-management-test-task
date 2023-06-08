package org.logiclettuce.depman.security.configuration

import org.logiclettuce.depman.util.loggerDelegate
import org.springframework.security.access.AccessDecisionManager
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.core.Authentication

class LoggingAccessDecisionManager(
    private val delegate: AccessDecisionManager
): AccessDecisionManager {

    private val logger by loggerDelegate()

    override fun decide(
        authentication: Authentication,
        `object`: Any,
        configAttributes: MutableCollection<ConfigAttribute>
    ) {
        logger.info("User name: {}", authentication.name)
        logger.info("User roles: {}", authentication.authorities)
        delegate.decide(authentication, `object`, configAttributes)
    }

    override fun supports(attribute: ConfigAttribute?): Boolean {
        return delegate.supports(attribute)
    }

    override fun supports(`class`: Class<*>): Boolean {
        return delegate.supports(`class`)
    }
}