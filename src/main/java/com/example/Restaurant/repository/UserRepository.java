package com.example.Restaurant.repository;

import com.example.Restaurant.model.Role;
import com.example.Restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * This repository provides CRUD operations for User entities in the database.
 * It extends JpaRepository to inherit standard database operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * This method generates a query to select a user where the username matches the given parameter.
     *
     * @param username the username of the user to find
     * @return an Optional containing the user with the specified username, or an empty Optional if none found
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user exists by their username.
     * This method generates a query to check if a user exists where the username matches the given parameter.
     *
     * @param username the username to check for existence
     * @return true if a user with the specified username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Finds all active users.
     * This method generates a query to select users where the isActive field is true.
     *
     * @return a list of active users
     */
    List<User> findByIsActiveTrue();

    /**
     * Finds all users by their role.
     * This method generates a query to select users where the role matches the given parameter.
     *
     * @param role the role of the users to find
     * @return a list of users with the specified role
     */
    List<User> findByRole(Role role);
}