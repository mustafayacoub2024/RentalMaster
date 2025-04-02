package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.db.repository.TechniqueRepository;
import com.example.rentalmaster.model.dto.request.TechniqueRequest;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;
import com.example.rentalmaster.service.TechniqueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TechniqueServiceImpl implements TechniqueService {

    private final TechniqueRepository techniqueRepository;
    private final ObjectMapper objectMapper;

    @Override
    public TechniqueResponse addTechnique(TechniqueRequest techniqueRequest) {
        techniqueRepository.findByStateNumber(techniqueRequest.getStateNumber())
                .ifPresent(technique -> {
                    throw new CommonBackendException(
                            "Техника с госномером " + techniqueRequest.getStateNumber() + " уже существует",
                            HttpStatus.CONFLICT
                    );
                });

        Technique technique = objectMapper.convertValue(techniqueRequest, Technique.class);
        techniqueRepository.save(technique);

        return TechniqueResponse.builder()
                .message("Техника с госномером " + techniqueRequest.getStateNumber() + " успешно добавлена")
                .build();
    }


    @Override
    public TechniqueResponse updateTechnique(String stateNumber, TechniqueRequest techniqueRequest) {
        Technique technique = techniqueRepository.findByStateNumber(techniqueRequest.getStateNumber()).
                orElseThrow(() -> new RuntimeException("Техника с госномером " +
                        techniqueRequest.getStateNumber() + " не найден"));

        technique.setTypeTechnique(techniqueRequest.getTypeTechnique());
        technique.setColor(techniqueRequest.getColor());
        technique.setWeight(techniqueRequest.getWeight());
        technique.setLoadCapacity(techniqueRequest.getLoadCapacity());
        technique.setYearOfProduction(techniqueRequest.getYearOfProduction());
        technique.setBaseCost(techniqueRequest.getBaseCost());
        technique.setAvailability(techniqueRequest.getAvailability());

        Technique updatedTechnique = techniqueRepository.save(technique);
        TechniqueResponse techniqueResponse = objectMapper.convertValue(updatedTechnique, TechniqueResponse.class);
        techniqueResponse.setMessage("Техника с госномером " + updatedTechnique.getStateNumber() + " успешно обновлено");
        return techniqueResponse;
    }

    @Override
    public TechniqueResponse deleteTechnique(String stateNumber) {
        Technique technique = techniqueRepository.findByStateNumber(stateNumber).
                orElseThrow(() -> new RuntimeException("Техника с госномером " +
                        stateNumber + " не найден"));

        techniqueRepository.delete(technique);

        TechniqueResponse techniqueResponse = objectMapper.convertValue(technique, TechniqueResponse.class);
        techniqueResponse.setMessage("Техника с госномером " + stateNumber + " успешно удаленно");

        return techniqueResponse;
    }
}
