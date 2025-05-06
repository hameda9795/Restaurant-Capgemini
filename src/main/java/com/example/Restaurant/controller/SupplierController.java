package com.example.Restaurant.controller;

import com.example.Restaurant.model.Supplier;
import com.example.Restaurant.services.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public String getSuppliersPage(Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers();

        // Log the supplier list to verify data is being fetched
        System.out.println("Suppliers List: " + suppliers);

        model.addAttribute("suppliers", suppliers);
        return "suppliers"; // Ensures "suppliers.html" gets the data
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Supplier getSupplierForUpdate(@PathVariable Long id) {
        return supplierService.getSupplierById(id).orElse(null);
    }

    @PostMapping
    public String createSupplier(@ModelAttribute Supplier supplier, Model model) {
        if (supplier.getName() == null || supplier.getName().isEmpty()) {
            model.addAttribute("error", "Supplier name cannot be empty");
            return "suppliers";
        }
        supplierService.createSupplier(supplier);
        return "redirect:/suppliers";
    }

    @PostMapping("/delete/{id}")
    public String deleteSupplierViaPost(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return "redirect:/suppliers";
    }
}
