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
        log.info("Создание карточки на технику с госномером:{}", techniqueRequest.getStateNumber());
        validateTechniqueNotExists(techniqueRequest.getStateNumber());
        Technique technique = objectMapper.convertValue(techniqueRequest, Technique.class);
        technique.setAvailability(Availability.AVAILABLE);
        log.info("Вновь созданная техника с госномерм:{} имеет статус:{}", techniqueRequest.getStateNumber(), technique.getAvailability());
        techniqueRepository.save(technique);
        log.info("Техника с госномером:{} сохранена в бд", techniqueRequest.getStateNumber());
        return TechniqueResponse.builder()
                .message("Техника с госномером " + techniqueRequest.getStateNumber() + " успешно добавлена")
                .build();
    }


    @Override
    public TechniqueResponse updateTechnique(String stateNumber, TechniqueUpdateRequest techniqueRequest) {
        log.info("Обновление данных технике с госномером:{}", stateNumber);
        Technique technique = validateTechniqueNoFound(stateNumber);
        updateTechniqueInfo(technique, techniqueRequest);
        Technique updatedTechnique = techniqueRepository.save(technique);
        TechniqueResponse techniqueResponse = objectMapper.convertValue(updatedTechnique, TechniqueResponse.class);
        techniqueResponse.setMessage("Техника с госномером " + updatedTechnique.getStateNumber() + " успешно обновлено");
        log.info("Обновлены данные технике с госномером:{} ", stateNumber);
        return techniqueResponse;
    }

    @Override
    public TechniqueResponse deleteTechnique(String stateNumber) {
        log.info("Удаление технике с госномером:{}", stateNumber);
        Technique technique = validateTechniqueNoFound(stateNumber);
        techniqueRepository.delete(technique);
        TechniqueResponse techniqueResponse = objectMapper.convertValue(technique, TechniqueResponse.class);
        techniqueResponse.setMessage("Техника с госномером " + stateNumber + " успешно удаленно");
        log.info("Технике с госномером:{} удалена", stateNumber);
        return techniqueResponse;
    }

    @Override
    public TechniqueInfoResponse getTechnique(String stateNumber) {
        log.info("Запрос данных о технике:{}", stateNumber);
        Technique technique = validateTechniqueNoFound(stateNumber);
        log.info("Запрос данных о технике:{} успешно выполнен", stateNumber);
        return buildTechniqueResponse(technique,stateNumber);
    }

    @Override
    public List<TechniqueResponse> getAllTechnique() {
        log.info("Запрос списка технике");
        List<Technique> techniqueList = techniqueRepository.findAll();
        log.info("Количество найденной технике:{}", techniqueList.size());
        validateTechniqueNoEmpty(techniqueList);
        return techniqueList.stream()
                .map(technique -> {
                    TechniqueResponse techniqueResponse = objectMapper.convertValue(technique, TechniqueResponse.class);
                    techniqueResponse.setMessage(technique.getTypeTechnique() + " ,госномер" + technique.getStateNumber());
                    return techniqueResponse;
                }).toList();
    }

    @Override
    public List<Technique> getAll() {
        return techniqueRepository.findAll();
    }

    private void validateTechniqueNotExists(String stateNumber) {
        techniqueRepository.findByStateNumber(stateNumber)
                .ifPresent(technique -> {
                    throw new CommonBackendException(
                            "Техника с госномером " + stateNumber + " уже существует",
                            HttpStatus.CONFLICT
                    );
                });
    }

    private Technique validateTechniqueNoFound(String stateNumber){
        return techniqueRepository.findByStateNumber(stateNumber).
                orElseThrow(() -> new CommonBackendException("Техника с госномером " +
                        stateNumber + " не найден", HttpStatus.NOT_FOUND));
    }

    public void updateTechniqueInfo(Technique technique, TechniqueUpdateRequest techniqueRequest){
        technique.setTypeTechnique(techniqueRequest.getTypeTechnique());
        technique.setColor(techniqueRequest.getColor());
        technique.setWeight(techniqueRequest.getWeight());
        technique.setLoadCapacity(techniqueRequest.getLoadCapacity());
        technique.setYearOfProduction(techniqueRequest.getYearOfProduction());
        technique.setBaseCost(techniqueRequest.getBaseCost());
    }

    private TechniqueInfoResponse buildTechniqueResponse(Technique technique, String stateNumber){
        return TechniqueInfoResponse.builder()
                .message("Данные об технике с госномер " + stateNumber)
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

    private void validateTechniqueNoEmpty(List<Technique> technique){
        if (technique.isEmpty()) {
            throw new CommonBackendException("Список технике пуст", HttpStatus.NOT_FOUND);
        }
    }
}
