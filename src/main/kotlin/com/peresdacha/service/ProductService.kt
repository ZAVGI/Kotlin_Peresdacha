package com.peresdacha.service

import com.peresdacha.cache.OrderCache
import com.peresdacha.domain.Product
import com.peresdacha.repository.ProductRepository

class ProductService(
    private val productRepository: ProductRepository,
    private val cache: OrderCache,
) {
    fun all(): List<Product> = productRepository.findAll()
    fun byId(id: Long): Product = productRepository.findById(id) ?: error("Product not found")
    fun create(product: Product): Product = productRepository.create(product)

    fun update(id: Long, product: Product) {
        if (!productRepository.update(id, product)) error("Product not found")
        cache.cacheOrder(id, "invalidate")
    }

    fun delete(id: Long) {
        if (!productRepository.delete(id)) error("Product not found")
        cache.cacheOrder(id, "invalidate")
    }
}
