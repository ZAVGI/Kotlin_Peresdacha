package com.peresdacha.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

enum class Role { USER, ADMIN }
enum class OrderStatus { CREATED, CANCELLED }

data class User(
    val id: Long,
    val email: String,
    val passwordHash: String,
    val role: Role,
)

data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
)

data class OrderItem(val productId: Long, val quantity: Int, val priceSnapshot: Double)

data class Order(
    val id: Long,
    val userId: Long,
    val status: OrderStatus,
    val createdAt: Instant = Clock.System.now(),
    val items: List<OrderItem> = emptyList(),
)
