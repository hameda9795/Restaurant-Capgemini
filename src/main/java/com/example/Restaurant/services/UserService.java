package com.example.Restaurant.services;

         import com.example.Restaurant.model.User; // Import the User model class.
         import com.example.Restaurant.repository.UserRepository; // Import the UserRepository interface.
         import org.springframework.beans.factory.annotation.Autowired; // Import the Autowired annotation.
         import org.springframework.security.crypto.password.PasswordEncoder; // Import the PasswordEncoder interface.
         import org.springframework.stereotype.Service; // Import the Service annotation.
         import org.springframework.transaction.annotation.Transactional; // Import the Transactional annotation.
         import java.time.LocalDateTime; // Import the LocalDateTime class.
         import java.util.List; // Import the List interface.
         import java.util.Optional; // Import the Optional class.

         /**
          * Service class for managing user-related operations.
          *
          * Main purpose of this service:
          * - To handle business logic related to user entities.
          *
          * Business features it provides:
          * - Creating a new user.
          * - Fetching all users.
          * - Fetching a specific user by ID.
          * - Updating an existing user.
          * - Toggling user status (active/inactive).
          * - Updating the last login time of a user.
          * - Fetching active users.
          * - Checking if a username exists.
          * - Deactivating a user.
          * - Activating a user.
          *
          * Dependencies and component interactions:
          * - UserRepository: Used to perform CRUD operations on user entities.
          * - PasswordEncoder: Used to encode user passwords.
          *
          * Transaction handling:
          * - The class is annotated with @Transactional to ensure data consistency for all methods.
          *
          * Security/validation considerations:
          * - Ensures that usernames are unique.
          * - Encodes passwords before saving.
          * - Validates the presence of users before performing update or delete operations.
          */
         @Service // Marks this class as a Spring service component, making it eligible for component scanning and dependency injection.
         @Transactional // Ensures that all methods in this class are executed within a transaction.
         public class UserService {

             @Autowired // Injects the UserRepository dependency into this service.
             private UserRepository userRepository;

             @Autowired // Injects the PasswordEncoder dependency into this service.
             private PasswordEncoder passwordEncoder;

             /**
              * Creates a new user.
              *
              * Business operation it performs:
              * - Creates and saves a new user entity to the database.
              *
              * Step by step logic:
              * 1. Check if the username already exists.
              * 2. Encode the user's password.
              * 3. Save the user entity to the database.
              *
              * Transaction behavior:
              * - This method is transactional to ensure data consistency.
              *
              * Validation rules:
              * - Ensures that the username is unique.
              *
              * Error handling:
              * - Throws RuntimeException if the username already exists.
              *
              * Interactions with repositories/other services:
              * - Interacts with UserRepository to save the user entity.
              *
              * Security checks:
              * - Encodes the user's password before saving.
              *
              * @param user the user entity to create
              * @return the created user entity
              */
             public User createUser(User user) {
                 if (userRepository.existsByUsername(user.getUsername())) { // Check if the username already exists.
                     throw new RuntimeException("Username already exists"); // Throw an exception if the username already exists.
                 }
                 user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode the user's password.
                 return userRepository.save(user); // Save the user entity to the database.
             }

             /**
              * Fetches all users.
              *
              * Business operation it performs:
              * - Retrieves all user entities from the database.
              *
              * Step by step logic:
              * 1. Call the findAll method of UserRepository to get all users.
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
              * - Interacts with UserRepository to fetch all user data.
              *
              * Security checks:
              * - No specific security checks.
              *
              * @return a list of all user entities
              */
             public List<User> getAllUsers() {
                 return userRepository.findAll(); // Fetch all users from the database.
             }

             /**
              * Fetches a specific user by ID.
              *
              * Business operation it performs:
              * - Retrieves a user entity from the database based on the specified ID.
              *
              * Step by step logic:
              * 1. Call the findById method of UserRepository with the given ID.
              *
              * Transaction behavior:
              * - This method performs a read operation and does not modify any data.
              *
              * Validation rules:
              * - No specific validation rules.
              *
              * Error handling:
              * - Returns an Optional.empty() if the user is not found.
              *
              * Interactions with repositories/other services:
              * - Interacts with UserRepository to fetch user data by ID.
              *
              * Security checks:
              * - No specific security checks.
              *
              * @param id the ID of the user to fetch
              * @return an Optional containing the user entity if found, or an empty Optional if not found
              */
             public Optional<User> getUserById(Long id) {
                 return userRepository.findById(id); // Fetch the user by ID from the database.
             }

             /**
              * Updates an existing user.
              *
              * Business operation it performs:
              * - Updates an existing user entity in the database.
              *
              * Step by step logic:
              * 1. Fetch the existing user by ID.
              * 2. Encode the new password if provided.
              * 3. Save the updated user entity to the database.
              *
              * Transaction behavior:
              * - This method is transactional to ensure data consistency.
              *
              * Validation rules:
              * - Ensures that the user exists before updating.
              *
              * Error handling:
              * - Throws RuntimeException if the user is not found.
              *
              * Interactions with repositories/other services:
              * - Interacts with UserRepository to fetch and save the user entity.
              *
              * Security checks:
              * - Encodes the user's password before saving if a new password is provided.
              *
              * @param user the user entity to update
              * @return the updated user entity
              */
             public User updateUser(User user) {
                 User existingUser = userRepository.findById(user.getId()) // Fetch the existing user by ID.
                         .orElseThrow(() -> new RuntimeException("User not found")); // Throw an exception if the user is not found.

                 if (user.getPassword() != null && !user.getPassword().isEmpty()) { // Check if a new password is provided.
                     user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode the new password.
                 } else {
                     user.setPassword(existingUser.getPassword()); // Keep the existing password if no new password is provided.
                 }

                 return userRepository.save(user); // Save the updated user entity to the database.
             }

             /**
              * Toggles the status of a user (active/inactive).
              *
              * Business operation it performs:
              * - Toggles the active status of a user entity in the database.
              *
              * Step by step logic:
              * 1. Fetch the user by ID.
              * 2. Toggle the active status.
              * 3. Save the updated user entity to the database.
              *
              * Transaction behavior:
              * - This method is transactional to ensure data consistency.
              *
              * Validation rules:
              * - Ensures that the user exists before toggling status.
              *
              * Error handling:
              * - Throws RuntimeException if the user is not found.
              *
              * Interactions with repositories/other services:
              * - Interacts with UserRepository to fetch and save the user entity.
              *
              * Security checks:
              * - No specific security checks.
              *
              * @param id the ID of the user to toggle status
              */
             public void toggleUserStatus(Long id) {
                 User user = userRepository.findById(id) // Fetch the user by ID.
                         .orElseThrow(() -> new RuntimeException("User not found")); // Throw an exception if the user is not found.
                 user.setIsActive(!user.getIsActive()); // Toggle the active status.
                 userRepository.save(user); // Save the updated user entity to the database.
             }

             /**
              * Updates the last login time of a user.
              *
              * Business operation it performs:
              * - Updates the last login time of a user entity in the database.
              *
              * Step by step logic:
              * 1. Fetch the user by username.
              * 2. Update the last login time.
              * 3. Save the updated user entity to the database.
              *
              * Transaction behavior:
              * - This method is transactional to ensure data consistency.
              *
              * Validation rules:
              * - Ensures that the user exists before updating the last login time.
              *
              * Error handling:
              * - No specific error handling.
              *
              * Interactions with repositories/other services:
              * - Interacts with UserRepository to fetch and save the user entity.
              *
              * Security checks:
              * - No specific security checks.
              *
              * @param username the username of the user to update last login time
              */
             public void updateLastLogin(String username) {
                 userRepository.findByUsername(username).ifPresent(user -> { // Fetch the user by username.
                     user.setLastLogin(LocalDateTime.now()); // Update the last login time.
                     userRepository.save(user); // Save the updated user entity to the database.
                 });
             }

             /**
              * Fetches all active users.
              *
              * Business operation it performs:
              * - Retrieves all active user entities from the database.
              *
              * Step by step logic:
              * 1. Call the findByIsActiveTrue method of UserRepository to get all active users.
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
              * - Interacts with UserRepository to fetch all active user data.
              *
              * Security checks:
              * - No specific security checks.
              *
              * @return a list of all active user entities
              */
             public List<User> getActiveUsers() {
                 return userRepository.findByIsActiveTrue(); // Fetch all active users from the database.
             }

             /**
              * Checks if a username exists.
              *
              * Business operation it performs:
              * - Checks if a user entity with the specified username exists in the database.
              *
              * Step by step logic:
              * 1. Call the existsByUsername method of UserRepository with the given username.
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
              * - Interacts with UserRepository to check if the username exists.
              *
              * Security checks:
              * - No specific security checks.
              *
              * @param username the username to check
              * @return true if the username exists, false otherwise
              */
             public boolean existsByUsername(String username) {
                 return userRepository.existsByUsername(username); // Check if the username exists in the database.
             }

             /**
              * Deactivates a user.
              *
              * Business operation it performs:
              * - Deactivates a user entity in the database.
              *
              * Step by step logic:
              * 1. Fetch the user by ID.
              * 2. Set the active status to false.
              * 3. Save the updated user entity to the database.
              *
              * Transaction behavior:
              * - This method is transactional to ensure data consistency.
              *
              * Validation rules:
              * - Ensures that the user exists before deactivating.
              *
              * Error handling:
              * - Throws RuntimeException if the user is not found.
              *
              * Interactions with repositories/other services:
              * - Interacts with UserRepository to fetch and save the user entity.
              *
              * Security checks:
              * - No specific security checks.
              *
              * @param id the ID of the user to deactivate
              */
             public void deactivateUser(Long id) {
                 User user = userRepository.findById(id) // Fetch the user by ID.
                         .orElseThrow(() -> new RuntimeException("User not found")); // Throw an exception if the user is not found.
                 user.setIsActive(false); // Set the active status to false.
                 userRepository.save(user); // Save the updated user entity to the database.
             }

             /**
              * Activates a user.
              *
              * Business operation it performs:
              * - Activates a user entity in the database.
              *
              * Step by step logic:
              * 1. Fetch the user by ID.
              * 2. Set the active status to true.
              * 3. Save the updated user entity to the database.
              *
              * Transaction behavior:
              * - This method is transactional to ensure data consistency.
              *
              * Validation rules:
              * - Ensures that the user exists before activating.
              *
              * Error handling:
              * - Throws RuntimeException if the user is not found.
              *
              * Interactions with repositories/other services:
              * - Interacts with UserRepository to fetch and save the user entity.
              *
              * Security checks:
              * - No specific security checks.
              *
              * @param id the ID of the user to activate
              */
             public void activateUser(Long id) {
                 User user = userRepository.findById(id) // Fetch the user by ID.
                         .orElseThrow(() -> new RuntimeException("User not found")); // Throw an exception if the user is not found.
                 user.setIsActive(true); // Set the active status to true.
                 userRepository.save(user); // Save the updated user entity to the database.
             }


         }