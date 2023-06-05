package org.logiclettuce.template.error.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

//base
open class BadRequestException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)
open class NotFoundException(message: String) : ResponseStatusException(HttpStatus.NOT_FOUND, message)

// base entity
class EntityNotFoundException(message: String) : NotFoundException(message)
class EntityStateException(message: String) : BadRequestException(message)

//user entity
class UserNotFoundException(message: String) : NotFoundException(message)
class UserStateException(message: String) : BadRequestException(message)

//dto
class DTOStateException(message: String) : BadRequestException(message)
