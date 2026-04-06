package com.peresdacha.service

object Validation {
    fun email(email: String): Boolean = email.contains("@") && email.length >= 5
    fun password(password: String): Boolean = password.length >= 6
    fun positiveStock(stock: Int): Boolean = stock >= 0
}
