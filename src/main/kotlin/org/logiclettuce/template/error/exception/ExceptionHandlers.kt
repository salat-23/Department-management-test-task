package org.logiclettuce.template.error.exception

import org.logiclettuce.template.error.dto.ApiError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.naming.AuthenticationException

@RestControllerAdvice
class ExceptionHandlers {
    private fun exceptionEntity(ex: Exception, status: HttpStatus) =
        ResponseEntity<ApiError>(ApiError.fromException(ex), status)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException::class)
    fun badRequestException(ex: BadRequestException) = exceptionEntity(ex, HttpStatus.BAD_REQUEST)

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    fun notFoundException(ex: NotFoundException) = exceptionEntity(ex, HttpStatus.NOT_FOUND)

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException::class)
    fun usernameNotFoundException(ex: AuthenticationException) = exceptionEntity(ex, HttpStatus.UNAUTHORIZED)
}
