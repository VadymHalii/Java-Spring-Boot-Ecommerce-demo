package com.example.demo.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    
    @GetMapping("/")
    fun home(): String {
        return "redirect:/products"
    }
}