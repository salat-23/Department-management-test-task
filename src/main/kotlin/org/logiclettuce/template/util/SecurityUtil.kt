@file:Suppress("unused")

package org.logiclettuce.template.util

import org.logiclettuce.template.security.configuration.CustomUserDetails
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private class PrincipalDelegate : ReadOnlyProperty<Nothing?, CustomUserDetails> {
    private val instance: CustomUserDetails by lazy {
        SecurityContextHolder.getContext().authentication?.principal as? CustomUserDetails
            ?: throw PrincipalDelegateException("Could not delegate user's principal")
    }

    override fun getValue(thisRef: Nothing?, property: KProperty<*>): CustomUserDetails = instance
}

private class PrincipalDelegateException(message: String?) : AuthenticationException(message)

fun principalDelegate(): ReadOnlyProperty<Nothing?, CustomUserDetails> = PrincipalDelegate()
