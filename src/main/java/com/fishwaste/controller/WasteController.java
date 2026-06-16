package com.fishwaste.controller;

import com.fishwaste.entity.Seller;
import com.fishwaste.entity.WasteDetail;
import com.fishwaste.repository.SellerRepository;
import com.fishwaste.repository.WasteDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WasteController {

    @Autowired private WasteDetailRepository wasteRepo;
    @Autowired private SellerRepository sellerRepo;

    // ── PUBLIC ──────────────────────────────────────────
    @GetMapping("/waste/available")
    public ResponseEntity<?> getAvailable() {
        return ResponseEntity.ok(wasteRepo.findByStatus("AVAILABLE"));
    }

    @GetMapping("/waste/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String wasteType,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(
            wasteRepo.searchAvailable(wasteType, location));
    }

    // ── SELLER: GET MY LISTINGS ──────────────────────────
    @GetMapping("/seller/waste")
    public ResponseEntity<?> myListings() {
        try {
            Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()
                    || "anonymousUser".equals(auth.getName())) {
                return ResponseEntity.status(401)
                    .body(Map.of("message", "Please login again"));
            }
            String email = auth.getName();
            System.out.println("=== GET LISTINGS for: " + email);
            Seller seller = sellerRepo.findByEmail(email).orElse(null);
            if (seller == null) {
                System.out.println("=== SELLER NOT FOUND for email: " + email);
                return ResponseEntity.ok(java.util.Collections.emptyList());
            }
            return ResponseEntity.ok(
                wasteRepo.findBySeller_UserId(seller.getUserId()));
        } catch (Exception e) {
            System.out.println("=== ERROR: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }

    // ── SELLER: ADD LISTING ──────────────────────────────
    @PostMapping("/seller/waste")
    public ResponseEntity<?> addWaste(
            @RequestBody Map<String, Object> body) {
        try {
            Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()
                    || "anonymousUser".equals(auth.getName())) {
                return ResponseEntity.status(401)
                    .body(Map.of("message", "Please login again"));
            }

            String email = auth.getName();
            System.out.println("=== ADD WASTE by: " + email);
            System.out.println("=== BODY: " + body);

            Seller seller = sellerRepo.findByEmail(email).orElse(null);
            if (seller == null) {
                return ResponseEntity.status(404)
                    .body(Map.of("message",
                        "Seller account not found. Please register again."));
            }

            WasteDetail waste = new WasteDetail();
            waste.setSeller(seller);
            waste.setStatus("AVAILABLE");

            Object wt = body.get("wasteType");
            Object qty = body.get("quantity");
            Object loc = body.get("location");
            Object det = body.get("sellerDetail");

            if (wt == null || wt.toString().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Waste type is required"));
            }
            if (qty == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Quantity is required"));
            }

            waste.setWasteType(wt.toString().trim());
            waste.setQuantity(Integer.parseInt(qty.toString()));
            if (loc != null) waste.setLocation(loc.toString().trim());
            if (det != null) waste.setSellerDetail(det.toString().trim());

            WasteDetail saved = wasteRepo.save(waste);
            System.out.println("=== SAVED waste id: " + saved.getWasteId());
            return ResponseEntity.ok(saved);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Quantity must be a valid number"));
        } catch (Exception e) {
            System.out.println("=== ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    // ── SELLER: UPDATE LISTING ───────────────────────────
    @PutMapping("/seller/waste/{id}")
    public ResponseEntity<?> updateWaste(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {
        try {
            WasteDetail existing = wasteRepo.findById(id).orElse(null);
            if (existing == null)
                return ResponseEntity.status(404)
                    .body(Map.of("message", "Listing not found"));

            if (body.get("wasteType") != null)
                existing.setWasteType(body.get("wasteType").toString());
            if (body.get("quantity") != null)
                existing.setQuantity(
                    Integer.parseInt(body.get("quantity").toString()));
            if (body.get("location") != null)
                existing.setLocation(body.get("location").toString());
            if (body.get("sellerDetail") != null)
                existing.setSellerDetail(body.get("sellerDetail").toString());

            return ResponseEntity.ok(wasteRepo.save(existing));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }

    // ── SELLER: DELETE LISTING ───────────────────────────
    @DeleteMapping("/seller/waste/{id}")
    public ResponseEntity<?> deleteWaste(@PathVariable Integer id) {
        try {
            wasteRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", e.getMessage()));
        }
    }
}