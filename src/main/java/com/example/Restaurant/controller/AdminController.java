package com.example.Restaurant.controller;

import com.example.Restaurant.model.User;
import com.example.Restaurant.model.Role;
import com.example.Restaurant.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The AdminController class handles administrative operations for the application.
 * Main features include displaying the admin dashboard, listing staff members,
 * adding new staff, editing existing staff, and toggling staff status.
 *
 * Dependencies:
 * - UserService: Service for user-related operations.
 * - Spring Web and MVC: For handling web requests and responses.
 */
@Controller // Marks this class as a Spring MVC controller
@RequestMapping("/admin") // Maps requests starting with /admin to this controller
public class AdminController {

    @Autowired // Injects the UserService bean
    private UserService userService; // Service for user-related operations

    /**
     * Displays the main admin dashboard with staff statistics.
     *
     * @param model the Model object used to pass data to the view
     * @return the name of the view to render
     */
    @GetMapping("/dashboard") // Maps GET requests to /admin/dashboard to this method
    public String dashboard(Model model) {
        // Get the total number of users and subtract 1 to exclude the admin user
        model.addAttribute("staffCount", userService.getAllUsers().size() - 1);
        // Get the number of active users
        model.addAttribute("activeStaff", userService.getActiveUsers().size());
        // Return the view name for the admin dashboard
        return "admin/dashboard";
    }

    /**
     * Displays the list of all staff members.
     *
     * @param model the Model object used to pass data to the view
     * @return the name of the view to render
     */
    @GetMapping("/staff") // Maps GET requests to /admin/staff to this method
    public String staffList(Model model) {
        // Add the list of all users to the model
        model.addAttribute("users", userService.getAllUsers());
        // Add the list of all roles to the model
        model.addAttribute("roles", Role.values());
        // Return the view name for the staff list
        return "admin/staff-list";
    }

    /**
     * Displays the form for adding a new staff member.
     *
     * @param model the Model object used to pass data to the view
     * @return the name of the view to render
     */
    @GetMapping("/staff/add") // Maps GET requests to /admin/staff/add to this method
    public String showAddStaffForm(Model model) {
        // Add a new User object to the model
        model.addAttribute("user", new User());
        // Add the list of all roles to the model
        model.addAttribute("roles", Role.values());
        // Return the view name for the add staff form
        return "admin/staff-form";
    }

    /**
     * Handles the submission of the form for adding a new staff member.
     *
     * @param user the User object containing the new staff member's details
     * @param redirectAttributes the RedirectAttributes object used to pass flash attributes
     * @return the redirect URL
     */
    @PostMapping("/staff/add") // Maps POST requests to /admin/staff/add to this method
    public String addStaff(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            // Create a new user
            userService.createUser(user);
            // Add a success message to the redirect attributes
            redirectAttributes.addFlashAttribute("success", "Staff member added successfully!");
        } catch (Exception e) {
            // Add an error message to the redirect attributes
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        // Redirect to the staff list page
        return "redirect:/admin/staff";
    }

    /**
     * Displays the form for editing an existing staff member.
     *
     * @param id the ID of the staff member to edit
     * @param model the Model object used to pass data to the view
     * @return the name of the view to render
     */
    @GetMapping("/staff/edit/{id}") // Maps GET requests to /admin/staff/edit/{id} to this method
    public String showEditStaffForm(@PathVariable Long id, Model model) {
        // Get the user by ID and add it to the model if present
        userService.getUserById(id).ifPresent(user -> {
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
        });
        // Return the view name for the edit staff form
        return "admin/staff-form";
    }

    /**
     * Handles the submission of the form for editing an existing staff member.
     *
     * @param user the User object containing the updated staff member's details
     * @param redirectAttributes the RedirectAttributes object used to pass flash attributes
     * @return the redirect URL
     */
    @PostMapping("/staff/edit") // Maps POST requests to /admin/staff/edit to this method
    public String updateStaff(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            // Update the user
            userService.updateUser(user);
            // Add a success message to the redirect attributes
            redirectAttributes.addFlashAttribute("success", "Staff member updated successfully!");
        } catch (Exception e) {
            // Add an error message to the redirect attributes
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        // Redirect to the staff list page
        return "redirect:/admin/staff";
    }

    /**
     * Toggles the active status of a staff member.
     *
     * @param id the ID of the staff member to toggle
     * @return the redirect URL
     */
    @PostMapping("/staff/toggle/{id}") // Maps POST requests to /admin/staff/toggle/{id} to this method
    public String toggleStaffStatus(@PathVariable Long id) {
        // Get the user by ID or throw an exception if not found
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Toggle the active status of the user
        if (user.getIsActive()) {
            userService.deactivateUser(id);
        } else {
            userService.activateUser(id);
        }

        // Redirect to the staff list page
        return "redirect:/admin/staff";
    }
}