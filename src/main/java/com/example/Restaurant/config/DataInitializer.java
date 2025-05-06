
package com.example.Restaurant.config; // Package declaration

import com.example.Restaurant.model.Role; // Import Role model
import com.example.Restaurant.model.User; // Import User model
import com.example.Restaurant.services.UserService; // Import UserService
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired annotation
import org.springframework.boot.CommandLineRunner; // Import CommandLineRunner interface
import org.springframework.stereotype.Component; // Import Component annotation

@Component // Mark this class as a Spring component
public class DataInitializer implements CommandLineRunner { // Implement CommandLineRunner interface

    private final UserService userService; // Declare UserService field

    @Autowired // Autowire UserService via constructor
    public DataInitializer(UserService userService) { // Constructor with UserService parameter
        this.userService = userService; // Initialize userService field
    }

    @Override // Override run method from CommandLineRunner
    public void run(String... args) { // Method to run at application startup
        if (!userService.existsByUsername("admin")) { // Check if admin user does not exist
            User admin = new User(); // Create new User object
            admin.setUsername("admin"); // Set username to "admin"
            admin.setPassword("admin123"); // Set password to "admin123"
            admin.setRole(Role.ADMIN); // Set role to ADMIN
            admin.setFirstName("Admin"); // Set first name to "Admin"
            admin.setLastName("User"); // Set last name to "User"
            admin.setIsActive(true); // Set isActive to true
            userService.createUser(admin); // Save admin user

            System.out.println("Admin user created successfully!"); // Print success message
        }
    }
}
