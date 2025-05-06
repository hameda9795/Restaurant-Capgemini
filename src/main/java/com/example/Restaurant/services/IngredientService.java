package com.example.Restaurant.services;

import com.example.Restaurant.model.Ingredient; // Import the Ingredient model class.
import com.example.Restaurant.repository.IngredientRepository; // Import the IngredientRepository interface.
import org.springframework.beans.factory.annotation.Autowired; // Import the Autowired annotation.
import org.springframework.stereotype.Service; // Import the Service annotation.
import org.springframework.transaction.annotation.Transactional; // Import the Transactional annotation.

import java.util.List; // Import the List interface.
import java.util.Optional; // Import the Optional class.
import java.util.stream.Collectors; // Import the Collectors class.

/**
 * Service class for managing ingredient-related operations.
 *
 * Main purpose of this service:
 * - To handle business logic related to ingredient entities.
 *
 * Business features it provides:
 * - Fetching all ingredients.
 * - Fetching a specific ingredient by ID.
 * - Saving a new ingredient.
 * - Deleting an ingredient by ID.
 * - Fetching ingredients with low stock.
 * - Counting out-of-stock ingredients.
 * - Counting well-stocked ingredients.
 * - Updating the stock of an ingredient.
 *
 * Dependencies and component interactions:
 * - IngredientRepository: Used to perform CRUD operations on ingredient entities.
 *
 * Transaction handling:
 * - Methods that modify data (saveIngredient, deleteIngredient, updateStock) are annotated with @Transactional to ensure data consistency.
 *
 * Security/validation considerations:
 * - Ensures that ingredient data is correctly handled and stored.
 * - Validates the presence of ingredient before performing delete operations.
 */
@Service // Marks this class as a Spring service component, making it eligible for component scanning and dependency injection.
public class IngredientService {

    @Autowired // Injects the IngredientRepository dependency into this service.
    private IngredientRepository ingredientRepository;

