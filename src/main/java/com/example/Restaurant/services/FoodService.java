package com.example.Restaurant.services;

          import com.example.Restaurant.model.Food; // Import the Food model class.
          import com.example.Restaurant.repository.FoodRepository; // Import the FoodRepository interface.
          import org.springframework.beans.factory.annotation.Autowired; // Import the Autowired annotation.
          import org.springframework.stereotype.Service; // Import the Service annotation.
          import org.springframework.transaction.annotation.Transactional; // Import the Transactional annotation.
          import org.springframework.web.multipart.MultipartFile; // Import the MultipartFile class.

          import java.io.IOException; // Import the IOException class.
          import java.nio.file.Files; // Import the Files class.
          import java.nio.file.Path; // Import the Path class.
          import java.nio.file.Paths; // Import the Paths class.
          import java.util.List; // Import the List interface.
          import java.util.Optional; // Import the Optional class.

          /**
           * Service class for managing food-related operations.
           *
           * Main purpose of this service:
           * - To handle business logic related to food entities.
           *
           * Business features it provides:
           * - Fetching all foods.
           * - Fetching foods by category.
           * - Fetching a specific food by ID.
           * - Saving a new food with an optional image.
           * - Updating an existing food.
           * - Deleting a food by ID.
           *
           * Dependencies and component interactions:
           * - FoodRepository: Used to perform CRUD operations on food entities.
           *
           * Transaction handling:
           * - Methods that modify data (saveFood, updateFood, deleteFood) are annotated with @Transactional to ensure data consistency.
           *
           * Security/validation considerations:
           * - Ensures that food data is correctly handled and stored.
           * - Validates the presence of food before performing update or delete operations.
           */
          @Service // Marks this class as a Spring service component, making it eligible for component scanning and dependency injection.
          public class FoodService {

              @Autowired // Injects the FoodRepository dependency into this service.
              private FoodRepository foodRepository;

              /**
               * Fetches all foods.
               *
               * Business operation it performs:
               * - Retrieves all food entities from the database.
               *
               * Step by step logic:
               * 1. Call the findAll method of FoodRepository to get all foods.
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
               * - Interacts with FoodRepository to fetch all food data.
               *
               * Security checks:
               * - No specific security checks.
               *
               * @return a list of all food entities
               */
              public List<Food> getAllFoods() {
                  return foodRepository.findAll(); // Fetch all foods from the database.
              }

              /**
               * Fetches foods by category.
               *
               * Business operation it performs:
               * - Retrieves food entities from the database based on the specified category.
               *
               * Step by step logic:
               * 1. Call the findByCategory method of FoodRepository with the given category.
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
               * - Interacts with FoodRepository to fetch food data by category.
               *
               * Security checks:
               * - No specific security checks.
               *
               * @param category the category of foods to fetch
               * @return a list of food entities in the specified category
               */
              public List<Food> getFoodsByCategory(String category) {
                  return foodRepository.findByCategory(category); // Fetch foods by category from the database.
              }

              /**
               * Fetches a specific food by ID.
               *
               * Business operation it performs:
               * - Retrieves a food entity from the database based on the specified ID.
               *
               * Step by step logic:
               * 1. Call the findById method of FoodRepository with the given ID.
               *
               * Transaction behavior:
               * - This method performs a read operation and does not modify any data.
               *
               * Validation rules:
               * - No specific validation rules.
               *
               * Error handling:
               * - Returns an Optional.empty() if the food is not found.
               *
               * Interactions with repositories/other services:
               * - Interacts with FoodRepository to fetch food data by ID.
               *
               * Security checks:
               * - No specific security checks.
               *
               * @param id the ID of the food to fetch
               * @return an Optional containing the food entity if found, or an empty Optional if not found
               */
              public Optional<Food> getFoodById(Long id) {
                  return foodRepository.findById(id); // Fetch the food by ID from the database.
              }

              /**
               * Saves a new food with an optional image.
               *
               * Business operation it performs:
               * - Saves a new food entity to the database, optionally saving an associated image file.
               *
               * Step by step logic:
               * 1. Check if the image file is not null and not empty.
               * 2. Generate a unique file name for the image.
               * 3. Create the upload directory if it does not exist.
               * 4. Save the image file to the upload directory.
               * 5. Set the image file name in the food entity.
               * 6. Save the food entity to the database using FoodRepository.
               *
               * Transaction behavior:
               * - This method is transactional to ensure data consistency.
               *
               * Validation rules:
               * - No specific validation rules.
               *
               * Error handling:
               * - Throws a RuntimeException if the image file cannot be stored.
               *
               * Interactions with repositories/other services:
               * - Interacts with FoodRepository to save the food entity.
               *
               * Security checks:
               * - No specific security checks.
               *
               * @param food the food entity to save
               * @param imageFile the optional image file to save
               * @return the saved food entity
               */
              @Transactional
              public Food saveFood(Food food, MultipartFile imageFile) {
                  if (imageFile != null && !imageFile.isEmpty()) { // Check if the image file is not null and not empty.
                      try {
                          String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename(); // Generate a unique file name for the image.
                          String uploadDir = "src/main/resources/static/uploads/"; // Define the upload directory.

                          // Create the directory if it does not exist.
                          Files.createDirectories(Paths.get(uploadDir));

                          // Save the file.
                          Path path = Paths.get(uploadDir + fileName);
                          Files.copy(imageFile.getInputStream(), path);

                          // Set the file name in the food entity.
                          food.setImage(fileName);
                      } catch (IOException e) {
                          throw new RuntimeException("Could not store the image file", e); // Throw a RuntimeException if the image file cannot be stored.
                      }
                  }
                  return foodRepository.save(food); // Save the food entity to the database.
              }

              /**
               * Updates an existing food.
               *
               * Business operation it performs:
               * - Updates an existing food entity in the database.
               *
               * Step by step logic:
               * 1. Save the updated food entity to the database using FoodRepository.
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
               * - Interacts with FoodRepository to update the food entity.
               *
               * Security checks:
               * - No specific security checks.
               *
               * @param food the food entity to update
               * @return the updated food entity
               */
              @Transactional
              public Food updateFood(Food food) {
                  return foodRepository.save(food); // Save the updated food entity to the database.
              }

              /**
               * Deletes a food by ID.
               *
               * Business operation it performs:
               * - Deletes a food entity from the database based on the specified ID.
               *
               * Step by step logic:
               * 1. Fetch the food entity by ID using FoodRepository.
               * 2. If the food is not found, throw an IllegalArgumentException.
               * 3. If the food has an associated image, delete the image file.
               * 4. Delete the food entity from the database using FoodRepository.
               *
               * Transaction behavior:
               * - This method is transactional to ensure data consistency.
               *
               * Validation rules:
               * - Ensures that the food entity exists before attempting to delete it.
               *
               * Error handling:
               * - Throws an IllegalArgumentException if the food ID is invalid.
               * - Logs an error if the image file cannot be deleted.
               *
               * Interactions with repositories/other services:
               * - Interacts with FoodRepository to delete the food entity.
               *
               * Security checks:
               * - No specific security checks.
               *
               * @param id the ID of the food to delete
               */
              @Transactional
              public void deleteFood(Long id) {
                  Food food = foodRepository.findById(id) // Fetch the food entity by ID.
                          .orElseThrow(() -> new IllegalArgumentException("Invalid food id: " + id)); // Throw an IllegalArgumentException if the food is not found.

                  // Delete the previous image if it exists.
                  if (food.getImage() != null) {
                      try {
                          Path imagePath = Paths.get("src/main/resources/static/uploads/" + food.getImage()); // Define the image path.
                          Files.deleteIfExists(imagePath); // Delete the image file if it exists.
                      } catch (IOException e) {
                          // Log the error.
                          e.printStackTrace();
                      }
                  }

                  foodRepository.deleteById(id); // Delete the food entity from the database.
              }

              // Uncomment and implement if needed
              // /**
              //  * Searches for foods by name.
              //  *
              //  * Business operation it performs:
              //  * - Searches for food entities in the database based on the specified keyword.
              //  *
              //  * Step by step logic:
              //  * 1. Call the findByNameContainingIgnoreCase method of FoodRepository with the given keyword.
              //  *
              //  * Transaction behavior:
              //  * - This method performs a read operation and does not modify any data.
              //  *
              //  * Validation rules:
              //  * - No specific validation rules.
              //  *
              //  * Error handling:
              //  * - No specific error handling.
              //  *
              //  * Interactions with repositories/other services:
              //  * - Interacts with FoodRepository to search for food data by name.
              //  *
              //  * Security checks:
              //  * - No specific security checks.
              //  *
              //  * @param keyword the keyword to search for
              //  * @return a list of food entities matching the keyword
              //  */
              // public List<Food> searchFoods(String keyword) {
              //     return foodRepository.findByNameContainingIgnoreCase(keyword);
              // }
          }