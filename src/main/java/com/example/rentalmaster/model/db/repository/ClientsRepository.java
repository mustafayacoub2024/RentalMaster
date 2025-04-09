package com.example.rentalmaster.model.db.repository;

import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientsRepository extends JpaRepository<Clients, UUID> {
    Optional<Clients> findByInn(String inn);

    Optional<Clients> findByInnAndStatus(String inn, Status status);

}
