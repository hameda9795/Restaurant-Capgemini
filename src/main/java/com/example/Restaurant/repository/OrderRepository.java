package com.example.Restaurant.repository;

import com.example.Restaurant.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Order entities.
 * This repository provides CRUD operations for Order entities in the database.
 * It extends JpaRepository to inherit standard database operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders by their status.
     * This method generates a query to select orders where the status matches the given parameter.
     *
     * @param status the status of the orders to find
     * @return a list of orders with the specified status
     */
    List<Order> findByStatus(String status);
}