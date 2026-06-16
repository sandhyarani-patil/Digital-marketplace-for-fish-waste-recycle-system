package com.fishwaste.repository;

import com.fishwaste.entity.CompanyRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompanyRequirementRepository extends JpaRepository<CompanyRequirement, Integer> {
    List<CompanyRequirement> findByCompany_CompanyId(Integer companyId);
}
