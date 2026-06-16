package com.fishwaste.controller;

import com.fishwaste.entity.*;
import com.fishwaste.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private SellerRepository      sellerRepo;
    @Autowired private CompanyRepository     companyRepo;
    @Autowired private WasteDetailRepository wasteRepo;
    @Autowired private OrderRepository       orderRepo;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSellers",   sellerRepo.count());
            stats.put("totalCompanies", companyRepo.count());
            stats.put("totalListings",  wasteRepo.count());
            stats.put("totalOrders",    orderRepo.count());
            stats.put("pendingOrders",
                orderRepo.findByStatus("PENDING").size());
            stats.put("availableWaste",
                wasteRepo.findByStatus("AVAILABLE").size());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/sellers")
    public ResponseEntity<?> allSellers() {
        try {
            return ResponseEntity.ok(sellerRepo.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/sellers/{id}")
    public ResponseEntity<?> deleteSeller(@PathVariable Integer id) {
        try {
            sellerRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Seller deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/companies")
    public ResponseEntity<?> allCompanies() {
        try {
            return ResponseEntity.ok(companyRepo.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Integer id) {
        try {
            companyRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Company deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/waste")
    public ResponseEntity<?> allWaste() {
        try {
            return ResponseEntity.ok(wasteRepo.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/waste/{id}")
    public ResponseEntity<?> deleteWaste(@PathVariable Integer id) {
        try {
            wasteRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Listing deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> allOrders() {
        try {
            return ResponseEntity.ok(orderRepo.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        try {
            Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
            order.setStatus(body.get("status"));
            if ("APPROVED".equals(body.get("status"))
                    && order.getWasteDetail() != null) {
                order.getWasteDetail().setStatus("SOLD");
                wasteRepo.save(order.getWasteDetail());
            }
            return ResponseEntity.ok(orderRepo.save(order));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }
}