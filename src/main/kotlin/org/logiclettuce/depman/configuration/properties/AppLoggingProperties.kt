package org.logiclettuce.depman.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "app.logging.cache")
@ConstructorBinding
class LoggingCacheProperties(
    val userEntityLogging: Boolean = false
)
