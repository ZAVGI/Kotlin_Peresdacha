package com.peresdacha.worker

import com.rabbitmq.client.ConnectionFactory
import org.slf4j.LoggerFactory

class OrderWorker(rabbitHost: String) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val queueName = "order-events"
    private val factory = ConnectionFactory().apply { host = rabbitHost }

    fun run() {
        val connection = factory.newConnection()
        val channel = connection.createChannel()
        channel.queueDeclare(queueName, true, false, false, null)
        channel.basicConsume(queueName, true) { _, delivery ->
            val body = String(delivery.body)
            log.info("Order event received: $body")
            log.info("Fake email sent for event=$body")
        } { _ -> }
    }
}
