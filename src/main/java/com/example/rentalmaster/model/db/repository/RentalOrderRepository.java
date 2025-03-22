package com.example.rentalmaster.model.db.repository;

import com.example.rentalmaster.model.db.entity.RentalOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RentalOrderRepository extends JpaRepository<RentalOrder, UUID> {
}
