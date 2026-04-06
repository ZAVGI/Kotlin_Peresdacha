package com.peresdacha.repository

import com.peresdacha.db.table.AuditLogsTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class AuditRepository {
    fun log(userId: Long?, action: String, payload: String) {
        transaction {
            AuditLogsTable.insert {
                it[AuditLogsTable.userId] = userId
                it[AuditLogsTable.action] = action
                it[AuditLogsTable.payload] = payload
                it[AuditLogsTable.createdAt] = Instant.now()
            }
        }
    }
}
