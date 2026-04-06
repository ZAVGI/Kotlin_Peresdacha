package com.peresdacha.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init(jdbcUrl: String, user: String, password: String) {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            this.jdbcUrl = jdbcUrl
            username = user
            this.password = password
            maximumPoolSize = 10
        }
        val dataSource = HikariDataSource(config)
        Flyway.configure().dataSource(dataSource).load().migrate()
        Database.connect(dataSource)
    }
}
