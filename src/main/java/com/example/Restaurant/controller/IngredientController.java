package com.example.Restaurant.controller;

                import com.example.Restaurant.model.Supplier;
                import com.example.Restaurant.services.SupplierService;

                import com.example.Restaurant.model.Ingredient;
                import com.example.Restaurant.services.IngredientService;
                import com.example.Restaurant.services.RecipeService;
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.http.HttpStatus;
                import org.springframework.http.ResponseEntity;
                import org.springframework.messaging.simp.SimpMessagingTemplate;
                import org.springframework.stereotype.Controller;
                import org.springframework.ui.Model;
                import org.springframework.web.bind.annotation.*;

                import java.util.List;
                import java.util.Map;

                /**
                 * The IngredientController class handles operations related to ingredients in the restaurant application.
                 *
                 * Main Responsibilities:
                 * - Display and manage ingredients.
                 * - Handle ingredient stock updates.
                 * - Provide real-time notifications for stock levels.
                 *
                 * Component Relationships:
                 * - Interacts with IngredientService for ingredient operations.
                 * - Interacts with RecipeService to check ingredient usage in recipes.
                 * - Uses SimpMessagingTemplate for real-time notifications.
                 *
                 * Dependencies:
                 * - IngredientService: Service for ingredient-related operations.
                 * - RecipeService: Service for recipe-related operations.
                 * - SimpMessagingTemplate: For sending real-time notifications.
                 *
                 * Security Considerations:
                 * - Ensure proper validation of ingredient data.
                 * - Handle real-time notifications securely.
                 */
                @Controller // Marks this class as a Spring MVC controller
                @RequestMapping("/ingredients") // Maps requests starting with /ingredients to this controller
                public class IngredientController {

                    @Autowired
                    private SupplierService supplierService;

                    @Autowired // Injects the IngredientService bean
                    private IngredientService ingredientService; // Service for ingredient-related operations

                    @Autowired // Injects the SimpMessagingTemplate bean
                    private SimpMessagingTemplate messagingTemplate; // For sending real-time notifications

                    @Autowired // Injects the RecipeService bean
                    private RecipeService recipeService; // Service for recipe-related operations

                    /**
                     * Displays the ingredients page.
                     *
                     * @param model the Model object used to pass data to the view
                     * @return the name of the view to render
                     */
                    @GetMapping
                    public String viewIngredients(Model model) {
                        List<Ingredient> allIngredients = ingredientService.getAllIngredients();
                        List<Ingredient> lowStockIngredients = ingredientService.getLowStockIngredients();
                        List<Supplier> allSuppliers = supplierService.getAllSuppliers(); // ✅ Supplier listesi frontend'e gönderiliyor.

                        model.addAttribute("ingredients", allIngredients);
                        model.addAttribute("suppliers", allSuppliers);
                        model.addAttribute("lowStockIngredients", lowStockIngredients);
                        model.addAttribute("totalCount", allIngredients.size());
                        model.addAttribute("outOfStockCount", ingredientService.getOutOfStockCount());
                        model.addAttribute("lowStockCount", lowStockIngredients.size());
                        model.addAttribute("wellStockedCount", ingredientService.getWellStockedCount());

                        return "ingredients";
                    }

                    /**
                     * Retrieves an ingredient by its ID.
                     *
                     * @param id the ID of the ingredient to retrieve
                     * @return the ResponseEntity containing the Ingredient object or a not found status
                     */
                    @GetMapping("/get/{id}") // Maps GET requests to /ingredients/get/{id} to this method
                    @ResponseBody // Indicates that the return value should be used as the response body
                    public ResponseEntity<Ingredient> getIngredient(@PathVariable Long id) {
                        // Retrieve the ingredient by ID or return a not found status
                        return ingredientService.getIngredientById(id)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
                    }


                    @PostMapping("/add")
                    @ResponseBody
                    public ResponseEntity<Ingredient> addIngredient(@RequestBody Map<String, Object> payload) {
                        String name = (String) payload.get("name");
                        Double currentStock = Double.valueOf(payload.get("currentStock").toString());
                        Double threshold = Double.valueOf(payload.get("threshold").toString());
                        Long supplierId = null;

                        if (payload.get("supplier") != null) {
                            Map<String, Object> supplierMap = (Map<String, Object>) payload.get("supplier");
                            supplierId = Long.valueOf(supplierMap.get("id").toString());
                        }

                        Supplier supplier = null;
                        if (supplierId != null) {
                            supplier = supplierService.getSupplierById(supplierId)
                                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
                        }

                        Ingredient ingredient = new Ingredient();
                        ingredient.setName(name);
                        ingredient.setCurrentStock(currentStock);
                        ingredient.setThreshold(threshold);
                        ingredient.setSupplier(supplier);

                        Ingredient savedIngredient = ingredientService.saveIngredient(ingredient);
                        return ResponseEntity.ok(savedIngredient);
                    }

                    @PostMapping("/update/{id}")
                    @ResponseBody
                    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
                        return ingredientService.getIngredientById(id).map(existingIngredient -> {
                            existingIngredient.setName((String) payload.get("name"));
                            existingIngredient.setCurrentStock(Double.valueOf(payload.get("currentStock").toString()));
                            existingIngredient.setThreshold(Double.valueOf(payload.get("threshold").toString()));

                            if (payload.get("supplier") != null) {
                                Map<String, Object> supplierMap = (Map<String, Object>) payload.get("supplier");
                                Long supplierId = Long.valueOf(supplierMap.get("id").toString());
                                Supplier supplier = supplierService.getSupplierById(supplierId)
                                        .orElseThrow(() -> new RuntimeException("Supplier not found"));
                                existingIngredient.setSupplier(supplier);
                            }

                            Ingredient updatedIngredient = ingredientService.saveIngredient(existingIngredient);
                            return ResponseEntity.ok(updatedIngredient);
                        }).orElse(ResponseEntity.notFound().build());
                    }

                    @DeleteMapping("/delete/{id}")
                    @ResponseBody
                    public ResponseEntity<?> deleteIngredient(@PathVariable Long id) {
                        try {
                            ingredientService.deleteIngredient(id);
                            return ResponseEntity.ok().build();
                        } catch (Exception e) {
                            return ResponseEntity.badRequest().body(e.getMessage());
                        }
                    }

                    /**
                     * Updates the stock level of an ingredient.
                     *
                     * @param id the ID of the ingredient to update
                     * @param request the request body containing the new stock level
                     * @return the ResponseEntity containing the updated Ingredient object or a not found status
                     */
                    @PostMapping("/update-stock/{id}") // Maps POST requests to /ingredients/update-stock/{id} to this method
                    @ResponseBody // Indicates that the return value should be used as the response body
                    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody Map<String, Double> request) {
                        // Retrieve the existing ingredient by ID
                        return ingredientService.getIngredientById(id)
                                .map(ingredient -> {
                                    // Update the current stock level
                                    ingredient.setCurrentStock(request.get("currentStock"));
                                    Ingredient updated = ingredientService.saveIngredient(ingredient);
                                    // Check and notify stock level
                                    checkAndNotifyStockLevel(updated);

                                    // Send real-time stock update notification
                                    messagingTemplate.convertAndSend("/topic/stock-updates",
                                            Map.of(
                                                    "type", "STOCK_UPDATE",
                                                    "ingredientId", updated.getId(),
                                                    "newStock", updated.getCurrentStock()
                                            ));

                                    return ResponseEntity.ok(updated); // Return the updated ingredient
                                })
                                .orElse(ResponseEntity.notFound().build()); // Return not found status if ingredient does not exist
                    }

                    /**
                     * Deletes an ingredient.
                     *
                     * @param id the ID of the ingredient to delete
                     * @return the ResponseEntity containing a success message or an error message
                     */


                    /**
                     * Checks the stock level of an ingredient and sends a notification if the stock is low.
                     *
                     * @param ingredient the Ingredient object to check
                     */
                    private void checkAndNotifyStockLevel(Ingredient ingredient) {
                        // Check if the current stock is less than or equal to the threshold
                        if (ingredient.getCurrentStock() <= ingredient.getThreshold()) {
                            // Send a low stock alert notification
                            messagingTemplate.convertAndSend("/topic/alerts",
                                    Map.of(
                                            "type", "LOW_STOCK_ALERT",
                                            "ingredient", ingredient
                                    ));
                        }
                    }

                    /**
                     * Retrieves a list of ingredients with low stock levels.
                     *
                     * @return the list of ingredients with low stock levels
                     */
                    @GetMapping("/low-stock") // Maps GET requests to /ingredients/low-stock to this method
                    @ResponseBody // Indicates that the return value should be used as the response body
                    public List<Ingredient> getLowStockIngredients() {
                        return ingredientService.getLowStockIngredients(); // Return the list of low stock ingredients
                    }
                }