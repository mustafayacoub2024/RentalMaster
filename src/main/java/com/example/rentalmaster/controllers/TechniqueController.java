package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.TechniqueRequest;
import com.example.rentalmaster.model.dto.request.TechniqueUpdateRequest;
import com.example.rentalmaster.model.dto.response.TechniqueInfoResponse;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;
import com.example.rentalmaster.service.TechniqueService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/technique")
@RequiredArgsConstructor
public class TechniqueController {

    private final TechniqueService techniqueService;

    @PostMapping
    @Operation(summary = "Создать карточка на технику")
    public TechniqueResponse addTechnique(@RequestBody @Valid TechniqueRequest techniqueRequest) {
        return techniqueService.addTechnique(techniqueRequest);

    }

    @PutMapping("/{state_number}")
    @Operation(summary = "Изменить карточка на технику")
    public TechniqueResponse updateTechnique(@PathVariable("state_number") String stateNumber,
                                             @RequestBody @Valid TechniqueUpdateRequest techniqueRequest) {
        return techniqueService.updateTechnique(stateNumber, techniqueRequest);

    }

    @DeleteMapping("/{state_number}")
    @Operation(summary = "Удалить карточка на технику")
    public TechniqueResponse deleteTechnique(@PathVariable("state_number") String stateNumber) {
        return techniqueService.deleteTechnique(stateNumber);

    }

    @GetMapping("/{state_number}")
    @Operation(summary = "Получение данных о технике")
    public TechniqueInfoResponse getTechnique(@PathVariable("state_number") String stateNumber) {
        return techniqueService.getTechnique(stateNumber);

    }

    @GetMapping()
    @Operation(summary = "Получение списка техника")
    public List<TechniqueResponse> getAllTechniques() {
        return techniqueService.getAllTechnique();

    }
}