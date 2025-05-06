package com.example.Restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The LoginController class handles the login operations for the restaurant application.
 *
 * Main Responsibilities:
 * - Display the login page.
 *
 * Component Relationships:
 * - This controller interacts with the view layer to render the login page.
 *
 * Dependencies:
 * - Spring Framework: For handling web requests and responses.
 *
 * Security Considerations:
 * - Ensure that the login page is accessible only to unauthenticated users.
 */
@Controller
public class LoginController {

    /**
     * Displays the login page.
     *
     * @return the name of the view to render
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";  // Refers to login.html in the templates folder
    }
}