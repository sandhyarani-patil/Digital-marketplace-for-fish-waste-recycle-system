package com.fishwaste.controller;

import com.fishwaste.entity.*;
import com.fishwaste.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/company")
@CrossOrigin(origins = "*")
public class CompanyController {

    @Autowired private CompanyRepository companyRepo;
    @Autowired private CompanyRequirementRepository requirementRepo;
    @Autowired private WasteDetailRepository wasteRepo;
    @Autowired private OrderRepository orderRepo;

    // ── GET ALL AVAILABLE WASTE FOR COMPANY TO BROWSE ──
    @GetMapping("/waste")
    public ResponseEntity<?> allWaste() {
        try {
            List<WasteDetail> list = wasteRepo.findByStatus("AVAILABLE");
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── POST A REQUIREMENT ──
    @PostMapping("/requirement")
    public ResponseEntity<?> addRequirement(
            @RequestBody CompanyRequirement req,
            Authentication auth) {
        try {
            Company company = companyRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            req.setCompany(company);
            CompanyRequirement saved = requirementRepo.save(req);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── GET MY REQUIREMENTS ──
    @GetMapping("/requirement")
    public ResponseEntity<?> myRequirements(Authentication auth) {
        try {
            Company company = companyRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            return ResponseEntity.ok(
                requirementRepo.findByCompany_CompanyId(company.getCompanyId())
            );
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── DELETE A REQUIREMENT ──
    @DeleteMapping("/requirement/{id}")
    public ResponseEntity<?> deleteRequirement(@PathVariable Integer id) {
        try {
            requirementRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Requirement deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── AUTO MATCH REQUIREMENTS WITH AVAILABLE WASTE ──
    @GetMapping("/matches")
    public ResponseEntity<?> getMatches(Authentication auth) {
        try {
            Company company = companyRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            List<CompanyRequirement> requirements =
                requirementRepo.findByCompany_CompanyId(company.getCompanyId());

            List<Map<String, Object>> result = new ArrayList<>();

            for (CompanyRequirement req : requirements) {
                // Get ALL available waste
                List<WasteDetail> allWaste = wasteRepo.findByStatus("AVAILABLE");

                // Match by waste type (case-insensitive partial match)
                List<WasteDetail> matched = new ArrayList<>();
                for (WasteDetail w : allWaste) {
                    boolean typeMatch = w.getWasteType()
                        .toLowerCase()
                        .contains(req.getWasteType().toLowerCase())
                        ||
                        req.getWasteType()
                        .toLowerCase()
                        .contains(w.getWasteType().toLowerCase());

                    boolean locationMatch = true;
                    if (req.getLocation() != null && !req.getLocation().isEmpty()
                        && w.getLocation() != null && !w.getLocation().isEmpty()) {
                        locationMatch = w.getLocation()
                            .toLowerCase()
                            .contains(req.getLocation().toLowerCase())
                            ||
                            req.getLocation()
                            .toLowerCase()
                            .contains(w.getLocation().toLowerCase());
                    }

                    if (typeMatch && locationMatch) {
                        matched.add(w);
                    }
                }

                Map<String, Object> entry = new HashMap<>();
                entry.put("requirement", req);
                entry.put("matches", matched);
                entry.put("matchCount", matched.size());
                result.add(entry);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── PLACE AN ORDER ──
    @PostMapping("/order")
    public ResponseEntity<?> placeOrder(
            @RequestBody Map<String, Integer> body,
            Authentication auth) {
        try {
            Company company = companyRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            Integer wasteId = body.get("wasteId");
            if (wasteId == null)
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "wasteId is required"));

            WasteDetail waste = wasteRepo.findById(wasteId)
                    .orElseThrow(() -> new RuntimeException("Waste not found"));

            if (!"AVAILABLE".equals(waste.getStatus()))
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "This waste is no longer available"));

            Order order = new Order();
            order.setCompany(company);
            order.setWasteDetail(waste);
            order.setStatus("PENDING");

            // Link to requirement if provided
            Integer requirementId = body.get("requirementId");
            if (requirementId != null) {
                requirementRepo.findById(requirementId)
                        .ifPresent(order::setRequirement);
            }

            // Mark waste as RESERVED
            waste.setStatus("RESERVED");
            wasteRepo.save(waste);

            Order saved = orderRepo.save(order);
            return ResponseEntity.ok(Map.of(
                "message", "Order placed successfully",
                "orderId", saved.getOrderId(),
                "status",  saved.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── GET MY ORDERS ──
    @GetMapping("/order")
    public ResponseEntity<?> myOrders(Authentication auth) {
        try {
            Company company = companyRepo.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            return ResponseEntity.ok(
                orderRepo.findByCompany_CompanyId(company.getCompanyId())
            );
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}