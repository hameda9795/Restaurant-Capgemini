package com.example.Restaurant.controller;

import com.example.Restaurant.model.Order;
import com.example.Restaurant.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * The OrderController class handles operations related to customer orders in the restaurant application.
 *
 * Main Responsibilities:
 * - View all orders.
 * - Process checkout and place new orders.
 * - Update order status.
 * - Provide API endpoints for order operations.
 *
 * Component Relationships:
 * - Interacts with OrderService for order-related operations.
 * - Uses SimpMessagingTemplate for real-time notifications.
 *
 * Dependencies:
 * - OrderService: Service for order-related operations.
 * - SimpMessagingTemplate: For sending real-time notifications.
 *
 * Security Considerations:
 * - Ensure proper validation of order data.
 * - Handle real-time notifications securely.
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * View all orders.
     *
     * @param model the Model object used to pass data to the view
     * @return the name of the view to render
     */
    @GetMapping
    public String viewOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

    /**
     * Process checkout and place new order.
     *
     * @param tableNumber the table number for the order
     * @param model the Model object used to pass data to the view
     * @return the name of the view to render
     * @throws IllegalArgumentException if the table number is invalid
     */
    @PostMapping("/checkout")
    public String checkout(@RequestParam String tableNumber, Model model) {
        try {
            // Validate table number
            int tableNum = Integer.parseInt(tableNumber);
            if (tableNum < 1 || tableNum > 25) {
                throw new IllegalArgumentException("Please select a table number between 1 and 25");
            }

            // Place order
            Order newOrder = orderService.placeOrder(tableNumber);

            // Send update to WebSocket subscribers
            List<Order> updatedOrders = orderService.getAllOrders();
            messagingTemplate.convertAndSend("/topic/orders", updatedOrders);

            // Add order to model for confirmation page
            model.addAttribute("order", newOrder);
            return "order-confirmation";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "cart";
        }
    }

    /**
     * Update order status.
     *
     * @param orderId the ID of the order to update
     * @param status the new status of the order
     * @return a success message
     * @throws IllegalArgumentException if the order ID or status is invalid
     */
    @PostMapping("/update-status")
    public ResponseEntity<String> updateOrderStatus(@RequestParam Long orderId,
                                                    @RequestParam String status) {
        try {
            orderService.updateOrderStatus(orderId, status);

            // Send update to WebSocket subscribers
            List<Order> updatedOrders = orderService.getAllOrders();
            messagingTemplate.convertAndSend("/topic/orders", updatedOrders);

            return ResponseEntity.ok("Order status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error updating order status: " + e.getMessage());
        }
    }

    /**
     * Get all orders (API endpoint).
     *
     * @return the list of all orders
     */
    @GetMapping("/all")
    @ResponseBody
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * Get specific order.
     *
     * @param id the ID of the order to retrieve
     * @return the Order object
     * @throws IllegalArgumentException if the order ID is invalid
     */
    @GetMapping("/{id}")
    @ResponseBody
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}