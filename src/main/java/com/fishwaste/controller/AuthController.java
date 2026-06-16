package com.fishwaste.controller;

import com.fishwaste.entity.Admin;
import com.fishwaste.entity.Company;
import com.fishwaste.entity.Seller;
import com.fishwaste.repository.AdminRepository;
import com.fishwaste.repository.CompanyRepository;
import com.fishwaste.repository.SellerRepository;
import com.fishwaste.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private SellerRepository  sellerRepo;
    @Autowired private CompanyRepository companyRepo;
    @Autowired private AdminRepository   adminRepo;
    @Autowired private PasswordEncoder   passwordEncoder;
    @Autowired private JwtUtils          jwtUtils;

    static class LoginRequest {
        public String email;
        public String password;
        public String role;
        public String getEmail()    { return email; }
        public void setEmail(String e) { this.email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
        public String getRole()     { return role; }
        public void setRole(String r) { this.role = r; }
    }

    static class RegisterSellerRequest {
        public String name, email, password, phone, address;
        public String getName()     { return name; }
        public void setName(String n) { this.name = n; }
        public String getEmail()    { return email; }
        public void setEmail(String e) { this.email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
        public String getPhone()    { return phone; }
        public void setPhone(String p) { this.phone = p; }
        public String getAddress()  { return address; }
        public void setAddress(String a) { this.address = a; }
    }

    static class RegisterCompanyRequest {
        public String name, email, password, phone;
        public String getName()     { return name; }
        public void setName(String n) { this.name = n; }
        public String getEmail()    { return email; }
        public void setEmail(String e) { this.email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
        public String getPhone()    { return phone; }
        public void setPhone(String p) { this.phone = p; }
    }

    // ── TEST ADMIN ──────────────────────────────────────
    @GetMapping("/test-admin")
    public ResponseEntity<?> testAdmin() {
        try {
            Optional<Admin> a = adminRepo.findByEmail("admin@fishwaste.com");
            if (a.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "found",   false,
                    "message", "Admin NOT found in database"
                ));
            }
            String hash  = a.get().getPassword();
            boolean m123 = passwordEncoder.matches("admin123", hash);
            boolean mpass = passwordEncoder.matches("password", hash);
            return ResponseEntity.ok(Map.of(
                "found",          true,
                "email",          a.get().getEmail(),
                "name",           a.get().getName(),
                "hashLength",     hash != null ? hash.length() : 0,
                "match_admin123", m123,
                "match_password", mpass,
                "USE_PASSWORD",   m123 ? "admin123" :
                                  mpass ? "password" : "UNKNOWN"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ── RESET ADMIN ─────────────────────────────────────
    @GetMapping("/reset-admin")
    public ResponseEntity<?> resetAdmin() {
        try {
            Optional<Admin> existing =
                adminRepo.findByEmail("admin@fishwaste.com");
            if (existing.isPresent()) {
                adminRepo.delete(existing.get());
                System.out.println("=== Old admin deleted");
            }
            Admin admin = new Admin();
            admin.setName("Admin");
            admin.setEmail("admin@fishwaste.com");
            String hash = passwordEncoder.encode("admin123");
            admin.setPassword(hash);
            adminRepo.save(admin);
            System.out.println("=== New admin saved with hash: " + hash);
            return ResponseEntity.ok(Map.of(
                "status",   "SUCCESS",
                "email",    "admin@fishwaste.com",
                "password", "admin123",
                "message",  "Admin reset! Now login with admin123"
            ));
        } catch (Exception e) {
            System.out.println("=== Reset error: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ── LOGIN ────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String role = req.getRole().toUpperCase();
        Map<String, Object> res = new HashMap<>();
        try {
            switch (role) {
                case "SELLER" -> {
                    Optional<Seller> s =
                        sellerRepo.findByEmail(req.getEmail());
                    if (s.isEmpty() || !passwordEncoder.matches(
                            req.getPassword(), s.get().getPassword()))
                        return ResponseEntity.status(401)
                            .body(Map.of("message", "Invalid credentials"));
                    String token = jwtUtils.generateToken(
                        req.getEmail(), "SELLER");
                    res.put("token", token);
                    res.put("role",  "SELLER");
                    res.put("id",    s.get().getUserId());
                    res.put("name",  s.get().getName());
                }
                case "COMPANY" -> {
                    Optional<Company> c =
                        companyRepo.findByEmail(req.getEmail());
                    if (c.isEmpty() || !passwordEncoder.matches(
                            req.getPassword(), c.get().getPassword()))
                        return ResponseEntity.status(401)
                            .body(Map.of("message", "Invalid credentials"));
                    String token = jwtUtils.generateToken(
                        req.getEmail(), "COMPANY");
                    res.put("token", token);
                    res.put("role",  "COMPANY");
                    res.put("id",    c.get().getCompanyId());
                    res.put("name",  c.get().getName());
                }
                case "ADMIN" -> {
                    Optional<Admin> a =
                        adminRepo.findByEmail(req.getEmail());
                    if (a.isEmpty()) {
                        System.out.println("=== ADMIN NOT FOUND: "
                            + req.getEmail());
                        return ResponseEntity.status(401)
                            .body(Map.of("message", "Invalid credentials"));
                    }
                    System.out.println("=== ADMIN FOUND: "
                        + a.get().getEmail());
                    System.out.println("=== STORED HASH: "
                        + a.get().getPassword());
                    boolean match = passwordEncoder.matches(
                        req.getPassword(), a.get().getPassword());
                    System.out.println("=== PASSWORD MATCH: " + match);
                    if (!match)
                        return ResponseEntity.status(401)
                            .body(Map.of("message", "Invalid credentials"));
                    String token = jwtUtils.generateToken(
                        req.getEmail(), "ADMIN");
                    res.put("token", token);
                    res.put("role",  "ADMIN");
                    res.put("id",    a.get().getEmailId());
                    res.put("name",  a.get().getName());
                }
                default -> {
                    return ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid role"));
                }
            }
        } catch (Exception e) {
            System.out.println("=== LOGIN ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("message", "Server error: " + e.getMessage()));
        }
        return ResponseEntity.ok(res);
    }

    // ── REGISTER SELLER ──────────────────────────────────
    @PostMapping("/register/seller")
    public ResponseEntity<?> registerSeller(
            @RequestBody RegisterSellerRequest req) {
        try {
            if (sellerRepo.existsByEmail(req.getEmail()))
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email already registered"));
            Seller seller = new Seller();
            seller.setName(req.getName());
            seller.setEmail(req.getEmail());
            seller.setPassword(passwordEncoder.encode(req.getPassword()));
            seller.setPhone(req.getPhone());
            seller.setAddress(req.getAddress());
            sellerRepo.save(seller);
            return ResponseEntity.ok(
                Map.of("message", "Seller registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // ── REGISTER COMPANY ─────────────────────────────────
    @PostMapping("/register/company")
    public ResponseEntity<?> registerCompany(
            @RequestBody RegisterCompanyRequest req) {
        try {
            if (companyRepo.existsByEmail(req.getEmail()))
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email already registered"));
            Company company = new Company();
            company.setName(req.getName());
            company.setEmail(req.getEmail());
            company.setPassword(passwordEncoder.encode(req.getPassword()));
            company.setPhone(req.getPhone());
            companyRepo.save(company);
            return ResponseEntity.ok(
                Map.of("message", "Company registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}