package com.example.demo.controller

import com.example.demo.entity.Product
import com.example.demo.service.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@Controller
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {
    
    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("categories", productService.getCategories())
        return "products/simple"
    }
    
    @GetMapping("/list")
    fun listProducts(model: Model): String {
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "products/list :: product-list"
    }
    
    @GetMapping("/search")
    fun searchProducts(@RequestParam query: String, model: Model): String {
        val products = if (query.isBlank()) {
            productService.getAllProducts()
        } else {
            productService.searchProducts(query)
        }
        model.addAttribute("products", products)
        return "products/list :: product-list"
    }
    
    @GetMapping("/category/{category}")
    fun getProductsByCategory(@PathVariable category: String, model: Model): String {
        val products = productService.getProductsByCategory(category)
        model.addAttribute("products", products)
        return "products/list :: product-list"
    }
    
    @GetMapping("/new")
    fun newProductForm(model: Model): String {
        model.addAttribute("product", Product(name = "", description = "", price = null, category = ""))
        return "products/form :: product-form"
    }
    
    @PostMapping
    fun createProduct(
        @RequestParam name: String,
        @RequestParam description: String?,
        @RequestParam price: String?,
        @RequestParam category: String?,
        model: Model
    ): String {
        val priceDecimal = price?.toBigDecimalOrNull()
        val product = Product(
            name = name,
            description = description?.takeIf { it.isNotBlank() },
            price = priceDecimal,
            category = category?.takeIf { it.isNotBlank() }
        )
        
        productService.saveProduct(product)
        
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "products/list :: product-list"
    }
    
    @GetMapping("/{id}/edit")
    fun editProductForm(@PathVariable id: Long, model: Model): String {
        val product = productService.getProductById(id)
        model.addAttribute("product", product)
        return "products/form :: product-form"
    }
    
    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestParam name: String,
        @RequestParam description: String?,
        @RequestParam price: String?,
        @RequestParam category: String?,
        model: Model
    ): String {
        val existingProduct = productService.getProductById(id)
        if (existingProduct != null) {
            val priceDecimal = price?.toBigDecimalOrNull()
            val updatedProduct = existingProduct.copy(
                name = name,
                description = description?.takeIf { it.isNotBlank() },
                price = priceDecimal,
                category = category?.takeIf { it.isNotBlank() }
            )
            productService.saveProduct(updatedProduct)
        }
        
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "products/list :: product-list"
    }
    
    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long, model: Model): String {
        productService.deleteProduct(id)
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "products/list :: product-list"
    }
    
    @PostMapping("/load")
    fun loadProducts(model: Model): String {
        productService.fetchAndSaveProducts()
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "products/list :: product-list"
    }
    
    @GetMapping("/api/test")
    @ResponseBody
    fun testApi(): String {
        val products = productService.getAllProducts()
        return "Found ${products.size} products in database"
    }
}