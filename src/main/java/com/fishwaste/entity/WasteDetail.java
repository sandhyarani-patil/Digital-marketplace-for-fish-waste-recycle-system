package com.fishwaste.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste_detail")
public class WasteDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wasteId;

    private String wasteType;
    private Integer quantity;

    @Column(columnDefinition = "TEXT")
    private String sellerDetail;

    private String location;
    private String status = "AVAILABLE";

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Seller seller;

    @Transient
    private String sellerName;

    @Transient
    private Integer sellerId;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PostLoad
    public void fillTransient() {
        if (seller != null) {
            this.sellerName = seller.getName();
            this.sellerId = seller.getUserId();
        }
    }

    public Integer getWasteId() { return wasteId; }
    public void setWasteId(Integer wasteId) { this.wasteId = wasteId; }

    public String getWasteType() { return wasteType; }
    public void setWasteType(String wasteType) { this.wasteType = wasteType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getSellerDetail() { return sellerDetail; }
    public void setSellerDetail(String sellerDetail) { this.sellerDetail = sellerDetail; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public Integer getSellerId() { return sellerId; }
    public void setSellerId(Integer sellerId) { this.sellerId = sellerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}