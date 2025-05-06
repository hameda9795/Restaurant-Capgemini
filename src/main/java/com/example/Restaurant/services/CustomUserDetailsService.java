package com.example.Restaurant.services;

import com.example.Restaurant.model.User; // Import the User model class.
import com.example.Restaurant.repository.UserRepository; // Import the UserRepository interface.
import org.springframework.beans.factory.annotation.Autowired; // Import the Autowired annotation.
import org.springframework.security.core.userdetails.UserDetails; // Import the UserDetails interface.
import org.springframework.security.core.userdetails.UserDetailsService; // Import the UserDetailsService interface.
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Import the UsernameNotFoundException class.
import org.springframework.stereotype.Service; // Import the Service annotation.

/**
 * Service class for managing user details.
 *
 * Main purpose of this service:
 * - To provide user details required for authentication and authorization.
 *
 * Business features it provides:
 * - Fetching user details from the database.
 * - Integrating with Spring Security for authentication.
 *
 * Dependencies and component interactions:
 * - UserRepository: Used to fetch user data from the database.
 *
 * Transaction handling:
 * - This service primarily deals with read operations and does not explicitly handle transactions.
 *
 * Security/validation considerations:
 * - Ensures that only valid and active users can be authenticated.
 * - Throws an exception if the user is not found or is inactive.
 */
@Service // Marks this class as a Spring service component, making it eligible for component scanning and dependency injection.
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired // Injects the UserRepository dependency into this service.
    private UserRepository userRepository;

    /**
     * Loads user details by username.
     *
     * Business operation it performs:
     * - Loads user details required for authentication.
     *
     * Step by step logic:
     * 1. Fetch the user from the UserRepository by username.
     * 2. If the user is not found, throw a UsernameNotFoundException.
     * 3. Construct a UserDetails object with the user's username, password, roles, and account status.
     *
     * Transaction behavior:
     * - This method performs a read operation and does not modify any data.
     *
     * Validation rules:
     * - Ensures that the user exists and is active.
     *
     * Error handling:
     * - Throws UsernameNotFoundException if the user is not found.
     *
     * Interactions with repositories/other services:
     * - Interacts with UserRepository to fetch user data.
     *
     * Security checks:
     * - Ensures that the user is active before allowing authentication.
     *
     * @param username the username of the user to load
     * @return UserDetails object containing user information for authentication
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the UserRepository by username.
        User user = userRepository.findByUsername(username)
                // If the user is not found, throw a UsernameNotFoundException.
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Construct a UserDetails object with the user's username, password, roles, and account status.
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername()) // Set the username.
                .password(user.getPassword()) // Set the password.
                .roles(user.getRole().name()) // Set the roles.
                .accountLocked(!user.getIsActive()) // Set the account locked status based on user activity.
                .build(); // Build the UserDetails object.
    }
}