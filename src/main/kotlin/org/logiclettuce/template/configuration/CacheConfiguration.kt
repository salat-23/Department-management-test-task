package org.logiclettuce.template.configuration

import org.logiclettuce.template.configuration.properties.LoggingCacheProperties
import org.logiclettuce.template.util.extensions.clearByNames
import org.logiclettuce.template.util.loggerDelegate
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@EnableCaching
@Configuration
class CacheConfiguration(
    protected val cacheManager: CacheManager,
    protected val loggingCacheProperties: LoggingCacheProperties,
) {
    private val logger by loggerDelegate()

    companion object {
        const val USER_ENTITY_CACHE_NAME: String = "USER_ENTITY_CACHE"
        const val USER_ENTITY_CACHE_CLEAR_RATE: Long = 1200000L
    }

    @Scheduled(fixedRate = USER_ENTITY_CACHE_CLEAR_RATE)
    fun scheduledClearUserEntityCache() {
        if (loggingCacheProperties.userEntityLogging)
            logger.info("Clearing cache: {$USER_ENTITY_CACHE_NAME} every {$USER_ENTITY_CACHE_CLEAR_RATE} ms")
        cacheManager.clearByNames(USER_ENTITY_CACHE_NAME)
    }
}
