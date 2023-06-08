package org.logiclettuce.depman.service.department

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.logiclettuce.depman.common.domain.department.DepartmentDao
import org.logiclettuce.depman.common.domain.department.DepartmentRepository
import org.logiclettuce.depman.common.domain.user.User
import org.logiclettuce.depman.security.configuration.UserRole
import org.logiclettuce.depman.service.user.UserService
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class DepartmentServiceTest {

    @Mock
    lateinit var departmentRepository: DepartmentRepository
    @Mock
    lateinit var userService: UserService
    @Mock
    lateinit var departmentDao: DepartmentDao

    @Test
    fun `given valid department when call createDepartment should create department`() {
        val departmentService = DepartmentService(departmentRepository, userService, departmentDao)

        val testName = "Test department"
        val testCode = "TEST"


        val testHeadUser = User("test", "test@test.com", "test", true, 1)
        testHeadUser.addRole(UserRole.MEMBER)

        `when`(userService.findUserByIdAndActive(1)).thenReturn(Optional.of(testHeadUser))
        `when`(userService.isHeadOfDepartment(testHeadUser)).thenReturn(false)
        `when`(departmentRepository.save(any())).thenAnswer { it.arguments.first() }
        `when`(userService.makeHead(testHeadUser)).thenAnswer { (it.arguments.first() as User).apply { addRole(UserRole.HEAD) } }

        val createdDepartment = departmentService.createDepartment(testName, testCode, testHeadUser.id!!)

        assertEquals(createdDepartment.name, testName)
        assertEquals(createdDepartment.code, testCode)
        assertEquals(createdDepartment.head, testHeadUser)
        verify(departmentRepository).save(any())
    }

}