package com.example.Restaurant.controller;



import com.example.Restaurant.model.Supplier;
import com.example.Restaurant.model.SupplierOrder;
import com.example.Restaurant.services.SupplierOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/supplier-orders")
public class SupplierOrderController {

    @Autowired
    private SupplierOrderService supplierOrderService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addOrder(@RequestBody SupplierOrder order) {
        supplierOrderService.saveOrder(order);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public String getOrdersPage(Model model) {
        Map<Supplier, List<SupplierOrder>> ordersBySupplier =
                supplierOrderService.getOrdersGroupedBySupplier();
        model.addAttribute("ordersBySupplier", ordersBySupplier);
        return "supplier-orders";
    }

    @GetMapping("/order-history")
    public String getOrderHistory(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        List<SupplierOrder> orders;
        if (fromDate != null && toDate != null) {
            LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
            LocalDateTime to = LocalDate.parse(toDate).atTime(23, 59, 59);
            orders = supplierOrderService.getOrdersByDateRange(from, to);
        } else {
            orders = supplierOrderService.getAllOrders();
        }

        model.addAttribute("orders", orders);
        return "order-history";
    }
}