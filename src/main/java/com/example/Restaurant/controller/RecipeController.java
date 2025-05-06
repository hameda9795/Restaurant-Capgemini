package com.example.Restaurant.controller;

import com.example.Restaurant.model.Recipe;
import com.example.Restaurant.services.RecipeService;
import com.example.Restaurant.services.FoodService;
import com.example.Restaurant.services.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * The RecipeController class handles operations related to recipe management in the restaurant application.
 *
 * Main Responsibilities:
 * - View all recipes.
 * - Save new recipes.
 * - Retrieve specific recipes.
 * - Delete recipes.
 *
 * Component Relationships:
 * - Interacts with RecipeService for recipe-related operations.
 * - Interacts with FoodService for food-related operations.
 * - Interacts with IngredientService for ingredient-related operations.
 *
 * Dependencies:
 * - RecipeService: Service for recipe-related operations.
 * - FoodService: Service for food-related operations.
 * - IngredientService: Service for ingredient-related operations.
 *
 * Security Considerations:
 * - Ensure proper validation of recipe data.
 * - Handle sensitive data securely.
 */
@Controller
@RequestMapping("/recipe-management")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private FoodService foodService;

    @Autowired
    private IngredientService ingredientService;

    /**
     * Displays the recipe management page with all recipes, foods, and ingredients.
     *
     * @param model the Model object used to pass data to the view
     * @return the name of the view to render
     */
    @GetMapping
    public String viewRecipes(Model model) {
        model.addAttribute("recipes", recipeService.getAllRecipes());
        model.addAttribute("foods", foodService.getAllFoods());
        model.addAttribute("ingredients", ingredientService.getAllIngredients());
        return "recipe-management";
    }

    /**
     * Saves a new recipe.
     *
     * @param recipe the Recipe object to save
     * @return the ResponseEntity containing the saved recipe or an error message
     * @throws IllegalArgumentException if the input data is invalid
     */
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> saveRecipe(@RequestBody Recipe recipe) {
        try {
            // Validate input data
            if (recipe.getFood() == null || recipe.getFood().getId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Please select a food"));
            }

            if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Please add at least one ingredient"));
            }

            Recipe savedRecipe = recipeService.saveRecipe(recipe);
            return ResponseEntity.ok(savedRecipe);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error saving recipe: " + e.getMessage()));
        }
    }

    /**
     * Retrieves a specific recipe by ID.
     *
     * @param id the ID of the recipe to retrieve
     * @return the ResponseEntity containing the recipe or a not found status
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getRecipe(@PathVariable Long id) {
        return recipeService.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific recipe by ID.
     *
     * @param id the ID of the recipe to delete
     * @return the ResponseEntity containing a success message or an error message
     * @throws IllegalArgumentException if the recipe ID is invalid
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        try {
            recipeService.deleteRecipe(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}