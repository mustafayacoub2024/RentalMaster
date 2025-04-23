package com.example.rentalmaster.model.dto.response;

import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.model.db.entity.Technique;

import java.util.List;

public record RentalOrderEntities(
        List<Drivers> drivers,
        List<Technique> techniques,
        Employees employee,
        Clients client,
        Branches branch
){}