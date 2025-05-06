package com.example.Restaurant.controller;

import com.example.Restaurant.model.CartItem;
import com.example.Restaurant.model.Food;
import com.example.Restaurant.repository.CartItemRepository;
import com.example.Restaurant.repository.FoodRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The CartController class handles operations related to the shopping cart.
 * Main features include adding items to the cart, viewing the cart, updating item quantities, and checking out.
 *
 * Dependencies:
 * - CartItemRepository: Repository for cart item operations.
 * - FoodRepository: Repository for food item operations.
 */
@Controller // Marks this class as a Spring MVC controller
@RequestMapping("/cart") // Maps requests starting with /cart to this controller
public class CartController {

    @Autowired // Injects the CartItemRepository bean
    private CartItemRepository cartItemRepository; // Repository for cart item operations

    @Autowired // Injects the FoodRepository bean
    private FoodRepository foodRepository; // Repository for food item operations

    /**
     * Adds a food item to the cart.
     *
     * @param foodId the ID of the food item to add
     * @param quantity the quantity of the food item to add
     * @return a success message
     */
    @PostMapping("/add") // Maps POST requests to /cart/add to this method
    @ResponseBody // Indicates that the return value should be used as the response body
    public String addToCart(@RequestParam Long foodId, @RequestParam Integer quantity) {
        // Find the food item by ID or throw an exception if not found
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid food Id:" + foodId));

        // Create a new CartItem object
        CartItem cartItem = new CartItem();
        // Set the food item
        cartItem.setFood(food);
        // Set the quantity
        cartItem.setQuantity(quantity);
        // Save the cart item to the repository
        cartItemRepository.save(cartItem);

        // Return a success message
        return "Added to cart successfully";
    }

    /**
     * Displays the cart.
     *
     * @param model the Model object used to pass data to the view
     * @param session the HttpSession object used to manage session attributes
     * @return the name of the view to render
     */
    @GetMapping // Maps GET requests to /cart to this method
    public String viewCart(Model model, HttpSession session) {
        // Get all cart items from the repository
        List<CartItem> cartItems = cartItemRepository.findAll();

        // If the cart is empty, remove the cartItems attribute from the session
        if (cartItems.isEmpty()) {
            session.removeAttribute("cartItems");
        }

        // Add the cart items to the model
        model.addAttribute("cartItems", cartItems);
        // Return the view name for the cart
        return "cart";
    }

    /**
     * Updates the quantity of a cart item.
     *
     * @param itemId the ID of the cart item to update
     * @param quantity the new quantity of the cart item
     * @return the updated cart item count
     */
    @PostMapping("/update") // Maps POST requests to /cart/update to this method
    @ResponseBody // Indicates that the return value should be used as the response body
    public String updateCart(@RequestParam Long itemId, @RequestParam Integer quantity) {
        // Find the cart item by ID or throw an exception if not found
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + itemId));

        // If the quantity is less than or equal to 0, delete the cart item
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            // Otherwise, update the quantity and save the cart item
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        // Return the updated cart item count
        return String.valueOf(getCartItemCount());
    }

    /**
     * Displays the checkout page.
     *
     * @param model the Model object used to pass data to the view
     * @return the name of the view to render
     */
    @GetMapping("/checkout") // Maps GET requests to /cart/checkout to this method
    public String checkout(Model model) {
        // Add all cart items to the model
        model.addAttribute("cartItems", cartItemRepository.findAll());
        // Return the view name for the checkout page
        return "checkout";
    }

    /**
     * Gets the total count of items in the cart.
     *
     * @return the total count of items in the cart
     */
    @GetMapping("/count") // Maps GET requests to /cart/count to this method
    @ResponseBody // Indicates that the return value should be used as the response body
    public int getCartItemCount() {
        // Get all cart items from the repository, map their quantities to an int stream, and sum the quantities
        return ((List<CartItem>) cartItemRepository.findAll())
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Displays the home page.
     *
     * @param model the Model object used to pass data to the view
     * @return the name of the view to render
     */
    @GetMapping("/") // Maps GET requests to /cart/ to this method
    public String getHome(Model model) {
        // Return the view name for the home page
        return "home";
    }
}