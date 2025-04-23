package com.example.rentalmaster.service;

import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.dto.request.TechniqueRequest;
import com.example.rentalmaster.model.dto.request.TechniqueUpdateRequest;
import com.example.rentalmaster.model.dto.response.TechniqueInfoResponse;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;

import java.util.List;

public interface TechniqueService {
    TechniqueResponse addTechnique(TechniqueRequest techniqueRequest);

    TechniqueResponse updateTechnique(String stateNumber, TechniqueUpdateRequest techniqueRequest);

    TechniqueResponse deleteTechnique(String stateNumber);

    TechniqueInfoResponse getTechnique(String stateNumber);

    List<TechniqueResponse> getAllTechnique();

    List<Technique> getAll();
}