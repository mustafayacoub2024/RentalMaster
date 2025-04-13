package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.db.repository.TechniqueRepository;
import com.example.rentalmaster.model.dto.request.TechniqueRequest;
import com.example.rentalmaster.model.dto.request.TechniqueUpdateRequest;
import com.example.rentalmaster.model.dto.response.TechniqueInfoResponse;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;
import com.example.rentalmaster.model.enums.Availability;
import com.example.rentalmaster.service.TechniqueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

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
        technique.setAvailability(Availability.AVAILABLE);
        techniqueRepository.save(technique);

        return TechniqueResponse.builder()
                .message("Техника с госномером " + techniqueRequest.getStateNumber() + " успешно добавлена")
                .build();
    }


    @Override
    public TechniqueResponse updateTechnique(String stateNumber, TechniqueUpdateRequest techniqueRequest) {
        Technique technique = techniqueRepository.findByStateNumber(stateNumber).
                orElseThrow(() -> new CommonBackendException("Техника с госномером " +
                        stateNumber + " не найден", HttpStatus.NOT_FOUND));

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
                orElseThrow(() -> new CommonBackendException("Техника с госномером " +
                        stateNumber + " не найден", HttpStatus.NOT_FOUND));

        techniqueRepository.delete(technique);

        TechniqueResponse techniqueResponse = objectMapper.convertValue(technique, TechniqueResponse.class);
        techniqueResponse.setMessage("Техника с госномером " + stateNumber + " успешно удаленно");

        return techniqueResponse;
    }

    @Override
    public TechniqueInfoResponse getTechnique(String stateNumber) {
        Technique technique = techniqueRepository.findByStateNumber(stateNumber).
                orElseThrow(() -> new CommonBackendException("Техника с госномером " +
                        stateNumber + " не найден", HttpStatus.NOT_FOUND));

       return TechniqueInfoResponse.builder()
                .message("Данные об технике с госномер "+ stateNumber)
                .typeTechnique(technique.getTypeTechnique())
                .color(technique.getColor())
                .weight(technique.getWeight())
                .loadCapacity(technique.getLoadCapacity())
                .yearOfProduction(technique.getYearOfProduction())
                .baseCost(technique.getBaseCost())
                .stateNumber(technique.getStateNumber())
               .availability(technique.getAvailability())
                .build();
    }

    @Override
    public List<TechniqueResponse> getAllTechnique() {
        List<Technique> techniqueList = techniqueRepository.findAll();

        if (techniqueList.isEmpty()) {
            throw new CommonBackendException("Список технике пуст", HttpStatus.NOT_FOUND);
        }
        return techniqueList.stream()
                .map(technique -> {
                    TechniqueResponse techniqueResponse = objectMapper.convertValue(technique, TechniqueResponse.class);
                    techniqueResponse.setMessage(technique.getTypeTechnique()+ " ,госномер" + technique.getStateNumber());
                    return techniqueResponse;
                }).toList();
    }

    @Override
    public List<Technique> getAll() {
        return techniqueRepository.findAll();
    }
}
