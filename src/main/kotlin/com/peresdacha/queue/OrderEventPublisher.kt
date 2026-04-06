package com.peresdacha.queue

import com.rabbitmq.client.ConnectionFactory

class OrderEventPublisher(rabbitHost: String) {
    private val queueName = "order-events"
    private val factory = ConnectionFactory().apply { host = rabbitHost }

    fun publish(event: String) {
        factory.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                channel.queueDeclare(queueName, true, false, false, null)
                channel.basicPublish("", queueName, null, event.toByteArray())
            }
        }
    }
}
