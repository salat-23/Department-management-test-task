package org.logiclettuce.template.api.user.auth

import org.logiclettuce.template.configuration.CacheConfiguration
import org.logiclettuce.template.common.dao.user.User
import org.logiclettuce.template.common.dao.user.UserDao
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class UserCachingService(
    protected val repo: UserDao,
) {
    @Cacheable(cacheNames = [CacheConfiguration.USER_ENTITY_CACHE_NAME], key = "#email")
    fun findUserByEmail(email: String): User? = repo.findByEmail(email)
}
