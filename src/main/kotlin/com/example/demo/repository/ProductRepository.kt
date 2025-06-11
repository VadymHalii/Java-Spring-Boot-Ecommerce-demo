package com.example.demo.repository

import com.example.demo.entity.Product
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class ProductRepository(private val jdbcClient: JdbcClient) {
    
    fun findAll(): List<Product> {
        return jdbcClient.sql("SELECT * FROM products ORDER BY created_at DESC")
            .query { rs, _ ->
                Product(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    description = rs.getString("description"),
                    price = rs.getBigDecimal("price"),
                    category = rs.getString("category"),
                    variants = null,
                    createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                    updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
                )
            }.list()
    }
    
    fun findById(id: Long): Product? {
        return jdbcClient.sql("SELECT * FROM products WHERE id = ?")
            .param(id)
            .query { rs, _ ->
                Product(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    description = rs.getString("description"),
                    price = rs.getBigDecimal("price"),
                    category = rs.getString("category"),
                    variants = null,
                    createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                    updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
                )
            }.optional().orElse(null)
    }
    
    fun findByNameContainingIgnoreCase(name: String): List<Product> {
        return jdbcClient.sql("SELECT * FROM products WHERE LOWER(name) LIKE LOWER(?) ORDER BY created_at DESC")
            .param("%$name%")
            .query { rs, _ ->
                Product(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    description = rs.getString("description"),
                    price = rs.getBigDecimal("price"),
                    category = rs.getString("category"),
                    variants = null,
                    createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                    updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
                )
            }.list()
    }
    
    fun findByCategory(category: String): List<Product> {
        return jdbcClient.sql("SELECT * FROM products WHERE category = ? ORDER BY created_at DESC")
            .param(category)
            .query { rs, _ ->
                Product(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    description = rs.getString("description"),
                    price = rs.getBigDecimal("price"),
                    category = rs.getString("category"),
                    variants = null,
                    createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                    updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
                )
            }.list()
    }
    
    fun findDistinctCategories(): List<String> {
        return jdbcClient.sql("SELECT DISTINCT category FROM products WHERE category IS NOT NULL ORDER BY category")
            .query(String::class.java)
            .list()
    }
    
    fun save(product: Product): Product {
        if (product.id == null) {
            jdbcClient.sql("""
                INSERT INTO products (name, description, price, category, variants, created_at, updated_at) 
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """)
                .param(product.name)
                .param(product.description)
                .param(product.price)
                .param(product.category)
                .param(product.variants?.toString())
                .param(product.createdAt)
                .param(product.updatedAt)
                .update()
            
            val lastId = jdbcClient.sql("SELECT MAX(id) FROM products")
                .query(Long::class.java)
                .single()
            
            return product.copy(id = lastId)
        } else {
            jdbcClient.sql("""
                UPDATE products 
                SET name = ?, description = ?, price = ?, category = ?, variants = ?, updated_at = ?
                WHERE id = ?
            """)
                .param(product.name)
                .param(product.description)
                .param(product.price)
                .param(product.category)
                .param(product.variants?.toString())
                .param(LocalDateTime.now())
                .param(product.id)
                .update()
            
            return product.copy(updatedAt = LocalDateTime.now())
        }
    }
    
    fun saveAll(products: List<Product>): List<Product> {
        return products.map { save(it) }
    }
    
    fun deleteById(id: Long) {
        jdbcClient.sql("DELETE FROM products WHERE id = ?")
            .param(id)
            .update()
    }
    
    fun deleteAll() {
        jdbcClient.sql("DELETE FROM products").update()
    }
}