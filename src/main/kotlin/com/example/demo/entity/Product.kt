package com.example.demo.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class Product(
    val id: Long? = null,
    val name: String = "",
    val description: String? = null,
    val price: BigDecimal? = null,
    val category: String? = null,
    val variants: Map<String, Any>? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)