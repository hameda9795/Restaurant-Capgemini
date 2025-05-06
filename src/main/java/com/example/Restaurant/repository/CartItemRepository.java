package com.example.Restaurant.repository;

import com.example.Restaurant.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing CartItem entities.
 * This repository provides CRUD operations for CartItem entities in the database.
 * It extends JpaRepository to inherit standard database operations.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // JpaRepository provides standard CRUD operations:
    // - save(S entity): Saves a given entity.
    // - findById(ID id): Retrieves an entity by its id.
    // - findAll(): Returns all entities.
    // - deleteById(ID id): Deletes the entity with the given id.
    // - etc.

    // No custom queries are defined in this repository.
}