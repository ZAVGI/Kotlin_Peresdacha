package com.peresdacha.repository

import com.peresdacha.db.table.OrderItemsTable
import com.peresdacha.db.table.OrdersTable
import com.peresdacha.domain.Order
import com.peresdacha.domain.OrderItem
import com.peresdacha.domain.OrderStatus
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

class OrderRepository {
    fun create(userId: Long, items: List<OrderItem>): Order = transaction {
        val orderId = OrdersTable.insertAndGetId {
            it[OrdersTable.userId] = userId
            it[OrdersTable.status] = OrderStatus.CREATED.name
            it[OrdersTable.createdAt] = LocalDateTime.now()
        }.value
        items.forEach { item ->
            OrderItemsTable.insertAndGetId {
                it[OrderItemsTable.orderId] = orderId
                it[OrderItemsTable.productId] = item.productId
                it[OrderItemsTable.quantity] = item.quantity
                it[OrderItemsTable.priceSnapshot] = BigDecimal.valueOf(item.priceSnapshot)
            }
        }
        findById(orderId)!!
    }

    fun listByUser(userId: Long): List<Order> = transaction {
        OrdersTable.selectAll().where { OrdersTable.userId.eq(userId) }.map { it.toOrder() }.map { withItems(it) }
    }

    fun findById(id: Long): Order? = transaction {
        OrdersTable.selectAll().where { OrdersTable.id.eq(id) }.singleOrNull()?.toOrder()?.let { withItems(it) }
    }

    fun cancel(orderId: Long, userId: Long): Boolean = transaction {
        OrdersTable.update({ (OrdersTable.id eq orderId) and (OrdersTable.userId eq userId) }) {
            it[OrdersTable.status] = OrderStatus.CANCELLED.name
        } > 0
    }

    fun countCreatedOrders(): Long = transaction {
        OrdersTable.selectAll().where { OrdersTable.status.eq(OrderStatus.CREATED.name) }.count()
    }

    private fun withItems(order: Order): Order {
        val items = OrderItemsTable.selectAll().where { OrderItemsTable.orderId.eq(order.id) }
            .map {
                OrderItem(
                    productId = it[OrderItemsTable.productId].value,
                    quantity = it[OrderItemsTable.quantity],
                    priceSnapshot = it[OrderItemsTable.priceSnapshot].toDouble(),
                )
            }
        return order.copy(items = items)
    }

    private fun ResultRow.toOrder() = Order(
        id = this[OrdersTable.id].value,
        userId = this[OrdersTable.userId].value,
        status = OrderStatus.valueOf(this[OrdersTable.status]),
        createdAt = Instant.fromEpochMilliseconds(this[OrdersTable.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000),
    )
}
