package com.example.Restaurant.controller;

import com.example.Restaurant.model.Ingredient;
import com.example.Restaurant.services.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/threshold")
public class ThresholdController {

    @Autowired
    private IngredientService ingredientsService;

    @GetMapping
    public String getThresholdPage(Model model) {
        // Retrieve all ingredients and filter only those below the threshold
        List<Ingredient> lowStockIngredients = ingredientsService.getAllIngredients()
                .stream()
                .filter(ingredient -> ingredient.getCurrentStock() != null
                        && ingredient.getThreshold() != null
                        && ingredient.getCurrentStock() < ingredient.getThreshold())
                .collect(Collectors.toList());

        // Add the filtered ingredients to the model
        model.addAttribute("ingredients", lowStockIngredients);

        // Return the threshold.html page
        return "threshold";
    }
}
