package com.peresdacha

import com.peresdacha.service.Validation
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationUnitTest {
    @Test
    fun `email validation accepts valid email`() {
        assertTrue(Validation.email("test@example.com"))
    }

    @Test
    fun `password validation rejects short password`() {
        assertFalse(Validation.password("123"))
    }

    @Test
    fun `stock validation allows zero and positive`() {
        assertTrue(Validation.positiveStock(0))
        assertTrue(Validation.positiveStock(10))
    }
}
