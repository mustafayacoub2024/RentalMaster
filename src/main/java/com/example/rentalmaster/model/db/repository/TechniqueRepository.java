package com.example.rentalmaster.model.db.repository;

import com.example.rentalmaster.model.db.entity.Technique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechniqueRepository extends JpaRepository<Technique, String> {
}
