package com.example.rentalmaster.model.db.repository;

import com.example.rentalmaster.model.db.entity.Drivers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DriversRepository extends JpaRepository<Drivers, Long> {
    Optional<Drivers> findByPersonalNumber(String personalNumber);
}
