package com.peresdacha.routes

import com.peresdacha.domain.Product
import com.peresdacha.dto.AuthResponse
import com.peresdacha.dto.CreateOrderRequest
import com.peresdacha.dto.CreateProductRequest
import com.peresdacha.dto.LoginRequest
import com.peresdacha.dto.RegisterRequest
import com.peresdacha.dto.UpdateProductRequest
import com.peresdacha.service.AuthService
import com.peresdacha.service.OrderService
import com.peresdacha.service.ProductService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val token = authService.register(call.receive<RegisterRequest>())
            call.respond(AuthResponse(token))
        }
        post("/login") {
            val token = authService.login(call.receive<LoginRequest>())
            call.respond(AuthResponse(token))
        }
    }
}

fun Route.userRoutes(productService: ProductService, orderService: OrderService) {
    get("/products") { call.respond(productService.all()) }
    get("/products/{id}") {
        call.respond(productService.byId(call.parameters["id"]!!.toLong()))
    }

    authenticate("auth-jwt") {
        post("/orders") {
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.payload.getClaim("userId").asLong()
            val order = orderService.createOrder(userId, call.receive<CreateOrderRequest>())
            call.respond(HttpStatusCode.Created, order)
        }
        get("/orders") {
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.payload.getClaim("userId").asLong()
            call.respond(orderService.listOrders(userId))
        }
        delete("/orders/{id}") {
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.payload.getClaim("userId").asLong()
            orderService.cancelOrder(call.parameters["id"]!!.toLong(), userId)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

fun Route.adminRoutes(productService: ProductService, orderService: OrderService) {
    authenticate("auth-jwt") {
        route("/products") {
            post {
                val req = call.receive<CreateProductRequest>()
                val product = productService.create(Product(0, req.name, req.description, req.price, req.stock))
                call.respond(HttpStatusCode.Created, product)
            }
            put("/{id}") {
                val req = call.receive<UpdateProductRequest>()
                productService.update(call.parameters["id"]!!.toLong(), Product(0, req.name, req.description, req.price, req.stock))
                call.respond(HttpStatusCode.NoContent)
            }
            delete("/{id}") {
                productService.delete(call.parameters["id"]!!.toLong())
                call.respond(HttpStatusCode.NoContent)
            }
        }
        get("/stats/orders") {
            val principal = call.principal<JWTPrincipal>()!!
            if (principal.payload.getClaim("role").asString() != "ADMIN") {
                call.respond(HttpStatusCode.Forbidden)
                return@get
            }
            call.respond(mapOf("createdOrders" to orderService.createdOrdersCount()))
        }
    }
}
