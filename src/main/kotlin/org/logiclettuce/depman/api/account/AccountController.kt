package org.logiclettuce.depman.api.account

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.logiclettuce.depman.api.account.dto.CreateAccountRequest
import org.logiclettuce.depman.api.account.dto.EditAccountRequest
import org.logiclettuce.depman.api.common.dto.UserGenericResponse
import org.logiclettuce.depman.api.user.auth.dto.TokenResponse
import org.logiclettuce.depman.error.dto.ApiError
import org.logiclettuce.depman.service.user.UserService
import org.logiclettuce.depman.util.AnyResponseEntity
import org.logiclettuce.depman.util.loggerDelegate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException

@RestController
@RequestMapping("/api/admin/account")
@PreAuthorize("hasAnyRole('ADMIN')")
class AccountController(
    private val userService: UserService
) {

    private val logger by loggerDelegate()

    @Operation(
        summary = "Create new user account",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successfully created new account",
            content = [Content(schema = Schema(implementation = UserGenericResponse::class))],
        ), ApiResponse(
            responseCode = "409",
            description = "User account with provided login/email already exists",
        )
    )
    // I decided to not create idempotent PUT version, POST only should be fine
    @PostMapping
    fun createAccount(@RequestBody createAccountRequest: CreateAccountRequest): AnyResponseEntity {
        return try {
            val createdUser = userService.createUser(
                createAccountRequest.login,
                createAccountRequest.email,
                createAccountRequest.password,
                createAccountRequest.roles
            )
            logger.info("Created user: [${createdUser.id}]${createdUser.login}")
            ResponseEntity.ok(UserGenericResponse(createdUser))//return
        } catch (alreadyExistsEx: EntityExistsException) {
            logger.error("Failed to create user: ${createAccountRequest.login}")
            ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.fromException(alreadyExistsEx))//return
        }
    }

    @Operation(
        summary = "Edit user account",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successfully applied changes",
            content = [Content(schema = Schema(implementation = UserGenericResponse::class))],
        )
    )
    @PutMapping("/{id}")
    fun editAccount(@PathVariable id: Long, @RequestBody editAccountRequest: EditAccountRequest): AnyResponseEntity {
        return try {
            val editedUser = userService.editUser(
                id,
                editAccountRequest.login,
                editAccountRequest.email,
                editAccountRequest.roles
            )
            logger.info("Edited user: [${editedUser.id}]${editedUser.login}")
            ResponseEntity.ok(UserGenericResponse(editedUser))//return
        } catch (notFoundEx: EntityNotFoundException) {
            logger.error("Could not find user with id: $id")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.fromException(notFoundEx))//return
        }
    }

    // todo: too much boilerplate, fix later if important
    @Operation(
        summary = "Block user account",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successfully blocked account",
            content = [Content(schema = Schema(implementation = UserGenericResponse::class))],
        )
    )
    @PutMapping("/block/{id}")
    fun blockAccount(@PathVariable id: Long): AnyResponseEntity {
        return try {
            val blockedUser = userService.changeAccountStatus(id, false)
            logger.info("Blocked user: [${blockedUser.id}]${blockedUser.login}")
            ResponseEntity.ok(UserGenericResponse(blockedUser))//return
        } catch (notFoundEx: EntityNotFoundException) {
            logger.error("Could not find user with id: $id")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.fromException(notFoundEx))//return
        }
    }

    @Operation(
        summary = "Unblock user account",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successfully unblocked account",
            content = [Content(schema = Schema(implementation = UserGenericResponse::class))],
        )
    )
    @PutMapping("/unblock/{id}")
    fun unblockAccount(@PathVariable id: Long): AnyResponseEntity {
        return try {
            val unblockedUser = userService.changeAccountStatus(id, true)
            logger.info("Unblocked user: [${unblockedUser.id}]${unblockedUser.login}")
            ResponseEntity.ok(UserGenericResponse(unblockedUser))//return
        } catch (notFoundEx: EntityNotFoundException) {
            logger.error("Could not find user with id: $id")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.fromException(notFoundEx))//return
        }
    }

}