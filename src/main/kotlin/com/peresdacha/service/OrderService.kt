package com.peresdacha.service

import com.peresdacha.cache.OrderCache
import com.peresdacha.domain.Order
import com.peresdacha.domain.OrderItem
import com.peresdacha.dto.CreateOrderRequest
import com.peresdacha.queue.OrderEventPublisher
import com.peresdacha.repository.AuditRepository
import com.peresdacha.repository.OrderRepository
import com.peresdacha.repository.ProductRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val auditRepository: AuditRepository,
    private val eventPublisher: OrderEventPublisher,
    private val cache: OrderCache,
) {
    fun createOrder(userId: Long, request: CreateOrderRequest): Order {
        val items = request.items.map {
            val product = productRepository.findById(it.productId) ?: error("Product ${it.productId} not found")
            if (product.stock < it.quantity) error("Not enough stock for product ${it.productId}")
            if (!productRepository.decreaseStock(it.productId, it.quantity)) error("Stock update failed")
            OrderItem(it.productId, it.quantity, product.price)
        }
        val order = orderRepository.create(userId, items)
        auditRepository.log(userId, "ORDER_CREATED", "orderId=${order.id}")
        eventPublisher.publish("ORDER_CREATED:${order.id}:$userId")
        cache.cacheOrder(order.id, Json.encodeToString(order.toString()))
        return order
    }

    fun listOrders(userId: Long): List<Order> = orderRepository.listByUser(userId)

    fun cancelOrder(orderId: Long, userId: Long) {
        if (!orderRepository.cancel(orderId, userId)) error("Order not found or no access")
        auditRepository.log(userId, "ORDER_CANCELLED", "orderId=$orderId")
        eventPublisher.publish("ORDER_CANCELLED:$orderId:$userId")
    }

    fun createdOrdersCount(): Long = orderRepository.countCreatedOrders()
}
