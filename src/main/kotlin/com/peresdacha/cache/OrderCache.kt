package com.peresdacha.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.SetArgs

class OrderCache(redisUri: String) {
    private val client = RedisClient.create(redisUri)

    fun cacheOrder(orderId: Long, payload: String) {
        client.connect().use { connection ->
            connection.sync().set("order:$orderId", payload, SetArgs.Builder.ex(300))
        }
    }

    fun getOrder(orderId: Long): String? = client.connect().use { connection ->
        connection.sync().get("order:$orderId")
    }
}
