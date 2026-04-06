package com.peresdacha.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val email: String, val password: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(val token: String)

@Serializable
data class CreateProductRequest(val name: String, val description: String, val price: Double, val stock: Int)

@Serializable
data class UpdateProductRequest(val name: String, val description: String, val price: Double, val stock: Int)

@Serializable
data class CreateOrderItemRequest(val productId: Long, val quantity: Int)

@Serializable
data class CreateOrderRequest(val items: List<CreateOrderItemRequest>)

@Serializable
data class ApiError(val code: String, val message: String)
