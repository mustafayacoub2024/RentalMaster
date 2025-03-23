package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.request.TechniqueRequest;
import com.example.rentalmaster.model.dto.response.ClientsResponse;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;
import com.example.rentalmaster.service.ClientsService;
import com.example.rentalmaster.service.TechniqueService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/technique")
@RequiredArgsConstructor
public class TechniqueController {

    private final TechniqueService techniqueService;

    @PostMapping
    @Operation(summary = "Создать карточка на технику")
    public TechniqueResponse addTechnique(@RequestBody @Valid TechniqueRequest techniqueRequest) {
        try {
            return techniqueService.addTechnique(techniqueRequest);
        } catch (RuntimeException e) {
            TechniqueResponse techniqueResponse = new TechniqueResponse();
            techniqueResponse.setMessage(e.getMessage());
            return techniqueResponse;
        }
    }

    @PutMapping("/{state_number}")
    @Operation(summary = "Изменить карточка на технику")
    public TechniqueResponse updateTechnique(@PathVariable("state_number") String stateNumber,
                                             @RequestBody @Valid TechniqueRequest techniqueRequest) {
        try {
            return techniqueService.updateTechnique(stateNumber, techniqueRequest);
        } catch (RuntimeException e) {
            TechniqueResponse techniqueResponse = new TechniqueResponse();
            techniqueResponse.setMessage(e.getMessage());
            return techniqueResponse;
        }
    }

    @DeleteMapping("/{state_number}")
    @Operation(summary = "Удалить карточка на технику")
    public TechniqueResponse deleteTechnique(@PathVariable("state_number") String stateNumber) {
        try {
            return techniqueService.deleteTechnique(stateNumber);
        } catch (RuntimeException e) {
            TechniqueResponse techniqueResponse = new TechniqueResponse();
            techniqueResponse.setMessage(e.getMessage());
            return techniqueResponse;
        }
    }
}
