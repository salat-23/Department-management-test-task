package org.logiclettuce.template.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Class used for delegating instance of [Logger]
 *
 * @property R instance of thisRef, class from which we're delegating [Logger]
 */
private class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, Logger> {
    private lateinit var instance: Logger

    override operator fun getValue(thisRef: R, property: KProperty<*>): Logger =
        if (this::instance.isInitialized)
            instance
        else
            LoggerFactory.getLogger(thisRef::class.java).also { logger ->
                instance = logger
            }
}

/**
 * Function used for delegating [LoggerDelegate]
 *
 * @param T Instance of class from which we're delegating [Logger]
 */
fun <T : Any> loggerDelegate(): ReadOnlyProperty<T, Logger> = LoggerDelegate()
