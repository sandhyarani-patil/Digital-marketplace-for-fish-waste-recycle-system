package com.fishwaste.repository;

import com.fishwaste.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findByEmail(String email);
    boolean existsByEmail(String email);
}