    /**
     * Fetches all ingredients.
     *
     * Business operation it performs:
     * - Retrieves all ingredient entities from the database.
     *
     * Step by step logic:
     * 1. Call the findAll method of IngredientRepository to get all ingredients.
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
     * - Interacts with IngredientRepository to fetch all ingredient data.
     *
     * Security checks:
     * - No specific security checks.
     *
     * @return a list of all ingredient entities
     */
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll(); // Fetch all ingredients from the database.
    }

    /**
     * Fetches a specific ingredient by ID.
     *
     * Business operation it performs:
     * - Retrieves an ingredient entity from the database based on the specified ID.
     *
     * Step by step logic:
     * 1. Call the findById method of IngredientRepository with the given ID.
     *
     * Transaction behavior:
     * - This method performs a read operation and does not modify any data.
     *
     * Validation rules:
     * - No specific validation rules.
     *
     * Error handling:
     * - Returns an Optional.empty() if the ingredient is not found.
     *
     * Interactions with repositories/other services:
     * - Interacts with IngredientRepository to fetch ingredient data by ID.
     *
     * Security checks:
     * - No specific security checks.
     *
     * @param id the ID of the ingredient to fetch
     * @return an Optional containing the ingredient entity if found, or an empty Optional if not found
     */
    public Optional<Ingredient> getIngredientById(Long id) {
        return ingredientRepository.findById(id); // Fetch the ingredient by ID from the database.
    }

    /**
     * Saves a new ingredient.
     *
     * Business operation it performs:
     * - Saves a new ingredient entity to the database.
     *
     * Step by step logic:
     * 1. Validate the ingredient's current stock and threshold.
     * 2. Save the ingredient entity to the database using IngredientRepository.
     *
     * Transaction behavior:
     * - This method is transactional to ensure data consistency.
     *
     * Validation rules:
     * - Ensures that the current stock and threshold are not null.
     *
     * Error handling:
     * - No specific error handling.
     *
     * Interactions with repositories/other services:
     * - Interacts with IngredientRepository to save the ingredient entity.
     *
     * Security checks:
     * - No specific security checks.
     *
     * @param ingredient the ingredient entity to save
     * @return the saved ingredient entity
     */
    @Transactional
    public Ingredient saveIngredient(Ingredient ingredient) {
        // Validate ingredient values
        if (ingredient.getCurrentStock() == null) { // Check if the current stock is null.
            ingredient.setCurrentStock(0.0); // Set the current stock to 0.0 if it is null.
        }
        if (ingredient.getThreshold() == null) { // Check if the threshold is null.
            ingredient.setThreshold(0.0); // Set the threshold to 0.0 if it is null.
        }
        return ingredientRepository.save(ingredient); // Save the ingredient entity to the database.
    }

    /**
     * Deletes an ingredient by ID.
     *
     * Business operation it performs:
     * - Deletes an ingredient entity from the database based on the specified ID.
     *
     * Step by step logic:
     * 1. Fetch the ingredient entity by ID using IngredientRepository.
     * 2. If the ingredient is not found, throw a RuntimeException.
     * 3. Delete the ingredient entity from the database using IngredientRepository.
     *
     * Transaction behavior:
     * - This method is transactional to ensure data consistency.
     *
     * Validation rules:
     * - Ensures that the ingredient entity exists before attempting to delete it.
     *
     * Error handling:
     * - Throws a RuntimeException if the ingredient ID is invalid.
     *
     * Interactions with repositories/other services:
     * - Interacts with IngredientRepository to delete the ingredient entity.
     *
     * Security checks:
     * - No specific security checks.
     *
     * @param id the ID of the ingredient to delete
     */
    @Transactional
    public void deleteIngredient(Long id) {
        Optional<Ingredient> ingredient = ingredientRepository.findById(id); // Fetch the ingredient entity by ID.
        if (ingredient.isPresent()) { // Check if the ingredient is present.
            ingredientRepository.delete(ingredient.get()); // Delete the ingredient entity from the database.
        } else {
            throw new RuntimeException("Ingredient not found"); // Throw a RuntimeException if the ingredient is not found.
        }
    }

    /**
     * Fetches ingredients with low stock.
     *
     * Business operation it performs:
     * - Retrieves ingredient entities from the database that have low stock.
     *
     * Step by step logic:
     * 1. Call the getAllIngredients method to get all ingredients.
     * 2. Filter the ingredients based on their current stock and threshold.
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
     * - Interacts with IngredientRepository to fetch all ingredient data.
     *
     * Security checks:
     * - No specific security checks.
     *
     * @return a list of ingredients with low stock
     */
    public List<Ingredient> getLowStockIngredients() {
        return getAllIngredients().stream() // Fetch all ingredients and convert to a stream.
                .filter(i -> i.getCurrentStock() <= i.getThreshold()) // Filter ingredients with low stock.
                .collect(Collectors.toList()); // Collect the filtered ingredients into a list.
    }

    /**
     * Counts out-of-stock ingredients.
     *
     * Business operation it performs:
     * - Counts the number of ingredient entities in the database that are out of stock.
     *
     * Step by step logic:
     * 1. Call the getAllIngredients method to get all ingredients.
     * 2. Filter the ingredients based on their current stock.
     * 3. Count the filtered ingredients.
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
     * - Interacts with IngredientRepository to fetch all ingredient data.
     *
     * Security checks:
     * - No specific security checks.
     *
     * @return the count of out-of-stock ingredients
     */
    public long getOutOfStockCount() {
        return getAllIngredients().stream() // Fetch all ingredients and convert to a stream.
                .filter(i -> i.getCurrentStock() <= 0) // Filter out-of-stock ingredients.
                .count(); // Count the filtered ingredients.
    }

    /**
     * Counts well-stocked ingredients.
     *
     * Business operation it performs:
     * - Counts the number of ingredient entities in the database that are well-stocked.
     *
     * Step by step logic:
     * 1. Call the getAllIngredients method to get all ingredients.
     * 2. Filter the ingredients based on their current stock and threshold.
     * 3. Count the filtered ingredients.
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
     * - Interacts with IngredientRepository to fetch all ingredient data.
     *
     * Security checks:
     * - No specific security checks.
     *
     * @return the count of well-stocked ingredients
     */
    public long getWellStockedCount() {
        return getAllIngredients().stream() // Fetch all ingredients and convert to a stream.
                .filter(i -> i.getCurrentStock() > i.getThreshold()) // Filter well-stocked ingredients.
                .count(); // Count the filtered ingredients.
    }

    /**
     * Updates the stock of an ingredient.
     *
     * Business operation it performs:
     * - Updates the stock of an ingredient entity in the database.
     *
     * Step by step logic:
     * 1. Fetch the ingredient entity by ID using IngredientRepository.
     * 2. Update the current stock of the ingredient.
     * 3. Save the updated ingredient entity to the database using IngredientRepository.
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
     * - Interacts with IngredientRepository to update the ingredient entity.
     *
     * Security checks:
     * - No specific security checks.
     *
     * @param id the ID of the ingredient to update
     * @param newStock the new stock value to set
     */
    @Transactional
    public void updateStock(Long id, Double newStock) {
        ingredientRepository.findById(id).ifPresent(ingredient -> { // Fetch the ingredient entity by ID.
            ingredient.setCurrentStock(newStock); // Update the current stock of the ingredient.
            ingredientRepository.save(ingredient); // Save the updated ingredient entity to the database.
        });
    }
}