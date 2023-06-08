package org.logiclettuce.depman.service.user

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.logiclettuce.depman.common.domain.user.User
import org.logiclettuce.depman.common.domain.user.UserDao
import org.logiclettuce.depman.common.domain.user.UserRepository
import org.logiclettuce.depman.security.configuration.UserRole
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import javax.persistence.EntityExistsException

@ExtendWith(MockitoExtension::class)
class UserServiceTest {


    @Mock
    lateinit var userDao: UserDao
    @Mock
    lateinit var userRepository: UserRepository
    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `given valid new user when call createUser should create new user`() {
        val userService = UserService(userDao, userRepository, passwordEncoder)

        val testLogin = "salat23"
        val testEmail = "salat@mail.ru"
        val testPassword = "testpassword"
        val testRoles = listOf(UserRole.MEMBER)

        `when`(userRepository.existsByLoginOrEmail(testLogin, testEmail)).thenReturn(false)
        `when`(userRepository.save(any())).thenAnswer { it.arguments.first() }
        `when`(passwordEncoder.encode(testPassword)).thenAnswer { it.arguments.first() }

        val createdUser = userService.createUser(testLogin, testEmail, testPassword, testRoles)
        assertEquals(testLogin, createdUser.login)
        assertEquals(testEmail, createdUser.email)
        assertEquals(testPassword, createdUser.password)
        assertEquals(testRoles, createdUser.roles.map { it.id!!.role })

        verify(userRepository).save(any())
    }

    @Test
    fun `given duplicate login and email new user when call createUser should fail`() {
        val userService = UserService(userDao, userRepository, passwordEncoder)

        val testLogin: String = "salat23"
        val testEmail: String = "salat@mail.ru"
        val testPassword = "testpassword"
        val testRoles = listOf(UserRole.MEMBER)

        `when`(userRepository.existsByLoginOrEmail(testLogin, testEmail)).thenReturn(false)
        `when`(userRepository.save(any())).thenAnswer { it.arguments.first() }
        `when`(passwordEncoder.encode(testPassword)).thenAnswer { it.arguments.first() }

        org.junit.jupiter.api.assertThrows<EntityExistsException> {
            val createdUser1 = userService.createUser(testLogin, testEmail, testPassword, testRoles)
            `when`(userRepository.existsByLoginOrEmail(testLogin, testEmail)).thenReturn(true)
            val createdUser2 = userService.createUser(testLogin, testEmail, testPassword, testRoles)
        }
    }

    @Test
    fun `given valid user when call editUser should edit user`() {
        val userService = UserService(userDao, userRepository, passwordEncoder)

        val testLogin = "salat23"
        val testEmail = "salat@mail.ru"
        val testPassword = "testpassword"
        val testChangedLogin = "salat11"
        val testChangedEmail = "salat11g@mail.com"
        val testChangedRoles = listOf(UserRole.MEMBER, UserRole.ADMIN)

        val existingUser = User(testLogin, testEmail, testPassword, true, 1)
        existingUser.addRole(UserRole.MEMBER)

        `when`(userRepository.findById(1)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.save(any())).thenAnswer { it.arguments.first() }

        val editedUser = userService.editUser(1, testChangedLogin, testChangedEmail, testChangedRoles)
        assertEquals(testChangedLogin, editedUser.login)
        assertEquals(testChangedEmail, editedUser.email)
        assertEquals(testChangedRoles, editedUser.roles.map { it.id!!.role })

        verify(userRepository).save(any())
    }
}