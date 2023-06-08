package org.logiclettuce.depman.api.user.auth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.logiclettuce.depman.api.user.auth.dto.AuthenticationRequest
import org.logiclettuce.depman.api.user.auth.dto.RefreshRequest
import org.logiclettuce.depman.api.user.auth.dto.TokenResponse
import org.logiclettuce.depman.api.user.auth.dto.WhoAmIResponse
import org.logiclettuce.depman.error.dto.ApiError
import org.logiclettuce.depman.service.authentication.AuthenticationService
import org.logiclettuce.depman.util.AnyResponseEntity
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

//region SwaggerDoc
@Tag(description = "Endpoints for authenticating users.", name = "auth")
//endregion
@RestController
@RequestMapping("/api/auth")
class AuthenticationController(
    protected val authenticationService: AuthenticationService,
) {

    //region SwaggerDoc
    @Operation(
        summary = "Authenticate",
        description = "Authenticated by email and password",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successful authentication",
            content = [(Content(schema = Schema(implementation = TokenResponse::class)))]
        ), ApiResponse(
            responseCode = "400", description = "Invalid DTO"
        ), ApiResponse(
            responseCode = "401",
            description = "Auth error",
        )
    )
    //endregion
    @PostMapping("/login")
    fun createAuthenticationToken(
        @Validated @RequestBody authenticationRequest: AuthenticationRequest,
        br: BindingResult
    ): AnyResponseEntity =
        if (br.hasErrors())
            badRequest().body(ApiError.fromBindingResult(br))
        else
            ok(authenticationService.authenticate(authenticationRequest))


    //region SwaggerDoc
    @Operation(
        summary = "Refresh the tokens",
        description = "Refresh both access and the refresh token with older refresh token",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successful refresh",
            content = [Content(schema = Schema(implementation = TokenResponse::class))],
        ), ApiResponse(
            responseCode = "400", description = "Invalid DTO"
        ), ApiResponse(
            responseCode = "401",
            description = "Invalid token or account disabled",
        )
    )
    //endregion
    @PostMapping("/refresh")
    fun refreshToken(
        @Validated @RequestBody refreshRequest: RefreshRequest,
        br: BindingResult
    ): AnyResponseEntity =
        if (br.hasErrors())
            badRequest().body(ApiError.fromBindingResult(br))
        else
            ok(authenticationService.generateTokensFromJwsRefreshToken(refreshRequest.token))


    //region SwaggerDoc
    @Operation(
        summary = "Get your own info",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "DTO Returned",
            content = [(Content(schema = Schema(implementation = WhoAmIResponse::class)))]
        ), ApiResponse(
            responseCode = "401",
            description = "Authorization error",
        )
    )
    //endregion
    @PreAuthorize("authenticated")
    @GetMapping("/whoami")
    fun whoAmI(): ResponseEntity<WhoAmIResponse> =
        ok(authenticationService.whoAmI())

}
