package com.example.Restaurant.controller;

import com.example.Restaurant.model.*;
import com.example.Restaurant.repository.OrderRepository;
import com.example.Restaurant.services.OrderService;
import com.example.Restaurant.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/seeOrders")
public class SeeOrdersController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RecipeService recipeService;

    @GetMapping
    public String seeOrders(Model model) {
        List<Order> orders = orderRepository.findByStatus("Pending");
        model.addAttribute("Orders", orders);
        return "seeOrders";
    }

    @PostMapping("/deleteOrder/{id}")
    public String deleteOrder(@PathVariable Long id) {
        try {
            orderService.updateOrderStatus(id, "Ready");
            return "redirect:/seeOrders";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @GetMapping("/check-preparation/{id}")
    @ResponseBody
    public Map<String, Object> checkOrderPreparation(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Order order = orderService.getOrderById(id);

            // check if order is null
            for (OrderItem item : order.getOrderItems()) {
                Optional<Recipe> recipe = recipeService.getRecipeByFoodId(item.getFood().getId());
                if (recipe.isEmpty()) {
                    response.put("canPrepare", false);
                    response.put("message", "Recipe not found for: " + item.getFood().getName());
                    return response;
                }

                // check if ingredients are available
                Recipe foundRecipe = recipe.get();
                for (RecipeIngredient recipeIngredient : foundRecipe.getIngredients()) {
                    Ingredient ingredient = recipeIngredient.getIngredient();
                    double requiredAmount = recipeIngredient.getAmount() * item.getQuantity();

                    if (ingredient.getCurrentStock() < requiredAmount) {
                        response.put("canPrepare", false);
                        response.put("message", "Not enough " + ingredient.getName() + " in stock");
                        return response;
                    }
                }
            }

            response.put("canPrepare", true);
            response.put("message", "Order can be prepared");

        } catch (Exception e) {
            response.put("canPrepare", false);
            response.put("message", "Error checking order: " + e.getMessage());
        }

        return response;
    }
}
