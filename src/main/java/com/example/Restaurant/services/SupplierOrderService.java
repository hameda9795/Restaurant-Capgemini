package com.example.Restaurant.services;

import com.example.Restaurant.model.Supplier;
import com.example.Restaurant.model.SupplierOrder;
import com.example.Restaurant.repository.SupplierOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SupplierOrderService {

    @Autowired
    private SupplierOrderRepository supplierOrderRepository;


    @Transactional
    public SupplierOrder saveOrder(SupplierOrder order) {
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        return supplierOrderRepository.save(order);
    }


    public List<SupplierOrder> getAllOrders() {
        return supplierOrderRepository.findAll();
    }


    public List<SupplierOrder> getOrdersBySupplier(Long supplierId) {
        return supplierOrderRepository.findBySupplier_Id(supplierId);
    }


    public Map<Supplier, List<SupplierOrder>> getOrdersGroupedBySupplier() {
        List<SupplierOrder> allOrders = getAllOrders();
        return allOrders.stream()
                .collect(Collectors.groupingBy(SupplierOrder::getSupplier));
    }


    public List<SupplierOrder> getOrdersByIngredient(Long ingredientId) {
        return supplierOrderRepository.findByIngredient_Id(ingredientId);
    }


    @Transactional
    public void deleteOrder(Long orderId) {
        supplierOrderRepository.deleteById(orderId);
    }

    public List<SupplierOrder> getOrdersByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        return supplierOrderRepository.findByOrderDateBetween(fromDate, toDate);
    }
}