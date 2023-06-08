package org.logiclettuce.depman.api.admin.department

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.logiclettuce.depman.api.admin.department.dto.CreateDepartmentRequest
import org.logiclettuce.depman.api.admin.department.dto.DepartmentGenericResponse
import org.logiclettuce.depman.api.admin.department.dto.EditDepartmentRequest
import org.logiclettuce.depman.api.common.dto.UserGenericResponse
import org.logiclettuce.depman.error.dto.ApiError
import org.logiclettuce.depman.service.department.AlreadyAssignedAsHeadException
import org.logiclettuce.depman.service.department.DepartmentService
import org.logiclettuce.depman.util.AnyResponseEntity
import org.logiclettuce.depman.util.loggerDelegate
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
import javax.persistence.EntityNotFoundException

@RestController
@RequestMapping("/api/department")
class DepartmentController(
    private val departmentService: DepartmentService
) {

    private val logger by loggerDelegate()

    @Operation(
        summary = "Create new department",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successfully created new department",
            content = [Content(schema = Schema(implementation = DepartmentGenericResponse::class))],
        ), ApiResponse(
            responseCode = "409",
            description = "Assignee is already a head of another department",
        ), ApiResponse(
            responseCode = "404",
            description = "Assignee was not found",
        )
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    fun createDepartment(@RequestBody createDepartmentRequest: CreateDepartmentRequest): AnyResponseEntity {
        return try {
            val newDepartment = departmentService.createDepartment(
                createDepartmentRequest.name,
                createDepartmentRequest.code,
                createDepartmentRequest.headId,
            )
            logger.info("Created new department [${newDepartment.id}]${newDepartment.code}")
            ResponseEntity.ok(DepartmentGenericResponse(newDepartment)) // return
        } catch (headNotFound: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.fromException(headNotFound))
        } catch (userAlreadyAssignedAsHead: AlreadyAssignedAsHeadException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.fromException(userAlreadyAssignedAsHead))
        }
    }

    @Operation(
        summary = "Edit department",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successfully applied changes",
            content = [Content(schema = Schema(implementation = DepartmentGenericResponse::class))],
        ), ApiResponse(
            responseCode = "409",
            description = "Assignee is already a head of another department",
        ), ApiResponse(
            responseCode = "404",
            description = "Assignee was not found",
        )
    )
    @PutMapping("/{id}")
    fun editDepartment(
        @PathVariable id: Long,
        @RequestBody editDepartmentRequest: EditDepartmentRequest
    ): AnyResponseEntity {
        return try {
            val editedDepartment = departmentService.editDepartment(
                id,
                editDepartmentRequest.name,
                editDepartmentRequest.code,
                editDepartmentRequest.headId,
            )
            logger.info("Successfully changed department [${editedDepartment.id}]${editedDepartment.code}")
            ResponseEntity.ok(DepartmentGenericResponse(editedDepartment)) // return
        } catch (headNotFound: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.fromException(headNotFound))
        } catch (userAlreadyAssignedAsHead: AlreadyAssignedAsHeadException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.fromException(userAlreadyAssignedAsHead))
        }
    }

    /*
    todo: shit code, fix if there will be spare time left
     can also implement pagination if needed
    */
    @Operation(
        summary = "Get list of all departments",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successfully applied changes",
            content = [Content(schema = Schema(implementation = Array<DepartmentGenericResponse>::class))],
        )
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    fun getDepartments(): AnyResponseEntity {
        return ResponseEntity.ok(departmentService.getAll())
    }
}