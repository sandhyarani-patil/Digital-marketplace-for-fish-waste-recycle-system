package com.fishwaste.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_requirement")
public class CompanyRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requirementId;
    private String wasteType;
    private Integer quantity;
    private String location;
    private BigDecimal budget;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Transient
    private String companyName;
    @Transient
    private Integer companyIdVal;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PostLoad
    public void fillTransient() {
        if (company != null) {
            this.companyName = company.getName();
            this.companyIdVal = company.getCompanyId();
        }
    }

    public Integer getRequirementId() { return requirementId; }
    public void setRequirementId(Integer requirementId) { this.requirementId = requirementId; }
    public String getWasteType() { return wasteType; }
    public void setWasteType(String wasteType) { this.wasteType = wasteType; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public Integer getCompanyIdVal() { return companyIdVal; }
    public void setCompanyIdVal(Integer companyIdVal) { this.companyIdVal = companyIdVal; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
}