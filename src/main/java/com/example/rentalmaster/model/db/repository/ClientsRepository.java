package com.example.rentalmaster.model.db.repository;

import com.example.rentalmaster.model.db.entity.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientsRepository extends JpaRepository<Clients, UUID> {
    Optional<Clients> findByInn(String inn);
}
