package com.fishwaste.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waste_id")
    private WasteDetail wasteDetail;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id")
    private CompanyRequirement requirement;

    @Transient private Integer wasteId;
    @Transient private String wasteType;
    @Transient private Integer quantity;
    @Transient private String companyName;
    @Transient private Integer companyIdVal;

    private String status = "PENDING";
    private LocalDate requestDate = LocalDate.now();

    @PostLoad
    public void fillTransient() {
        if (wasteDetail != null) {
            this.wasteId = wasteDetail.getWasteId();
            this.wasteType = wasteDetail.getWasteType();
            this.quantity = wasteDetail.getQuantity();
        }
        if (company != null) {
            this.companyName = company.getName();
            this.companyIdVal = company.getCompanyId();
        }
    }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public WasteDetail getWasteDetail() { return wasteDetail; }
    public void setWasteDetail(WasteDetail wasteDetail) { this.wasteDetail = wasteDetail; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public CompanyRequirement getRequirement() { return requirement; }
    public void setRequirement(CompanyRequirement requirement) { this.requirement = requirement; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
    public Integer getWasteId() { return wasteId; }
    public void setWasteId(Integer wasteId) { this.wasteId = wasteId; }
    public String getWasteType() { return wasteType; }
    public void setWasteType(String wasteType) { this.wasteType = wasteType; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public Integer getCompanyIdVal() { return companyIdVal; }
    public void setCompanyIdVal(Integer companyIdVal) { this.companyIdVal = companyIdVal; }
}