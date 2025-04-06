package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.request.TechniqueRequest;
import com.example.rentalmaster.model.dto.request.TechniqueUpdateRequest;
import com.example.rentalmaster.model.dto.response.ClientsResponse;
import com.example.rentalmaster.model.dto.response.TechniqueInfoResponse;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;
import com.example.rentalmaster.service.ClientsService;
import com.example.rentalmaster.service.TechniqueService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TechniqueResponse> addTechnique(@RequestBody @Valid TechniqueRequest techniqueRequest) {
        TechniqueResponse techniqueResponse = techniqueService.addTechnique(techniqueRequest);
        return ResponseEntity.ok(techniqueResponse);
    }

    @PutMapping("/{state_number}")
    @Operation(summary = "Изменить карточка на технику")
    public ResponseEntity<TechniqueResponse> updateTechnique(@PathVariable("state_number") String stateNumber,
                                                             @RequestBody @Valid TechniqueUpdateRequest techniqueRequest) {
        TechniqueResponse techniqueResponse = techniqueService.updateTechnique(stateNumber, techniqueRequest);
        return ResponseEntity.ok(techniqueResponse);
    }

    @DeleteMapping("/{state_number}")
    @Operation(summary = "Удалить карточка на технику")
    public ResponseEntity<TechniqueResponse> deleteTechnique(@PathVariable("state_number") String stateNumber) {
        TechniqueResponse techniqueResponse = techniqueService.deleteTechnique(stateNumber);
        return ResponseEntity.ok(techniqueResponse);
    }

    @GetMapping("/{state_number}")
    @Operation(summary = "Получение данных о технике")
    public ResponseEntity<TechniqueInfoResponse> getTechnique(@PathVariable("state_number") String stateNumber) {
        TechniqueInfoResponse techniqueResponse = techniqueService.getTechnique(stateNumber);
        return ResponseEntity.ok(techniqueResponse);
    }

    @GetMapping()
    @Operation(summary = "Получение списка техника")
    public ResponseEntity<List<TechniqueResponse>> getAllTechniques() {
       List<TechniqueResponse> techniqueResponse = techniqueService.getAllTechnique();
        return ResponseEntity.ok(techniqueResponse);
    }
}
