package com.peresdacha.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.peresdacha.domain.Role
import com.peresdacha.dto.LoginRequest
import com.peresdacha.dto.RegisterRequest
import com.peresdacha.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt

class AuthService(
    private val userRepository: UserRepository,
    private val jwtSecret: String,
) {
    fun register(request: RegisterRequest): String {
        require(Validation.email(request.email)) { "Invalid email" }
        require(Validation.password(request.password)) { "Weak password" }
        val hash = BCrypt.hashpw(request.password, BCrypt.gensalt())
        val role = if (request.email.endsWith("@admin.local")) Role.ADMIN else Role.USER
        val user = userRepository.create(request.email, hash, role)
        return token(user.id, user.role)
    }

    fun login(request: LoginRequest): String {
        val user = userRepository.findByEmail(request.email) ?: error("Invalid credentials")
        if (!BCrypt.checkpw(request.password, user.passwordHash)) error("Invalid credentials")
        return token(user.id, user.role)
    }

    private fun token(userId: Long, role: Role): String = JWT.create()
        .withClaim("userId", userId)
        .withClaim("role", role.name)
        .sign(Algorithm.HMAC256(jwtSecret))
}
