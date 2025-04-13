package com.example.rentalmaster.model.db.repository;

import com.example.rentalmaster.model.db.entity.RentalOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RentalOrderRepository extends JpaRepository<RentalOrder, String> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM rentalOrder r JOIN r.techniques t " +
            "WHERE t.stateNumber = :stateNumber " +
            "AND ((r.startDate <= :endDate) AND (r.endDate >= :startDate))")
    boolean existsByTechniquesAndDateRange(
            @Param("stateNumber") String stateNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

//    boolean existsByTechniquesAndDateRange(String stateNumber, LocalDateTime startDate, LocalDateTime endDate);

    Optional<RentalOrder> findByRentalOrderId(String rentalOrderId);
}
