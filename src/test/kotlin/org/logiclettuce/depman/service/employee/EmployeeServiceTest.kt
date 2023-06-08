package org.logiclettuce.depman.service.employee

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.logiclettuce.depman.api.admin.employee.dto.CreateEmployeeDepartmentJunction
import org.logiclettuce.depman.api.admin.employee.dto.CreateEmployeeRequest
import org.logiclettuce.depman.api.admin.employee.dto.PayPropRequest
import org.logiclettuce.depman.common.domain.department.Department
import org.logiclettuce.depman.common.domain.employee.Employee
import org.logiclettuce.depman.common.domain.employee.EmployeeRepository
import org.logiclettuce.depman.common.domain.payprop.PayMethod
import org.logiclettuce.depman.common.domain.payprop.PayPropRepository
import org.logiclettuce.depman.common.domain.user.User
import org.logiclettuce.depman.common.domain.userdepartmentjunction.CurrencyType
import org.logiclettuce.depman.common.domain.userdepartmentjunction.EmployeeDepartmentJunctionRepository
import org.logiclettuce.depman.service.department.DepartmentService
import org.logiclettuce.depman.service.user.UserService
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class EmployeeServiceTest {

    @Mock
    private lateinit var userService: UserService

    @Mock
    lateinit var departmentService: DepartmentService

    @Mock
    lateinit var employeeDepartmentJunctionRepository: EmployeeDepartmentJunctionRepository

    @Mock
    lateinit var employeeRepository: EmployeeRepository

    @Mock
    lateinit var payPropRepository: PayPropRepository

    @Test
    fun `given valid employee with several payprops for 1 department when calling createEmployee then should create an employee`() {
        val employeeService = EmployeeService(
            userService,
            departmentService,
            employeeDepartmentJunctionRepository,
            employeeRepository,
            payPropRepository
        )

        val userId = 1L
        val user = User("test_login", "test_email", "test_password", true, userId)

        val departmentId = 2L
        val department = Department("test_department", "test_code", departmentId)

        val payPropsToCreate = listOf(
            PayPropRequest("99999", PayMethod.WEBMONEY),
            PayPropRequest("111111", PayMethod.QIWI)
        )
        val createEmployeeRequest = CreateEmployeeRequest(
            userId,
            "Test Employee",
            listOf(
                CreateEmployeeDepartmentJunction(
                    departmentId,
                    1000,
                    CurrencyType.USD,
                    payPropsToCreate
                )
            )
        )

        `when`(userService.findUserById(userId)).thenReturn(Optional.of(user))
        `when`(departmentService.findDepartmentById(departmentId)).thenReturn(Optional.of(department))
        `when`(employeeRepository.save(Mockito.any(Employee::class.java))).thenAnswer { it.getArgument(0) }

        val employee = employeeService.createEmployee(createEmployeeRequest)

        // created employee with correct name
        assertEquals("Test Employee", employee.fullName)
        verify(employeeRepository).save(any())
    }
}