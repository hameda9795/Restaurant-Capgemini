package com.example.Restaurant.controller;

import com.example.Restaurant.model.SupplierOrder;
import com.example.Restaurant.services.SupplierOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/order-history")
public class OrderHistoryController {

    @Autowired
    private SupplierOrderService supplierOrderService;

    @GetMapping
    public String getOrderHistory(Model model) {
        List<SupplierOrder> orders = supplierOrderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "order-history";
    }

    @GetMapping("/filter")
    public String filterOrders(
            @RequestParam String fromDate,
            @RequestParam String toDate,
            Model model) {

        LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
        LocalDateTime to = LocalDate.parse(toDate).atTime(23, 59, 59);

        List<SupplierOrder> orders = supplierOrderService.getOrdersByDateRange(from, to);
        model.addAttribute("orders", orders);

        return "order-history";
    }
}