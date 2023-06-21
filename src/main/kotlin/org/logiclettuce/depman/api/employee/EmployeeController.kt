package org.logiclettuce.depman.api.employee

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.logiclettuce.depman.api.employee.dto.CreateEmployeeRequest
import org.logiclettuce.depman.api.employee.dto.EditEmployeeRequest
import org.logiclettuce.depman.api.employee.dto.EmployeeGenericResponse
import org.logiclettuce.depman.api.employee.dto.EmployeeHeadResponse
import org.logiclettuce.depman.common.domain.employee.data.EmployeeResultForHead
import org.logiclettuce.depman.error.dto.ApiError
import org.logiclettuce.depman.service.employee.EmployeeService
import org.logiclettuce.depman.util.AnyResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.persistence.EntityNotFoundException

@RestController
@RequestMapping("/api/employee")
class EmployeeController(
    private val employeeService: EmployeeService
) {
    @Operation(
        summary = "Create new employee and assign to departments",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successfully created new employee",
            content = [Content(schema = Schema(implementation = EmployeeGenericResponse::class))],
        ), ApiResponse(
            responseCode = "400",
            description = "Employee cannot be added to the same department twice",
        ), ApiResponse(
            responseCode = "404",
            description = "Department was not found",
        )
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    fun createEmployeesForDepartment(
        @RequestBody createEmployeeRequest: CreateEmployeeRequest
    ): AnyResponseEntity {
        return try {
            // todo remove list make just employee generic response
            val createdEmployee = employeeService.createEmployee(createEmployeeRequest)
            ResponseEntity.ok(EmployeeGenericResponse(createdEmployee)) // return
        } catch (notFound: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.fromException(notFound))
        } catch (illegalArgument: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.fromException(illegalArgument))
        }
    }

    @Operation(
        summary = "Edit employee",
        description = "Returns generic response if edited employee as admin and head response if edited as a department head"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successfully applied changes.",
            content = [
                Content(schema = Schema(oneOf = [EmployeeGenericResponse::class, EmployeeHeadResponse::class]))
            ],
        ), ApiResponse(
            responseCode = "400",
            description = "Employee cannot be added to the same department twice",
        ), ApiResponse(
            responseCode = "404",
            description = "Department was not found",
        )
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HEAD')")
    fun editEmployee(@PathVariable id: Long, @RequestBody editEmployeeRequest: EditEmployeeRequest): AnyResponseEntity {
        return try {
            // todo remove list make just employee generic response
            val editedEmployeeResponse = employeeService.editEmployeeGeneric(id, editEmployeeRequest)
            ResponseEntity.ok(editedEmployeeResponse) // return
        } catch (notFound: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.fromException(notFound))
        } catch (illegalArgument: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.fromException(illegalArgument))
        }
    }

    @Operation(
        summary = "Get all employees and their info such as assigned departments and pay props",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Returns list of all employees available",
            content = [Content(schema = Schema(anyOf = [Array<EmployeeGenericResponse>::class, Array<EmployeeResultForHead>::class]))]
        )
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'HEAD')")
    fun getAll(): AnyResponseEntity {
        return ResponseEntity.ok(employeeService.getAllEmployees())
    }
}