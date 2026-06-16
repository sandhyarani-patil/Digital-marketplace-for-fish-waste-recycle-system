package com.fishwaste.repository;

import com.fishwaste.entity.WasteDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface WasteDetailRepository extends JpaRepository<WasteDetail, Integer> {

    List<WasteDetail> findBySeller_UserId(Integer sellerId);

    List<WasteDetail> findByStatus(String status);

    @Query("SELECT w FROM WasteDetail w WHERE w.status = 'AVAILABLE' AND " +
           "(:wasteType IS NULL OR LOWER(w.wasteType) LIKE LOWER(CONCAT('%',:wasteType,'%'))) AND " +
           "(:location  IS NULL OR LOWER(w.location)  LIKE LOWER(CONCAT('%',:location, '%')))")
    List<WasteDetail> searchAvailable(
        @Param("wasteType") String wasteType,
        @Param("location")  String location
    );
}