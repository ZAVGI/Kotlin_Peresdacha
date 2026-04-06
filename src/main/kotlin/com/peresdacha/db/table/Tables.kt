package com.peresdacha.db.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp

object UsersTable : LongIdTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20).index()
}

object ProductsTable : LongIdTable("products") {
    val name = varchar("name", 255).index()
    val description = text("description")
    val price = decimal("price", 10, 2)
    val stock = integer("stock")
}

object OrdersTable : LongIdTable("orders") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val status = varchar("status", 20).index()
    val createdAt = timestamp("created_at")
}

object OrderItemsTable : LongIdTable("order_items") {
    val orderId = reference("order_id", OrdersTable, onDelete = ReferenceOption.CASCADE).index()
    val productId = reference("product_id", ProductsTable).index()
    val quantity = integer("quantity")
    val priceSnapshot = decimal("price_snapshot", 10, 2)
}

object AuditLogsTable : LongIdTable("audit_logs") {
    val userId = long("user_id").nullable().index()
    val action = varchar("action", 100).index()
    val payload = text("payload")
    val createdAt = timestamp("created_at")
}
