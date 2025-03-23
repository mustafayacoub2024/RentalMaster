package com.example.rentalmaster.service;

import com.example.rentalmaster.model.dto.request.TechniqueRequest;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;

public interface TechniqueService {
    TechniqueResponse addTechnique(TechniqueRequest techniqueRequest);

    TechniqueResponse updateTechnique(String stateNumber, TechniqueRequest techniqueRequest);

    TechniqueResponse deleteTechnique(String stateNumber);
}
