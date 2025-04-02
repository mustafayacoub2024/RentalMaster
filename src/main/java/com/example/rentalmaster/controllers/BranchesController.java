package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.dto.request.BranchesRequest;
import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.response.BranchesResponse;
import com.example.rentalmaster.model.dto.response.ClientsResponse;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;
import com.example.rentalmaster.service.BranchesService;
import com.example.rentalmaster.service.ClientsService;
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
import java.util.UUID;

@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchesController {

    private final BranchesService branchesService;

    @PostMapping
    @Operation(summary = "Создать филиал")
    public BranchesResponse addBranches(@RequestBody @Valid BranchesRequest branchesRequest) {
        try {
            return branchesService.addBranches(branchesRequest);
        } catch (RuntimeException e) {
            BranchesResponse branchesResponse = new BranchesResponse();
            branchesResponse.setMessage(e.getMessage());
            return branchesResponse;
        }
    }

    @PutMapping("/{branchId}")
    @Operation(summary = "Изменить данные филиала")
    public BranchesResponse updateClient(@PathVariable @Valid UUID branchId, @RequestBody @Valid  BranchesRequest branchesRequest) {
        try {
            return branchesService.updateBranches(branchId, branchesRequest);
        } catch (RuntimeException e) {
            BranchesResponse branchesResponse = new BranchesResponse();
            branchesResponse.setMessage(e.getMessage());
            return branchesResponse;
        }
    }

    @DeleteMapping("/{branchId}")
    @Operation(summary = "Удалить филиал")
    public BranchesResponse deleteClient(@PathVariable("branchId") UUID branchId) {
        try {
            return branchesService.deleteClient(branchId);
        } catch (RuntimeException e) {
            BranchesResponse branchesResponse = new BranchesResponse();
            branchesResponse.setMessage(e.getMessage());
            return branchesResponse;
        }
    }

    @GetMapping
    @Operation(summary = "Получить список всех филиалов")
    public List<BranchesResponse> getAllBranches() {
        try {
            return branchesService.getAllBranches();
        } catch (RuntimeException e) {
            BranchesResponse branchesResponse = new BranchesResponse();
            branchesResponse.setMessage(e.getMessage());
            return (List<BranchesResponse>) branchesResponse;
        }
    }

    @GetMapping("/{branchId}/techniques")
    @Operation(summary = "Получить список технике на филиале")
    public ResponseEntity<List<TechniqueResponse>> getTechniquesByBranch(@PathVariable UUID branchId) {
        List<TechniqueResponse> techniques = branchesService.getTechniquesByBranchId(branchId);
        return ResponseEntity.ok(techniques);
    }

    @PostMapping("/{branchId}/techniques/{techniqueStateNumber}")
    @Operation(summary = "Добавить технику в филиал")
    public BranchesResponse addTechniqueToBranch(
            @PathVariable UUID branchId,
            @PathVariable String techniqueStateNumber) {
        try {
            return branchesService.addTechniqueToBranch(branchId, techniqueStateNumber);
        } catch (RuntimeException e) {
            BranchesResponse response = new BranchesResponse();
            response.setMessage(e.getMessage());
            return response;
        }
    }
}
