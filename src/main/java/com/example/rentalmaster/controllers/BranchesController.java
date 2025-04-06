package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.dto.request.BranchesRequest;
import com.example.rentalmaster.model.dto.request.BranchesRequestUpdate;
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
    public ResponseEntity<BranchesResponse> addBranches(@RequestBody @Valid BranchesRequest branchesRequest) {
        BranchesResponse branchesResponse = branchesService.addBranches(branchesRequest);
        return ResponseEntity.ok(branchesResponse);

    }

    @PutMapping("/{branchName}")
    @Operation(summary = "Изменить данные филиала")
    public ResponseEntity<BranchesResponse> updateClient(@PathVariable @Valid String branchName,
                                                         @RequestBody @Valid BranchesRequestUpdate branchesRequest) {
        BranchesResponse branchesResponse = branchesService.updateBranches(branchName, branchesRequest);

        return ResponseEntity.ok(branchesResponse);
    }

    @DeleteMapping("/{branchName}")
    @Operation(summary = "Удалить филиал")
    public ResponseEntity<BranchesResponse> deleteClient(@PathVariable("branchName") String branchName) {
        BranchesResponse response = branchesService.deleteBranch(branchName);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Получить список всех филиалов")
    public ResponseEntity<List<BranchesResponse>> getAllBranches() {
        List<BranchesResponse> branchesResponse = branchesService.getAllBranches();
        return ResponseEntity.ok(branchesResponse);

    }

    @GetMapping("/{branchName}/techniques")
    @Operation(summary = "Получить список технике на филиале")
    public ResponseEntity<List<TechniqueResponse>> getTechniquesByBranch(@PathVariable String branchName) {
        List<TechniqueResponse> techniques = branchesService.getTechniquesByBranchName(branchName);
        return ResponseEntity.ok(techniques);
    }

    @PostMapping("/{branchName}/techniques/{techniqueStateNumber}")
    @Operation(summary = "Добавить технику в филиал")
    public ResponseEntity<BranchesResponse> addTechniqueToBranch(
            @PathVariable String branchName,
            @PathVariable String techniqueStateNumber) {

        BranchesResponse branchesResponse = branchesService.addTechniqueToBranch(branchName, techniqueStateNumber);
        return ResponseEntity.ok(branchesResponse);
    }

    @DeleteMapping("/{branchName}/techniques/{techniqueStateNumber}")
    @Operation(summary = "Удалить технику из филиала")
    public ResponseEntity<BranchesResponse> deleteTechniqueToBranch(
            @PathVariable String branchName,
            @PathVariable String techniqueStateNumber) {

        BranchesResponse branchesResponse = branchesService.deleteTechniqueToBranch(branchName, techniqueStateNumber);
        return ResponseEntity.ok(branchesResponse);

    }
}
