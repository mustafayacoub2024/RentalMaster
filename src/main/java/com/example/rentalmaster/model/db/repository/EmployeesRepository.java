package com.example.rentalmaster.model.db.repository;

import com.example.rentalmaster.model.db.entity.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeesRepository extends JpaRepository<Employees, String> {
    Optional<Employees> findByPersonalNumber(String personalNumber);
}
