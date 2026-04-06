package com.peresdacha.repository

import com.peresdacha.db.table.UsersTable
import com.peresdacha.domain.Role
import com.peresdacha.domain.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun create(email: String, passwordHash: String, role: Role = Role.USER): User = transaction {
        val id = UsersTable.insertAndGetId {
            it[UsersTable.email] = email
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.role] = role.name
        }.value
        User(id, email, passwordHash, role)
    }

    fun findByEmail(email: String): User? = transaction {
        UsersTable.selectAll().where { UsersTable.email.eq(email) }.singleOrNull()?.toUser()
    }

    private fun ResultRow.toUser() = User(
        id = this[UsersTable.id].value,
        email = this[UsersTable.email],
        passwordHash = this[UsersTable.passwordHash],
        role = Role.valueOf(this[UsersTable.role]),
    )
}
