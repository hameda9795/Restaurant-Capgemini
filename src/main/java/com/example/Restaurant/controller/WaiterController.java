package com.example.Restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The WaiterController class handles operations related to waiter management in the restaurant application.
 *
 * Main Responsibilities:
 * - View waiter dashboard.
 *
 * Component Relationships:
 * - Interacts with the view layer to display waiter-related information.
 *
 * Dependencies:
 * - None.
 *
 * Security Considerations:
 * - Ensure proper access control to the waiter dashboard.
 */
@Controller
@RequestMapping("/waiters")
public class WaiterController {

    /**
     * Displays the waiter dashboard.
     *
     * @return the name of the view to render
     */
    @GetMapping
    public String viewWaiterDashboard() {
        // This returns the view name "waiters", which refers to waiters.html
        return "waiters";
    }
}