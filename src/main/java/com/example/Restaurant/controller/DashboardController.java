package com.example.Restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The DashboardController class handles the routing for different user dashboards in the application.
 *
 * Main Responsibilities:
 * - Provide endpoints for accessing various user dashboards.
 *
 * Component Relationships:
 * - This controller interacts with the view layer to render the appropriate dashboard pages.
 *
 * Dependencies:
 * - Spring Framework: For handling web requests and responses.
 *
 * Security Considerations:
 * - Ensure that only authorized users can access their respective dashboards.
 */
@Controller // Marks this class as a Spring MVC controller
@RequestMapping("/dashboard") // Maps requests starting with /dashboard to this controller
public class DashboardController {

    /**
     * Displays the chef dashboard.
     *
     * @return the name of the view to render
     */
    @GetMapping("/chef-dashboard") // Maps GET requests to /dashboard/chef-dashboard to this method
    public String chefDashboard() {
        // Return the view name for the chef dashboard
        return "dashboard/chef-dashboard";
    }

    /**
     * Displays the waiter dashboard.
     *
     * @return the name of the view to render
     */
    @GetMapping("/waiter-dashboard") // Maps GET requests to /dashboard/waiter-dashboard to this method
    public String waiterDashboard() {
        // Return the view name for the waiter dashboard
        return "dashboard/waiter-dashboard";
    }

    /**
     * Displays the sous chef dashboard.
     *
     * @return the name of the view to render
     */
    @GetMapping("/souschef-dashboard") // Maps GET requests to /dashboard/souschef-dashboard to this method
    public String sousChefDashboard() {
        // Return the view name for the sous chef dashboard
        return "dashboard/souschef-dashboard";
    }
}