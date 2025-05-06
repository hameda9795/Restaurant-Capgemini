package com.example.Restaurant.services;

    import com.example.Restaurant.model.Recipe; // Import the Recipe model class.
    import com.example.Restaurant.model.RecipeIngredient; // Import the RecipeIngredient model class.
    import com.example.Restaurant.repository.RecipeRepository; // Import the RecipeRepository interface.
    import org.springframework.beans.factory.annotation.Autowired; // Import the Autowired annotation.
    import org.springframework.stereotype.Service; // Import the Service annotation.
    import org.springframework.transaction.annotation.Transactional; // Import the Transactional annotation.

    import java.util.List; // Import the List interface.
    import java.util.Optional; // Import the Optional class.

    /**
     * Service class for managing recipe-related operations.
     *
     * Main purpose of this service:
     * - To handle business logic related to recipe entities.
     *
     * Business features it provides:
     * - Fetching all recipes.
     * - Fetching a specific recipe by ID.
     * - Saving a new recipe.
     * - Deleting a recipe by ID.
     * - Fetching a recipe by food ID.
     * - Checking if an ingredient is used in any recipes.
     *
     * Dependencies and component interactions:
     * - RecipeRepository: Used to perform CRUD operations on recipe entities.
     *
     * Transaction handling:
     * - Methods that modify data (saveRecipe, deleteRecipe) are annotated with @Transactional to ensure data consistency.
     *
     * Security/validation considerations:
     * - Ensures that recipe data is correctly handled and stored.
     * - Validates the presence of ingredients before saving a recipe.
     */
    @Service // Marks this class as a Spring service component, making it eligible for component scanning and dependency injection.
    public class RecipeService {

        @Autowired // Injects the RecipeRepository dependency into this service.
        private RecipeRepository recipeRepository;

        /**
         * Fetches all recipes.
         *
         * Business operation it performs:
         * - Retrieves all recipe entities from the database.
         *
         * Step by step logic:
         * 1. Call the findAll method of RecipeRepository to get all recipes.
         *
         * Transaction behavior:
         * - This method performs a read operation and does not modify any data.
         *
         * Validation rules:
         * - No specific validation rules.
         *
         * Error handling:
         * - No specific error handling.
         *
         * Interactions with repositories/other services:
         * - Interacts with RecipeRepository to fetch all recipe data.
         *
         * Security checks:
         * - No specific security checks.
         *
         * @return a list of all recipe entities
         */
        public List<Recipe> getAllRecipes() {
            return recipeRepository.findAll(); // Fetch all recipes from the database.
        }

        /**
         * Fetches a specific recipe by ID.
         *
         * Business operation it performs:
         * - Retrieves a recipe entity from the database based on the specified ID.
         *
         * Step by step logic:
         * 1. Call the findById method of RecipeRepository with the given ID.
         *
         * Transaction behavior:
         * - This method performs a read operation and does not modify any data.
         *
         * Validation rules:
         * - No specific validation rules.
         *
         * Error handling:
         * - Returns an Optional.empty() if the recipe is not found.
         *
         * Interactions with repositories/other services:
         * - Interacts with RecipeRepository to fetch recipe data by ID.
         *
         * Security checks:
         * - No specific security checks.
         *
         * @param id the ID of the recipe to fetch
         * @return an Optional containing the recipe entity if found, or an empty Optional if not found
         */
        public Optional<Recipe> getRecipeById(Long id) {
            return recipeRepository.findById(id); // Fetch the recipe by ID from the database.
        }

        /**
         * Saves a new recipe.
         *
         * Business operation it performs:
         * - Saves a new recipe entity to the database.
         *
         * Step by step logic:
         * 1. Validate the presence of ingredients in the recipe.
         * 2. Set the recipe reference in each ingredient.
         * 3. Save the recipe entity to the database using RecipeRepository.
         *
         * Transaction behavior:
         * - This method is transactional to ensure data consistency.
         *
         * Validation rules:
         * - Ensures that the ingredients are not null.
         *
         * Error handling:
         * - No specific error handling.
         *
         * Interactions with repositories/other services:
         * - Interacts with RecipeRepository to save the recipe entity.
         *
         * Security checks:
         * - No specific security checks.
         *
         * @param recipe the recipe entity to save
         * @return the saved recipe entity
         */
        @Transactional
        public Recipe saveRecipe(Recipe recipe) {
            if (recipe.getIngredients() != null) { // Check if the recipe has ingredients.
                for (RecipeIngredient ingredient : recipe.getIngredients()) { // Iterate through the ingredients.
                    ingredient.setRecipe(recipe); // Set the recipe reference in each ingredient.
                }
            }
            return recipeRepository.save(recipe); // Save the recipe entity to the database.
        }

        /**
         * Deletes a recipe by ID.
         *
         * Business operation it performs:
         * - Deletes a recipe entity from the database based on the specified ID.
         *
         * Step by step logic:
         * 1. Call the deleteById method of RecipeRepository with the given ID.
         *
         * Transaction behavior:
         * - This method is transactional to ensure data consistency.
         *
         * Validation rules:
         * - No specific validation rules.
         *
         * Error handling:
         * - No specific error handling.
         *
         * Interactions with repositories/other services:
         * - Interacts with RecipeRepository to delete the recipe entity.
         *
         * Security checks:
         * - No specific security checks.
         *
         * @param id the ID of the recipe to delete
         */
        @Transactional
        public void deleteRecipe(Long id) {
            recipeRepository.deleteById(id); // Delete the recipe by ID from the database.
        }

        /**
         * Fetches a recipe by food ID.
         *
         * Business operation it performs:
         * - Retrieves a recipe entity from the database based on the specified food ID.
         *
         * Step by step logic:
         * 1. Call the findByFoodId method of RecipeRepository with the given food ID.
         *
         * Transaction behavior:
         * - This method performs a read operation and does not modify any data.
         *
         * Validation rules:
         * - No specific validation rules.
         *
         * Error handling:
         * - Returns an Optional.empty() if the recipe is not found.
         *
         * Interactions with repositories/other services:
         * - Interacts with RecipeRepository to fetch recipe data by food ID.
         *
         * Security checks:
         * - No specific security checks.
         *
         * @param foodId the food ID of the recipe to fetch
         * @return an Optional containing the recipe entity if found, or an empty Optional if not found
         */
        public Optional<Recipe> getRecipeByFoodId(Long foodId) {
            return recipeRepository.findByFoodId(foodId); // Fetch the recipe by food ID from the database.
        }

        /**
         * Checks if an ingredient is used in any recipes.
         *
         * Business operation it performs:
         * - Checks if the specified ingredient is used in any recipe entities.
         *
         * Step by step logic:
         * 1. Call the getAllRecipes method to get all recipes.
         * 2. Iterate through the recipes and their ingredients.
         * 3. Check if the specified ingredient ID matches any ingredient in the recipes.
         *
         * Transaction behavior:
         * - This method performs a read operation and does not modify any data.
         *
         * Validation rules:
         * - No specific validation rules.
         *
         * Error handling:
         * - No specific error handling.
         *
         * Interactions with repositories/other services:
         * - Interacts with RecipeRepository to fetch all recipe data.
         *
         * Security checks:
         * - No specific security checks.
         *
         * @param ingredientId the ID of the ingredient to check
         * @return true if the ingredient is used in any recipes, false otherwise
         */
        public boolean isIngredientUsedInRecipes(Long ingredientId) {
            List<Recipe> recipes = getAllRecipes(); // Fetch all recipes from the database.
            for (Recipe recipe : recipes) { // Iterate through the recipes.
                for (RecipeIngredient recipeIngredient : recipe.getIngredients()) { // Iterate through the ingredients of each recipe.
                    if (recipeIngredient.getIngredient().getId().equals(ingredientId)) { // Check if the ingredient ID matches.
                        return true; // Return true if the ingredient is used in any recipe.
                    }
                }
            }
            return false; // Return false if the ingredient is not used in any recipe.
        }
    }