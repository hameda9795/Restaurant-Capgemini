package com.example.Restaurant.services;

                import com.example.Restaurant.model.*; // Import all classes from the model package.
                import com.example.Restaurant.repository.CartItemRepository; // Import the CartItemRepository interface.
                import com.example.Restaurant.repository.OrderRepository; // Import the OrderRepository interface.
                import org.springframework.beans.factory.annotation.Autowired; // Import the Autowired annotation.
                import org.springframework.messaging.simp.SimpMessagingTemplate; // Import the SimpMessagingTemplate class.
                import org.springframework.stereotype.Service; // Import the Service annotation.
                import org.springframework.transaction.annotation.Transactional; // Import the Transactional annotation.

                import java.time.LocalDateTime; // Import the LocalDateTime class.
                import java.util.List; // Import the List interface.
                import java.util.Map; // Import the Map interface.

                /**
                 * Service class for managing order-related operations.
                 *
                 * Main purpose of this service:
                 * - To handle business logic related to order entities.
                 *
                 * Business features it provides:
                 * - Placing a new order.
                 * - Updating the status of an existing order.
                 * - Fetching all orders.
                 * - Fetching a specific order by ID.
                 * - Fetching orders by status.
                 *
                 * Dependencies and component interactions:
                 * - OrderRepository: Used to perform CRUD operations on order entities.
                 * - CartItemRepository: Used to fetch and delete cart items.
                 * - RecipeService: Used to fetch recipes for foods.
                 * - IngredientService: Used to manage ingredient stock.
                 * - SimpMessagingTemplate: Used to send real-time messages.
                 *
                 * Transaction handling:
                 * - Methods that modify data (placeOrder, updateOrderStatus, updateIngredientsStock) are annotated with @Transactional to ensure data consistency.
                 *
                 * Security/validation considerations:
                 * - Ensures that order data is correctly handled and stored.
                 * - Validates the presence of cart items before placing an order.
                 * - Validates the table number before placing an order.
                 * - Validates the presence of order before updating its status.
                 */
                @Service // Marks this class as a Spring service component, making it eligible for component scanning and dependency injection.
                public class OrderService {

                    @Autowired // Injects the OrderRepository dependency into this service.
                    private OrderRepository orderRepository;

                    @Autowired // Injects the CartItemRepository dependency into this service.
                    private CartItemRepository cartItemRepository;

                    @Autowired // Injects the RecipeService dependency into this service.
                    private RecipeService recipeService;

                    @Autowired // Injects the IngredientService dependency into this service.
                    private IngredientService ingredientService;

                    @Autowired // Injects the SimpMessagingTemplate dependency into this service.
                    private SimpMessagingTemplate messagingTemplate;

                    /**
                     * Places a new order.
                     *
                     * Business operation it performs:
                     * - Places a new order based on the current cart items.
                     *
                     * Step by step logic:
                     * 1. Fetch all cart items.
                     * 2. Validate that the cart is not empty.
                     * 3. Validate the table number.
                     * 4. Create a new order and set its properties.
                     * 5. Create order items from cart items.
                     * 6. Save the order to the database.
                     * 7. Clear the cart.
                     *
                     * Transaction behavior:
                     * - This method is transactional to ensure data consistency.
                     *
                     * Validation rules:
                     * - Ensures that the cart is not empty.
                     * - Ensures that the table number is valid.
                     *
                     * Error handling:
                     * - Throws IllegalStateException if the cart is empty.
                     * - Throws IllegalArgumentException if the table number is invalid.
                     *
                     * Interactions with repositories/other services:
                     * - Interacts with CartItemRepository to fetch and delete cart items.
                     * - Interacts with OrderRepository to save the order.
                     *
                     * Security checks:
                     * - No specific security checks.
                     *
                     * @param tableNumber the table number for the order
                     * @return the placed order
                     */
                    @Transactional
                    public Order placeOrder(String tableNumber) {
                        List<CartItem> cartItems = cartItemRepository.findAll(); // Fetch all cart items
                        if (cartItems.isEmpty()) { // Check if the cart is empty
                            throw new IllegalStateException("The cart is empty!"); // Throw an exception if the cart is empty
                        }

                        try {
                            int tableNum = Integer.parseInt(tableNumber); // Parse the table number
                            if (tableNum < 1 || tableNum > 25) { // Validate the table number range
                                throw new IllegalArgumentException("Table number must be between 1 and 25"); // Throw an exception if the table number is invalid
                            }
                        } catch (NumberFormatException e) { // Handle number format exception
                            throw new IllegalArgumentException("Invalid table number"); // Throw an exception if the table number is not a valid number
                        }

                        Order order = new Order(); // Create a new order
                        order.setTableNumber(tableNumber); // Set the table number for the order
                        order.setOrderTime(LocalDateTime.now()); // Set the order time to the current time
                        order.setStatus("Pending"); // Set the initial status of the order to "Pending"
                        order.setTotalPrice(cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum()); // Calculate and set the total price of the order

                        List<OrderItem> orderItems = cartItems.stream() // Create order items from cart items
                                .map(cartItem -> new OrderItem(order, cartItem.getFood(), cartItem.getQuantity()))
                                .toList();

                        order.setOrderItems(orderItems); // Set the order items for the order
                        Order savedOrder = orderRepository.save(order); // Save the order to the database
                        cartItemRepository.deleteAll(); // Clear the cart

                        return savedOrder; // Return the placed order
                    }

                    /**
                     * Updates the status of an existing order.
                     *
                     * Business operation it performs:
                     * - Updates the status of an order based on the specified status.
                     *
                     * Step by step logic:
                     * 1. Fetch the order by ID.
                     * 2. Validate the new status.
                     * 3. If the new status is "Preparing", check ingredient availability and update stock.
                     * 4. Update the order status and save the order.
                     *
                     * Transaction behavior:
                     * - This method is transactional to ensure data consistency.
                     *
                     * Validation rules:
                     * - Ensures that the order exists.
                     * - Ensures that the new status is valid.
                     *
                     * Error handling:
                     * - Throws RuntimeException if ingredient availability check fails.
                     * - Throws RuntimeException if stock update fails.
                     *
                     * Interactions with repositories/other services:
                     * - Interacts with OrderRepository to fetch and save the order.
                     * - Interacts with RecipeService to fetch recipes.
                     * - Interacts with IngredientService to update ingredient stock.
                     * - Interacts with SimpMessagingTemplate to send real-time messages.
                     *
                     * Security checks:
                     * - No specific security checks.
                     *
                     * @param orderId the ID of the order to update
                     * @param status the new status to set
                     */

                    @Transactional
                    public void updateOrderStatus(Long orderId, String status) {
                        Order order = getOrderById(orderId);

                        if ("Ready".equals(status)) {
                            try {
                                if (!checkIngredientsAvailability(order)) {
                                    throw new RuntimeException("Insufficient ingredient stock!");
                                }
                                updateIngredientsStock(order);
                            } catch (Exception e) {
                                throw new RuntimeException("Error updating stock: " + e.getMessage());
                            }
                        }

                        order.setStatus(status);
                        if ("Delivered".equals(status)) {
                            order.setDeliveryTime(LocalDateTime.now());
                        }
                        orderRepository.save(order);
                    }

                    /**
                     * Checks the availability of ingredients for an order.
                     *
                     * Business operation it performs:
                     * - Checks if the required ingredients for the order are available in stock.
                     *
                     * Step by step logic:
                     * 1. Iterate through the order items.
                     * 2. Fetch the recipe for each food item.
                     * 3. Check if the required amount of each ingredient is available.
                     * 4. Send a real-time alert if any ingredient is insufficient.
                     *
                     * Transaction behavior:
                     * - This method performs a read operation and does not modify any data.
                     *
                     * Validation rules:
                     * - Ensures that the recipe exists for each food item.
                     *
                     * Error handling:
                     * - Throws RuntimeException if the recipe is not found.
                     *
                     * Interactions with repositories/other services:
                     * - Interacts with RecipeService to fetch recipes.
                     * - Interacts with SimpMessagingTemplate to send real-time alerts.
                     *
                     * Security checks:
                     * - No specific security checks.
                     *
                     * @param order the order to check
                     * @return true if all ingredients are available, false otherwise
                     */
                    private boolean checkIngredientsAvailability(Order order) {
                        for (OrderItem orderItem : order.getOrderItems()) { // Iterate through the order items
                            Recipe recipe = recipeService.getRecipeByFoodId(orderItem.getFood().getId()) // Fetch the recipe for each food item
                                    .orElseThrow(() -> new RuntimeException("Recipe for food " + orderItem.getFood().getName() + " not found")); // Throw an exception if the recipe is not found

                            for (RecipeIngredient recipeIngredient : recipe.getIngredients()) { // Iterate through the recipe ingredients
                                Ingredient ingredient = recipeIngredient.getIngredient(); // Get the ingredient
                                double requiredAmount = recipeIngredient.getAmount() * orderItem.getQuantity(); // Calculate the required amount of the ingredient

                                if (ingredient.getCurrentStock() < requiredAmount) { // Check if the ingredient stock is insufficient
                                    messagingTemplate.convertAndSend("/topic/alerts", // Send a real-time alert if the ingredient stock is insufficient
                                            Map.of(
                                                    "type", "INSUFFICIENT_STOCK",
                                                    "ingredient", ingredient.getName(),
                                                    "required", requiredAmount,
                                                    "available", ingredient.getCurrentStock()
                                            ));
                                    return false; // Return false if any ingredient is insufficient
                                }
                            }
                        }
                        return true; // Return true if all ingredients are available
                    }

                    /**
                     * Updates the stock of ingredients for an order.
                     *
                     * Business operation it performs:
                     * - Updates the stock of ingredients based on the order items.
                     *
                     * Step by step logic:
                     * 1. Iterate through the order items.
                     * 2. Fetch the recipe for each food item.
                     * 3. Update the stock of each ingredient.
                     * 4. Send real-time updates and alerts.
                     *
                     * Transaction behavior:
                     * - This method is transactional to ensure data consistency.
                     *
                     * Validation rules:
                     * - Ensures that the recipe exists for each food item.
                     *
                     * Error handling:
                     * - Throws RuntimeException if the recipe is not found.
                     *
                     * Interactions with repositories/other services:
                     * - Interacts with RecipeService to fetch recipes.
                     * - Interacts with IngredientService to update ingredient stock.
                     * - Interacts with SimpMessagingTemplate to send real-time updates and alerts.
                     *
                     * Security checks:
                     * - No specific security checks.
                     *
                     * @param order the order to update stock for
                     */
                    @Transactional
                    private void updateIngredientsStock(Order order) {
                        for (OrderItem orderItem : order.getOrderItems()) { // Iterate through the order items
                            Recipe recipe = recipeService.getRecipeByFoodId(orderItem.getFood().getId()) // Fetch the recipe for each food item
                                    .orElseThrow(() -> new RuntimeException("Recipe for food " + orderItem.getFood().getName() + " not found")); // Throw an exception if the recipe is not found

                            for (RecipeIngredient recipeIngredient : recipe.getIngredients()) { // Iterate through the recipe ingredients
                                Ingredient ingredient = recipeIngredient.getIngredient(); // Get the ingredient
                                double requiredAmount = recipeIngredient.getAmount() * orderItem.getQuantity(); // Calculate the required amount of the ingredient
                                double newStock = ingredient.getCurrentStock() - requiredAmount; // Calculate the new stock of the ingredient

                                ingredient.setCurrentStock(newStock); // Update the ingredient stock
                                Ingredient updatedIngredient = ingredientService.saveIngredient(ingredient); // Save the updated ingredient

                                // Send real-time stock update
                                messagingTemplate.convertAndSend("/topic/stock-updates",
                                        Map.of(
                                                "type", "STOCK_UPDATE",
                                                "ingredientId", updatedIngredient.getId(),
                                                "newStock", updatedIngredient.getCurrentStock()
                                        ));

                                // Send low stock alert
                                if (newStock <= ingredient.getThreshold()) {
                                    messagingTemplate.convertAndSend("/topic/alerts",
                                            Map.of(
                                                    "type", "LOW_STOCK_ALERT",
                                                    "ingredient", ingredient
                                            ));
                                }
                            }
                        }
                    }

                    /**
                     * Fetches all orders.
                     *
                     * Business operation it performs:
                     * - Retrieves all order entities from the database.
                     *
                     * Step by step logic:
                     * 1. Call the findAll method of OrderRepository to get all orders.
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
                     * - Interacts with OrderRepository to fetch all order data.
                     *
                     * Security checks:
                     * - No specific security checks.
                     *
                     * @return a list of all order entities
                     */
                    public List<Order> getAllOrders() {
                        return orderRepository.findAll(); // Fetch all orders from the database
                    }

                    /**
                     * Fetches a specific order by ID.
                     *
                     * Business operation it performs:
                     * - Retrieves an order entity from the database based on the specified ID.
                     *
                     * Step by step logic:
                     * 1. Call the findById method of OrderRepository with the given ID.
                     *
                     * Transaction behavior:
                     * - This method performs a read operation and does not modify any data.
                     *
                     * Validation rules:
                     * - No specific validation rules.
                     *
                     * Error handling:
                     * - Throws IllegalArgumentException if the order is not found.
                     *
                     * Interactions with repositories/other services:
                     * - Interacts with OrderRepository to fetch order data by ID.
                     *
                     * Security checks:
                     * - No specific security checks.
                     *
                     * @param orderId the ID of the order to fetch
                     * @return the order entity if found
                     */
                    public Order getOrderById(Long orderId) {
                        return orderRepository.findById(orderId) // Fetch the order by ID
                                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " not found")); // Throw an exception if the order is not found
                    }

                    /**
                     * Fetches orders by status.
                     *
                     * Business operation it performs:
                     * - Retrieves order entities from the database based on the specified status.
                     *
                     * Step by step logic:
                     * 1. Call the findByStatus method of OrderRepository with the given status.
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
                     * - Interacts with OrderRepository to fetch order data by status.
                     *
                     * Security checks:
                     * - No specific security checks.
                     *
                     * @param status the status of the orders to fetch
                     * @return a list of order entities with the specified status
                     */
                    public List<Order> getOrdersByStatus(String status) {
                        return orderRepository.findByStatus(status); // Fetch orders by status from the database
                    }

                    //active orders
                    public List<Order> getActiveOrdersCount() {
                        return orderRepository.findByStatus("Pending");
                    }
                }