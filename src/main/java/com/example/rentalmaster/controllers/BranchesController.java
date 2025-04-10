package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.BranchesRequest;
import com.example.rentalmaster.model.dto.request.BranchesRequestUpdate;
import com.example.rentalmaster.model.dto.response.BranchesResponse;
import com.example.rentalmaster.model.dto.response.TechniqueInfoResponse;
import com.example.rentalmaster.service.BranchesService;
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
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchesController {

    private final BranchesService branchesService;

    @PostMapping
    @Operation(summary = "Создать филиал")
    public BranchesResponse addBranches(@RequestBody @Valid BranchesRequest branchesRequest) {
        return branchesService.addBranches(branchesRequest);

    }

    @PutMapping("/{branchName}")
    @Operation(summary = "Изменить данные филиала")
    public BranchesResponse updateClient(@PathVariable @Valid String branchName,
                                         @RequestBody @Valid BranchesRequestUpdate branchesRequest) {
        return branchesService.updateBranches(branchName, branchesRequest);

    }

    @DeleteMapping("/{branchName}")
    @Operation(summary = "Удалить филиал")
    public BranchesResponse deleteClient(@PathVariable("branchName") String branchName) {
        return branchesService.deleteBranch(branchName);

    }

    @GetMapping
    @Operation(summary = "Получить список всех филиалов")
    public List<BranchesResponse> getAllBranches() {
        return branchesService.getAllBranches();

    }

    @GetMapping("/{branchName}/techniques")
    @Operation(summary = "Получить список технике на филиале")
    public List<TechniqueInfoResponse> getTechniquesByBranch(@PathVariable String branchName) {
        return branchesService.getTechniquesByBranchName(branchName);

    }

    @PostMapping("/{branchName}/techniques/{techniqueStateNumber}")
    @Operation(summary = "Добавить технику в филиал")
    public BranchesResponse addTechniqueToBranch(
            @PathVariable String branchName,
            @PathVariable String techniqueStateNumber) {

        return branchesService.addTechniqueToBranch(branchName, techniqueStateNumber);

    }

    @DeleteMapping("/{branchName}/techniques/{techniqueStateNumber}")
    @Operation(summary = "Удалить технику из филиала")
    public BranchesResponse deleteTechniqueToBranch(
            @PathVariable String branchName,
            @PathVariable String techniqueStateNumber) {

        return branchesService.deleteTechniqueToBranch(branchName, techniqueStateNumber);

    }
}
