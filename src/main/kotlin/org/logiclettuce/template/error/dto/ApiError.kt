package org.logiclettuce.template.error.dto

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import org.logiclettuce.template.util.extensions.EmptyObject
import org.logiclettuce.template.util.extensions.compact
import org.springframework.validation.BindingResult
import java.time.LocalDateTime


/**
 * Class representing error returned from this API.
 *
 * This class is used for serializing into json format representing error DTO.
 *
 * @property errors Collection representing multiple errors that occurred during request parsing.
 * @property firstError Property representing first error from [errors].
 * @property timestamp Property representing timestamp when error occurred.
 */
class ApiError(
    @ArraySchema(schema = Schema(implementation = ObjectErrorCompact::class, nullable = false))
    val errors: Collection<ObjectErrorCompact>,
    @Schema(implementation = ObjectErrorCompact::class, nullable = true)
    val firstError: Any = errors.firstOrNull() ?: EmptyObject,
    @Schema(implementation = LocalDateTime::class)
    val timestamp: String = LocalDateTime.now().toString(),
) {
    /**
     * [Companion] object of [ApiError] holding helper methods.
     */
    companion object {
        /**
         * Convert a [Exception] to instance of [ApiError].
         *
         * @return ApiError instance.
         */
        fun fromException(ex: Exception) = ApiError(
            errors = listOf(
                ObjectErrorCompact(
                    defaultMessage = ex.localizedMessage ?: "Unknown error",
                    code = ex::class.simpleName ?: "UnknownException"
                )
            )
        )

        /**
         * Convert a [BindingResult] to instance of [ApiError].
         *
         * @return ApiError instance.
         */
        fun fromBindingResult(br: BindingResult) = ApiError(
            errors = br.allErrors.compact
        )
    }
}
