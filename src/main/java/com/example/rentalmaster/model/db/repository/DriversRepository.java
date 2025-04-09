package com.example.rentalmaster.model.db.repository;

import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.entity.Technique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DriversRepository extends JpaRepository<Drivers, Long> {
    Optional<Drivers> findByPersonalNumber(String personalNumber);

    List<Drivers> findAllByPersonalNumberIn(List<String> personalNumbers);

}
