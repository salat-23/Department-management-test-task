package org.logiclettuce.depman.service.employee

import org.logiclettuce.depman.api.admin.employee.dto.*
import org.logiclettuce.depman.common.domain.department.Department
import org.logiclettuce.depman.common.domain.employee.Employee
import org.logiclettuce.depman.common.domain.employee.EmployeeDao
import org.logiclettuce.depman.common.domain.employee.EmployeeRepository
import org.logiclettuce.depman.common.domain.payprop.PayProp
import org.logiclettuce.depman.common.domain.payprop.PayPropRepository
import org.logiclettuce.depman.common.domain.user.User
import org.logiclettuce.depman.common.domain.user.UserRepository
import org.logiclettuce.depman.common.domain.userdepartmentjunction.EmployeeDepartmentJunction
import org.logiclettuce.depman.common.domain.userdepartmentjunction.EmployeeDepartmentJunctionRepository
import org.logiclettuce.depman.security.configuration.UserRole
import org.logiclettuce.depman.service.department.DepartmentService
import org.logiclettuce.depman.service.user.UserService
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
class EmployeeService(
    protected val userService: UserService,
    protected val departmentService: DepartmentService,
    protected val employeeDepartmentJunctionRepository: EmployeeDepartmentJunctionRepository,
    protected val employeeRepository: EmployeeRepository,
    protected val employeeDao: EmployeeDao,
    protected val payPropRepository: PayPropRepository,
    protected val userRepository: UserRepository
) {
    // SOLID seem like non-existent thing here, if there will be spare time - need to refactor everything into separate services


    fun getAllEmployees(): List<Any> {
        val authentication = SecurityContextHolder.getContext().authentication

        // do all necessary checks to be sure that request is valid for the head user
        val user = userService.findUserByLogin(authentication.name)
            .orElseThrow { EntityNotFoundException("Could not retrieve authenticated user") }

        val authorities = authentication.authorities.map { it.authority }

        if (User.hasRole(authorities, UserRole.ADMIN))
            return employeeRepository.findAll().map { EmployeeGenericResponse(it) }

        if (User.hasRole(authorities, UserRole.HEAD)) {
            val headUserDepartment = departmentService.findDepartmentByHead(user)
                .orElseThrow { EntityNotFoundException("Could not retrieve head user department") }
            val depId = headUserDepartment.id!!
            return employeeDao.findAllByDepartmentIdForHead(depId)
        }

        return employeeDao.findAllForEmployee()
    }

    private fun countSameIdMap(departments: List<CreateEmployeeDepartmentJunction>): Map<Long, Int> {
        val resultMap = mutableMapOf<Long, Int>()
        departments.forEach { dep ->
            if (resultMap.containsKey(dep.departmentId)) resultMap[dep.departmentId] =
                resultMap[dep.departmentId]!! + 1
            else resultMap[dep.departmentId] = 1
        }
        return resultMap
    }

    // todo: possible to make batch insert for better performance in case of critical load
    @Transactional
    fun createEmployee(
        createEmployeeRequest: CreateEmployeeRequest
    ): Employee {
        // todo create exceptions for each of these cases
        val userId = createEmployeeRequest.userId
        val user = userService.findUserById(userId)
            .orElseThrow { EntityNotFoundException("User with id: $userId was not found") }

        if (employeeRepository.existsByUser(user)) throw EntityExistsException("Employee with user id: ${user.id} already exists")

        if (createEmployeeRequest.departments.isEmpty()) throw IllegalArgumentException("At least 1 department must be present")

        val countIdMap = countSameIdMap(createEmployeeRequest.departments)
        countIdMap.entries.firstOrNull { it.value > 1 }?.let {
            throw IllegalArgumentException("Employee cannot be added to department with id: ${it.value} ${it.key} times")
        }

        val employee = Employee(createEmployeeRequest.fullName)
        employee.user = user

        userService.makeEmployee(employee.user)

        val employeeJunctions = mutableListOf<EmployeeDepartmentJunction>()
        for (employeeDepartmentJunctionRequest in createEmployeeRequest.departments) {
            val departmentId = employeeDepartmentJunctionRequest.departmentId
            val department = departmentService.findDepartmentById(departmentId)
                .orElseThrow { EntityNotFoundException("Department with id: $departmentId was not found") }

            val employeeDepartmentJunction = EmployeeDepartmentJunction(
                wageRate = employeeDepartmentJunctionRequest.wageRate,
                currency = employeeDepartmentJunctionRequest.currency
            )
            employeeDepartmentJunction.employee = employee
            employeeDepartmentJunction.department = department

            val payPropsToSave = mutableListOf<PayProp>()
            for (payPropRequest in employeeDepartmentJunctionRequest.payProps) {
                val payProp = PayProp(payPropRequest.method, payPropRequest.value)
                payProp.junction = employeeDepartmentJunction
                payPropsToSave += payProp
            }
            employeeJunctions += employeeDepartmentJunctionRepository.save(employeeDepartmentJunction)
            payPropRepository.saveAll(payPropsToSave)
        }
        employee.junctions = employeeJunctions

        return employeeRepository.save(employee)
    }

    @Transactional
    fun editEmployeeGeneric(id: Long, editEmployeeRequest: EditEmployeeRequest): Any {
        val userRoles = SecurityContextHolder.getContext().authentication.authorities.map { it.authority }

        return when {
            User.hasRole(userRoles, UserRole.ADMIN) -> EmployeeGenericResponse(editEmployee(id, editEmployeeRequest))
            User.hasRole(userRoles, UserRole.HEAD) -> editEmployeeAsHead(id, editEmployeeRequest)
            else -> throw AccessDeniedException("User does not have enough permissions. User's roles: $userRoles")
        }
    }

    private fun headRequestContainsInaccessibleDepartmentJunctions(
        validDepartment: Department,
        editEmployeeRequest: EditEmployeeRequest
    ): Boolean {
        return editEmployeeRequest.junctions.any { it.departmentId != validDepartment.id }
    }

    @Transactional
    fun editEmployeeAsHead(id: Long, editEmployeeRequest: EditEmployeeRequest): EmployeeHeadResponse {
        val authentication = SecurityContextHolder.getContext().authentication

        // do all necessary checks to be sure that request is valid for the head user
        val headUser = userService.findUserByLogin(authentication.name)
            .orElseThrow { EntityNotFoundException("Could not retrieve authenticated user") }
        val headUserDepartment = departmentService.findDepartmentByHead(headUser)
            .orElseThrow { EntityNotFoundException("Could not retrieve head user department") }

        val existingEmployee = employeeRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Employee with id: $id was not found") }

        userService.makeEmployee(existingEmployee.user)

        if (headRequestContainsInaccessibleDepartmentJunctions(headUserDepartment, editEmployeeRequest))
            throw IllegalArgumentException("Cannot edit departments where you are not a head")

        if (editEmployeeRequest.junctions.size != 1) throw IllegalArgumentException("1 department must be specified")

        val departmentJunction =
            employeeDepartmentJunctionRepository.findByDepartmentAndEmployeeId(headUserDepartment, id)
                .orElseThrow { EntityNotFoundException("Could not retrieve employee department info with department id: ${headUserDepartment.id} and employee id: $id") }

        val newJunction = editEmployeeRequest.junctions.first()
        departmentJunction.wageRate = newJunction.wageRate
        departmentJunction.currency = newJunction.currency

        // delete removed payprops
        val payPropsToRemove = departmentJunction.payProps.filterNot { payProp ->
            newJunction.payProps.any { it.value == payProp.value && it.method == payProp.method }
        }

        departmentJunction.payProps.removeAll(payPropsToRemove)

        // add new payprops

        val payPropsToCreate = newJunction.payProps
            .filterNot { payProp -> departmentJunction.payProps.any { it.value == payProp.value && it.method == payProp.method } }

        val payPropsToSave = payPropsToCreate.map { payPropRequest ->
            PayProp(payPropRequest.method, payPropRequest.value).also {
                it.junction = departmentJunction
            }
        }

        employeeDepartmentJunctionRepository.save(departmentJunction)
        departmentJunction.payProps += payPropRepository.saveAll(payPropsToSave)

        return EmployeeHeadResponse(employeeRepository.save(existingEmployee), departmentJunction)
    }


    @Transactional
    fun editEmployee(id: Long, editEmployeeRequest: EditEmployeeRequest): Employee {
        val employee = employeeRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Employee with id: $id was not found") }

        editEmployeeRequest.junctions.takeIf { it.isNotEmpty() }
            ?: throw IllegalArgumentException("At least 1 department must be present")

        editEmployeeRequest.userId.takeIf { it != employee.user.id }?.let { userId ->
            val user = userService.findUserById(userId)
                .orElseThrow { EntityNotFoundException("User with id: $userId was not found") }
            employee.user = user
            userService.makeEmployee(employee.user)
        }

        val countIdMap = countSameIdMap(editEmployeeRequest.junctions)
        countIdMap.entries.firstOrNull { it.value > 1 }?.let {
            throw IllegalArgumentException("Employee cannot be added to department with id: ${it.value} ${it.key} times")
        }

        val junctionsToRemove = employee.junctions.filterNot { junction ->
            editEmployeeRequest.junctions.any { it.departmentId == junction.department.id }
        }

        employee.junctions.removeAll(junctionsToRemove)
        employeeDepartmentJunctionRepository.deleteAll(junctionsToRemove)

        for (employeeDepartmentJunctionRequest in editEmployeeRequest.junctions) {
            val departmentId = employeeDepartmentJunctionRequest.departmentId

            val departmentJunction = employee.junctions.find { it.department.id == departmentId } ?: run {
                val department = departmentService.findDepartmentById(departmentId)
                    .orElseThrow { EntityNotFoundException("Department with id: $departmentId was not found") }
                EmployeeDepartmentJunction(
                    wageRate = employeeDepartmentJunctionRequest.wageRate,
                    currency = employeeDepartmentJunctionRequest.currency,
                ).apply {
                    this.employee = employee
                    this.department = department
                }
            }

            departmentJunction.wageRate = employeeDepartmentJunctionRequest.wageRate
            departmentJunction.currency = employeeDepartmentJunctionRequest.currency

            val payPropsToRemove = departmentJunction.payProps.filterNot { payProp ->
                employeeDepartmentJunctionRequest.payProps.any { it.value == payProp.value && it.method == payProp.method }
            }

            departmentJunction.payProps.removeAll(payPropsToRemove)
            payPropRepository.deleteAll(payPropsToRemove)

            val payPropsToCreate = employeeDepartmentJunctionRequest.payProps
                .filterNot { payProp -> departmentJunction.payProps.any { it.value == payProp.value && it.method == payProp.method } }

            val payPropsToSave = payPropsToCreate.map { payPropRequest ->
                PayProp(payPropRequest.method, payPropRequest.value).also {
                    it.junction = departmentJunction
                }
            }

            employeeDepartmentJunctionRepository.save(departmentJunction)
            departmentJunction.payProps += payPropRepository.saveAll(payPropsToSave)
        }
        return employeeRepository.save(employee)
    }

}