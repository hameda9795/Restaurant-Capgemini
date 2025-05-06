package com.example.Restaurant.repository;

import com.example.Restaurant.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Recipe entities.
 * This repository provides CRUD operations for Recipe entities in the database.
 * It extends JpaRepository to inherit standard database operations.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Finds a recipe by the associated food ID.
     * This method generates a query to select a recipe where the food ID matches the given parameter.
     *
     * @param foodId the ID of the food associated with the recipe to find
     * @return an Optional containing the recipe with the specified food ID, or an empty Optional if none found
     */
    Optional<Recipe> findByFoodId(Long foodId);
}