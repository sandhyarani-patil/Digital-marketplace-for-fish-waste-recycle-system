package com.fishwaste.repository;

import com.fishwaste.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCompany_CompanyId(Integer companyId);
    List<Order> findByWasteDetail_Seller_UserId(Integer sellerId);
    List<Order> findByStatus(String status);
}
