package com.example.Restaurant.controller;

    import com.example.Restaurant.model.Food;
    import com.example.Restaurant.repository.FoodRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.util.StringUtils;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.nio.file.StandardCopyOption;

    /**
     * The FoodController class handles operations related to food items in the restaurant application.
     *
     * Main Responsibilities:
     * - Display the home page.
     * - Display the menu based on category.
     * - Manage food items (add, edit, delete).
     * - Handle file uploads for food images.
     *
     * Component Relationships:
     * - Interacts with the FoodRepository to perform CRUD operations on food items.
     *
     * Dependencies:
     * - FoodRepository: Repository for food item operations.
     *
     * Security Considerations:
     * - Ensure proper validation of food item data.
     * - Handle file uploads securely to prevent file injection attacks.
     */
    @Controller // Marks this class as a Spring MVC controller
    public class FoodController {

        @Autowired // Injects the FoodRepository bean
        private FoodRepository foodRepository; // Repository for food item operations

        /**
         * Displays the home page.
         *
         * @return the name of the view to render
         */
        @GetMapping("/") // Maps GET requests to / to this method
        public String home() {
            return "home"; // Return the view name for the home page
        }

        /**
         * Displays the menu based on the specified category.
         *
         * @param category the category of food items to display
         * @param model the Model object used to pass data to the view
         * @return the name of the view to render
         */
        @GetMapping("/menu/{category}") // Maps GET requests to /menu/{category} to this method
        public String menu(@PathVariable String category, Model model) {
            // Add the list of food items in the specified category to the model
            model.addAttribute("foods", foodRepository.findByCategory(category));
            return "menu"; // Return the view name for the menu page
        }

        /**
         * Displays the form for managing food items.
         *
         * @param model the Model object used to pass data to the view
         * @return the name of the view to render
         */
        @GetMapping("/form") // Maps GET requests to /form to this method
        public String showForm(Model model) {
            // Add the list of all food items to the model
            model.addAttribute("foods", foodRepository.findAll());
            return "form"; // Return the view name for the form page
        }

        /**
         * Displays the form for editing an existing food item.
         *
         * @param id the ID of the food item to edit
         * @param model the Model object used to pass data to the view
         * @return the name of the view to render
         * @throws IllegalArgumentException if the food item ID is invalid
         */
        @GetMapping("/form/edit/{id}") // Maps GET requests to /form/edit/{id} to this method
        public String editForm(@PathVariable Long id, Model model) {
            // Find the food item by ID or throw an exception if not found
            Food food = foodRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid food Id:" + id));
            // Add the food item and edit flag to the model
            model.addAttribute("food", food);
            model.addAttribute("isEdit", true);
            return "form"; // Return the view name for the form page
        }

        /**
         * Deletes a food item.
         *
         * @param id the ID of the food item to delete
         * @return the redirect URL
         */
        @PostMapping("/form/delete/{id}") // Maps POST requests to /form/delete/{id} to this method
        public String deleteFood(@PathVariable Long id) {
            // Delete the food item by ID
            foodRepository.deleteById(id);
            return "redirect:/form"; // Redirect to the form page
        }

        /**
         * Saves a food item. Handles both adding new food items and updating existing ones.
         *
         * @param food the Food object containing the food item details
         * @param file the MultipartFile object containing the uploaded image file
         * @return the redirect URL
         */
        @PostMapping("/save") // Maps POST requests to /save to this method
        public String saveFood(@ModelAttribute Food food, @RequestParam(value = "file", required = false) MultipartFile file) {
            // Check if a file is uploaded
            if (file != null && !file.isEmpty()) {
                // Clean the file name to prevent file injection attacks
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                try {
                    // Define the upload directory
                    String uploadDir = "src/main/resources/static/uploads/";
                    // Create the upload directory if it does not exist
                    Files.createDirectories(Paths.get(uploadDir));
                    // Copy the file to the upload directory
                    Files.copy(file.getInputStream(),
                            Paths.get(uploadDir + fileName),
                            StandardCopyOption.REPLACE_EXISTING);
                    // Set the image file name in the food object
                    food.setImage(fileName);
                } catch (IOException e) {
                    e.printStackTrace(); // Print the stack trace for debugging
                }
            }
            // Save the food item to the repository
            foodRepository.save(food);
            return "redirect:/form"; // Redirect to the form page
        }

        /**
         * Retrieves a food item by its ID.
         *
         * @param id the ID of the food item to retrieve
         * @return the Food object
         * @throws IllegalArgumentException if the food item ID is invalid
         */
        @GetMapping("/api/foods/{id}") // Maps GET requests to /api/foods/{id} to this method
        @ResponseBody // Indicates that the return value should be used as the response body
        public Food getFoodById(@PathVariable Long id) {
            // Find the food item by ID or throw an exception if not found
            return foodRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid food Id:" + id));
        }
    }