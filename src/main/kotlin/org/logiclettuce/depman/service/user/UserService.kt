package org.logiclettuce.depman.service.user

import org.logiclettuce.depman.common.domain.role.Role
import org.logiclettuce.depman.common.domain.user.User
import org.logiclettuce.depman.common.domain.user.UserDao
import org.logiclettuce.depman.common.domain.user.UserRepository
import org.logiclettuce.depman.configuration.CacheConfiguration
import org.logiclettuce.depman.security.configuration.UserRole
import org.logiclettuce.depman.service.department.AlreadyAssignedAsHeadException
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Optional
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
class UserService(
    protected val userDao: UserDao,
    protected val userRepository: UserRepository,
    protected val passwordEncoder: PasswordEncoder
) {
    @Cacheable(cacheNames = [CacheConfiguration.USER_ENTITY_CACHE_NAME], key = "#login")
    fun findUserByLogin(login: String): Optional<User> = userRepository.findByLogin(login)

    @Cacheable(cacheNames = [CacheConfiguration.USER_ENTITY_CACHE_NAME], key = "#id")
    fun findUserById(id: Long): Optional<User> = userRepository.findById(id)

    @Cacheable(cacheNames = [CacheConfiguration.USER_ENTITY_CACHE_NAME], key = "#id")
    fun findUserByIdAndActive(id: Long): Optional<User> = userRepository.findByIdAndActive(id)
    fun isHeadOfDepartment(user: User): Boolean = userDao.isHeadOfDepartment(user)
    fun isEmployee(user: User): Boolean = userDao.isEmployee(user)

    @Transactional
    @CachePut(cacheNames = [CacheConfiguration.DEPARTMENT_ENTITY_CACHE_NAME], key = "#user.id")
    fun makeHead(user: User): User {
        val headRole = user createRole UserRole.HEAD
        if (user.roles.contains(headRole)) throw AlreadyAssignedAsHeadException("Provided user with id: ${user.id} already has HEAD role")
        user.addRole(headRole)
        return userRepository.save(user)
    }

    @Transactional
    @CachePut(cacheNames = [CacheConfiguration.DEPARTMENT_ENTITY_CACHE_NAME], key = "#user.id")
    fun removeHead(user: User): User {
        user.removeRole(UserRole.HEAD)
        return userRepository.save(user)
    }

    @Transactional
    @CachePut(cacheNames = [CacheConfiguration.DEPARTMENT_ENTITY_CACHE_NAME], key = "#user.id")
    fun makeEmployee(user: User): User {
        val employeeRole = user createRole UserRole.EMPLOYEE
        if (user.roles.contains(employeeRole)) throw AlreadyAssignedAsHeadException("Provided user with id: ${user.id} already has EMPLOYEE role")
        user.addRole(employeeRole)
        return userRepository.save(user)
    }

    @Transactional
    @CachePut(cacheNames = [CacheConfiguration.DEPARTMENT_ENTITY_CACHE_NAME], key = "#user.id")
    fun removeEmployee(user: User): User {
        user.removeRole(UserRole.EMPLOYEE)
        return userRepository.save(user)
    }

    @Transactional
    fun createUser(
        login: String,
        email: String,
        password: String,
        roles: List<UserRole>
    ): User {
        if (userRepository.existsByLoginOrEmail(
                login,
                email
            )
        ) throw EntityExistsException("User with this login/email already exists: $login/$email")
        val user = User(login, email, passwordEncoder.encode(password), true)
        for (role in roles) {
            val roleId = Role.UserRoleId(user.id, role)
            val roleEntity = Role(roleId)
            user.addRole(roleEntity)
        }
        return userRepository.save(user)
    }

    @Transactional
    @CachePut(cacheNames = [CacheConfiguration.DEPARTMENT_ENTITY_CACHE_NAME], key = "#id")
    fun editUser(
        id: Long,
        login: String,
        email: String,
        roles: List<UserRole>
    ): User {
        val user =
            userRepository.findById(id).orElseThrow { EntityNotFoundException("User with this id[$id] not found") }
        user.login = login
        user.email = email
        user.reassignRoles(roles)
        return userRepository.save(user)
    }

    @Transactional
    @CachePut(cacheNames = [CacheConfiguration.DEPARTMENT_ENTITY_CACHE_NAME], key = "#id")
    fun changeAccountStatus(id: Long, active: Boolean): User {
        val user =
            userRepository.findById(id).orElseThrow { EntityNotFoundException("User with this id[$id] not found") }
        user.active = active
        return userRepository.save(user)
    }

}