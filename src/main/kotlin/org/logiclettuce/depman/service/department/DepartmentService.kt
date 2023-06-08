package org.logiclettuce.depman.service.department

import org.logiclettuce.depman.api.admin.department.dto.DepartmentGenericResponse
import org.logiclettuce.depman.api.admin.employee.dto.EmployeeGenericResponse
import org.logiclettuce.depman.common.domain.department.Department
import org.logiclettuce.depman.common.domain.department.DepartmentDao
import org.logiclettuce.depman.common.domain.department.DepartmentRepository
import org.logiclettuce.depman.common.domain.user.User
import org.logiclettuce.depman.configuration.CacheConfiguration
import org.logiclettuce.depman.error.exception.EntityNotFoundException
import org.logiclettuce.depman.security.configuration.UserRole
import org.logiclettuce.depman.service.user.UserService
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.Optional
import javax.transaction.Transactional

@Service
class DepartmentService(
    protected val departmentRepository: DepartmentRepository,
    protected val userService: UserService,
    protected val departmentDao: DepartmentDao
) {

    @Cacheable(cacheNames = [CacheConfiguration.DEPARTMENT_ENTITY_CACHE_NAME], key = "#id")
    fun findDepartmentById(id: Long): Optional<Department> = departmentRepository.findById(id)

    fun findDepartmentByHead(headUser: User): Optional<Department> =
        departmentRepository.findByHead(headUser)


    @Transactional
    fun createDepartment(
        name: String,
        code: String,
        headId: Long
    ): Department {
        // search only for active users
        var headToAssign =
            userService.findUserByIdAndActive(headId)
                .orElseThrow { EntityNotFoundException("User with id: $headId was not found") }
        if (userService.isHeadOfDepartment(headToAssign)) throw AlreadyAssignedAsHeadException("User with id: $headId is already head of another department")

        // add head role to assigned user
        headToAssign = userService.makeHead(headToAssign)

        val department = Department(name, code)
        department.head = headToAssign

        return departmentRepository.save(department)
    }

    @Transactional
    @CachePut(cacheNames = [CacheConfiguration.DEPARTMENT_ENTITY_CACHE_NAME], key = "#id")
    fun editDepartment(
        id: Long,
        name: String,
        code: String,
        headId: Long
    ): Department {
        // search only for active users
        val headToAssign =
            userService.findUserByIdAndActive(headId)
                .orElseThrow { EntityNotFoundException("User with id: $headId was not found") }
        if (userService.isHeadOfDepartment(headToAssign)) throw AlreadyAssignedAsHeadException("User with id: $headId is already head of another department")

        val existingDepartment = departmentRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Department with id: $id was not found") }

        // change roles of heads if needed
        if (headId != existingDepartment.head.id) {
            userService.removeHead(existingDepartment.head)
            userService.makeHead(headToAssign)
        }

        existingDepartment.name = name
        existingDepartment.code = code
        existingDepartment.head = headToAssign

        return departmentRepository.save(existingDepartment)
    }

    fun getAll(): List<Any> {
        val authentication = SecurityContextHolder.getContext().authentication

        val authorities = authentication.authorities.map { it.authority }

        if (User.hasRole(authorities, UserRole.EMPLOYEE))
            return departmentDao.findAllForEmployee()

        return departmentRepository.findAll().map { d -> DepartmentGenericResponse(d) }
    }

}