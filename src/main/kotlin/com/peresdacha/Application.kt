package com.peresdacha

import com.peresdacha.cache.OrderCache
import com.peresdacha.config.configureSecurity
import com.peresdacha.db.DatabaseFactory
import com.peresdacha.dto.ApiError
import com.peresdacha.queue.OrderEventPublisher
import com.peresdacha.repository.AuditRepository
import com.peresdacha.repository.OrderRepository
import com.peresdacha.repository.ProductRepository
import com.peresdacha.repository.UserRepository
import com.peresdacha.routes.adminRoutes
import com.peresdacha.routes.authRoutes
import com.peresdacha.routes.userRoutes
import com.peresdacha.service.AuthService
import com.peresdacha.service.OrderService
import com.peresdacha.service.ProductService
import com.peresdacha.worker.OrderWorker
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respond
import io.ktor.server.routing.routing

fun main() {
    val mode = System.getenv("APP_MODE") ?: "api"
    if (mode == "worker") {
        OrderWorker(System.getenv("RABBIT_HOST") ?: "localhost").run()
        return
    }

    embeddedServer(Netty, port = (System.getenv("PORT") ?: "8080").toInt(), module = Application::module).start(wait = true)
}

fun Application.module() {
    val jdbcUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/app"
    val dbUser = System.getenv("DB_USER") ?: "app"
    val dbPassword = System.getenv("DB_PASSWORD") ?: "app"
    val jwtSecret = System.getenv("JWT_SECRET") ?: "dev-secret"
    val redisUrl = System.getenv("REDIS_URL") ?: "redis://localhost:6379"
    val rabbitHost = System.getenv("RABBIT_HOST") ?: "localhost"

    DatabaseFactory.init(jdbcUrl, dbUser, dbPassword)

    val userRepository = UserRepository()
    val productRepository = ProductRepository()
    val orderRepository = OrderRepository()
    val auditRepository = AuditRepository()
    val cache = OrderCache(redisUrl)
    val eventPublisher = OrderEventPublisher(rabbitHost)

    val authService = AuthService(userRepository, jwtSecret)
    val productService = ProductService(productRepository, cache)
    val orderService = OrderService(orderRepository, productRepository, auditRepository, eventPublisher, cache)

    install(CallLogging)
    install(ContentNegotiation) { json() }
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respond(io.ktor.http.HttpStatusCode.BadRequest, ApiError("BUSINESS_ERROR", cause.message ?: "Unknown"))
        }
        exception<Throwable> { call, cause ->
            this@module.environment.log.error("Unhandled error", cause)
            call.respond(io.ktor.http.HttpStatusCode.InternalServerError, ApiError("INTERNAL", "Unexpected error"))
        }
    }

    configureSecurity(jwtSecret)

    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        authRoutes(authService)
        userRoutes(productService, orderService)
        adminRoutes(productService, orderService)
    }
}
