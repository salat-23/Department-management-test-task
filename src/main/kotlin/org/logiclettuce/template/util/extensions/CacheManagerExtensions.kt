@file:Suppress("unused")

package org.logiclettuce.template.util.extensions

import org.springframework.cache.CacheManager

/*
 * clear cache by name from CacheManager
 */
fun CacheManager.clearByNames(vararg names: String) {
    names.forEach { name ->
        this.getCache(name)?.clear()
    }
}

fun <T : Any> CacheManager.clearByNameAndKey(name: String, key: T) {
    val cache = this.getCache(name) ?: return
    cache.evict(key)
}
