package com.example.Restaurant.services;


import com.example.Restaurant.model.Supplier;
import com.example.Restaurant.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Optional<Supplier> updateSupplier(Long id, Supplier updatedSupplier) {
        return supplierRepository.findById(id).map(existingSupplier -> {
            existingSupplier.setName(updatedSupplier.getName());
            existingSupplier.setContact(updatedSupplier.getContact());
            existingSupplier.setEmail(updatedSupplier.getEmail());
            existingSupplier.setAddress(updatedSupplier.getAddress());
            return supplierRepository.save(existingSupplier);
        });
    }

    public boolean deleteSupplier(Long id) {
        if (supplierRepository.existsById(id)) {
            supplierRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
