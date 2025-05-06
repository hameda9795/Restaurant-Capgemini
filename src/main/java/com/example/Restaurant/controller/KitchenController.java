package com.example.Restaurant.controller;


import com.example.Restaurant.services.IngredientService;
import com.example.Restaurant.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class KitchenController {

    @Autowired
    private OrderService OrderService;

    @Autowired
    private IngredientService ingredientService;

    @GetMapping("/kitchen")
    public String getKitchen(Model model) {
        model.addAttribute("activeOrdersCount", OrderService.getActiveOrdersCount().size());
        model.addAttribute(("lowStockCount"), ingredientService.getLowStockIngredients().size());

        return "kitchen";
    }

}
