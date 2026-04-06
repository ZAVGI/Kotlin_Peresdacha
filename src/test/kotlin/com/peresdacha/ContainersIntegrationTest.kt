package com.peresdacha

import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import kotlin.test.assertTrue

class ContainersIntegrationTest {
    @Test
    fun `postgres container starts and exposes jdbc url`() {
        PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
            withDatabaseName("app")
            withUsername("app")
            withPassword("app")
            start()
            try {
                assertTrue(jdbcUrl.contains("jdbc:postgresql"))
            } finally {
                stop()
            }
        }
    }

    @Test
    fun `rabbitmq container starts and exposes amqp port`() {
        RabbitMQContainer("rabbitmq:3.13-management-alpine").apply {
            start()
            try {
                assertTrue(amqpPort > 0)
            } finally {
                stop()
            }
        }
    }
}
