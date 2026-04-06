package com.peresdacha.repository

import com.peresdacha.db.table.ProductsTable
import com.peresdacha.domain.Product
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.math.BigDecimal

class ProductRepository {
    fun findAll(): List<Product> = transaction { ProductsTable.selectAll().map { it.toProduct() } }

    fun findById(id: Long): Product? = transaction {
        ProductsTable.selectAll().where { ProductsTable.id.eq(id) }.singleOrNull()?.toProduct()
    }

    fun create(product: Product): Product = transaction {
        val id = ProductsTable.insertAndGetId {
            it[name] = product.name
            it[description] = product.description
            it[price] = BigDecimal.valueOf(product.price)
            it[stock] = product.stock
        }.value
        product.copy(id = id)
    }

    fun update(id: Long, product: Product): Boolean = transaction {
        ProductsTable.update({ ProductsTable.id.eq(id) }) {
            it[name] = product.name
            it[description] = product.description
            it[price] = BigDecimal.valueOf(product.price)
            it[stock] = product.stock
        } > 0
    }

    fun delete(id: Long): Boolean = transaction { ProductsTable.deleteWhere { ProductsTable.id.eq(id) } > 0 }

    fun decreaseStock(id: Long, quantity: Int): Boolean = transaction {
        val p = ProductsTable.selectAll().where { ProductsTable.id.eq(id) }.singleOrNull() ?: return@transaction false
        val current = p[ProductsTable.stock]
        if (current < quantity) return@transaction false
        ProductsTable.update({ ProductsTable.id.eq(id) and ProductsTable.stock.eq(current) }) { it[stock] = current - quantity } > 0
    }

    private fun ResultRow.toProduct() = Product(
        id = this[ProductsTable.id].value,
        name = this[ProductsTable.name],
        description = this[ProductsTable.description],
        price = this[ProductsTable.price].toDouble(),
        stock = this[ProductsTable.stock],
    )
}
