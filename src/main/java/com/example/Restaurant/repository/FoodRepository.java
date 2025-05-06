package com.example.Restaurant.repository;

import com.example.Restaurant.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Food entities.
 * This repository provides CRUD operations for Food entities in the database.
 * It extends JpaRepository to inherit standard database operations.
 */
@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    /**
     * Finds all food items by their category.
     * This method generates a query to select food items where the category matches the given parameter.
     *
     * @param category the category of the food items to find
     * @return a list of food items in the specified category
     */
    List<Food> findByCategory(String category);
}