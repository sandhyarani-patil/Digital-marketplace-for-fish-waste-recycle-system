package com.fishwaste.repository;

import com.fishwaste.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Integer> {
    Optional<Seller> findByEmail(String email);
    boolean existsByEmail(String email);
}
