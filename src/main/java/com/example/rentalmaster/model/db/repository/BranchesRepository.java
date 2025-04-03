package com.example.rentalmaster.model.db.repository;

import com.example.rentalmaster.model.db.entity.Branches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchesRepository extends JpaRepository<Branches, String> {
    Optional<Branches> findByBranchName(String branchName);
}
