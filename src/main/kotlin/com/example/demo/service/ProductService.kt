package com.example.demo.service

import com.example.demo.entity.Product
import com.example.demo.repository.ProductRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {
    
    private val logger = LoggerFactory.getLogger(ProductService::class.java)
    
    @Scheduled(fixedRate = 3600000) // Run every hour
    @Async
    @Transactional
    fun fetchAndSaveProducts() {
        try {
            logger.info("Starting product fetch from external API")
            val response = restTemplate.getForObject("https://famme.no/products.json", String::class.java)
            val jsonNode = objectMapper.readTree(response)
            
            val products = mutableListOf<Product>()
            var count = 0
            
            // The API returns {"products": [...]} structure
            val productsArray = jsonNode.get("products")
            if (productsArray != null && productsArray.isArray) {
                for (productNode in productsArray) {
                    if (count >= 50) break
                    
                    try {
                        val product = parseProduct(productNode)
                        products.add(product)
                        count++
                    } catch (e: Exception) {
                        logger.warn("Failed to parse product: ${e.message}")
                    }
                }
            }
            
            productRepository.deleteAll()
            productRepository.saveAll(products)
            logger.info("Successfully saved ${products.size} products")
            
        } catch (e: Exception) {
            logger.error("Failed to fetch products from API", e)
        }
    }
    
    private fun parseProduct(productNode: JsonNode): Product {
        val name = productNode.get("title")?.asText() ?: "Unknown Product"
        val rawDescription = productNode.get("body_html")?.asText()?.takeIf { it.isNotBlank() }
        // Strip HTML and limit description length
        val description = rawDescription?.replace(Regex("<[^>]*>"), "")?.take(1000)
        val category = productNode.get("product_type")?.asText()?.takeIf { it.isNotBlank() }
        
        // Extract price from first variant that has a price
        var price: BigDecimal? = null
        val variantsNode = productNode.get("variants")
        if (variantsNode != null && variantsNode.isArray && variantsNode.size() > 0) {
            for (variant in variantsNode) {
                val variantPrice = variant.get("price")?.asText()
                if (variantPrice != null && variantPrice != "0.00") {
                    price = variantPrice.toBigDecimalOrNull()
                    break
                }
            }
        }
        
        val variants = mutableMapOf<String, Any>()
        productNode.get("variants")?.let { variantsNode ->
            if (variantsNode.isArray) {
                variants["items"] = objectMapper.convertValue(variantsNode, List::class.java)
            } else if (variantsNode.isObject) {
                @Suppress("UNCHECKED_CAST")
                variants.putAll(objectMapper.convertValue(variantsNode, Map::class.java) as Map<String, Any>)
            }
        }
        
        return Product(
            name = name,
            description = description,
            price = price,
            category = category,
            variants = if (variants.isNotEmpty()) variants else null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }
    
    fun getAllProducts(): List<Product> = productRepository.findAll()
    
    fun getProductById(id: Long): Product? = productRepository.findById(id)
    
    fun searchProducts(query: String): List<Product> = 
        productRepository.findByNameContainingIgnoreCase(query)
    
    fun getProductsByCategory(category: String): List<Product> = 
        productRepository.findByCategory(category)
    
    fun getCategories(): List<String> = productRepository.findDistinctCategories()
    
    @Transactional
    fun saveProduct(product: Product): Product = productRepository.save(product)
    
    @Transactional
    fun deleteProduct(id: Long) = productRepository.deleteById(id)
}