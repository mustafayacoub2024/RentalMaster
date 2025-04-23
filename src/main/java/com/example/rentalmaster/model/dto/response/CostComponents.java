package com.example.rentalmaster.model.dto.response;

public record CostComponents(
        double dailyTechCost,
        double dailyDriverCost,
        int days
) {}
