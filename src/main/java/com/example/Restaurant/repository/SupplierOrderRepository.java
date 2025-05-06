package com.example.Restaurant.repository;

import com.example.Restaurant.model.SupplierOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupplierOrderRepository extends JpaRepository<SupplierOrder, Long> {
    List<SupplierOrder> findBySupplier_Id(Long supplierId);
    List<SupplierOrder> findByIngredient_Id(Long ingredientId);
    List<SupplierOrder> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}
