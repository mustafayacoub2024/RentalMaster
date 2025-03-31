package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.db.repository.BranchesRepository;
import com.example.rentalmaster.model.db.repository.TechniqueRepository;
import com.example.rentalmaster.model.dto.request.BranchesRequest;
import com.example.rentalmaster.model.dto.response.BranchesResponse;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;
import com.example.rentalmaster.service.BranchesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchesServiceImpl implements BranchesService {

    private final BranchesRepository branchesRepository;

    private final ObjectMapper objectMapper;

    private final TechniqueRepository techniqueRepository;

    @Override
    public BranchesResponse addBranches(BranchesRequest branchesRequest) {
        Branches branch = objectMapper.convertValue(branchesRequest, Branches.class);
        branch.setBranchId(UUID.randomUUID());

        Branches savedBranch = branchesRepository.save(branch);

      BranchesResponse branchesResponse = objectMapper.convertValue(savedBranch, BranchesResponse.class);
      branchesResponse.setMessage("Филиал с id  " + branch.getBranchId() + " успешно создан");
      return branchesResponse;

    }

    @Override
    public BranchesResponse updateBranches(UUID branchId, BranchesRequest branchesRequest) {
        Branches branches = branchesRepository.findByBranchId(branchId)
                .orElseThrow(() -> new RuntimeException("Филиал с id "
                        + branchId + " не найден"));

        branches.setCity(branchesRequest.getCity());
        branches.setPhone(branchesRequest.getPhone());
        branches.setAddress(branchesRequest.getAddress());
        branches.setEmail(branchesRequest.getEmail());
        branches.setEmployees(branchesRequest.getEmployees());
        branchesRepository.save(branches);

        BranchesResponse branchesResponse = objectMapper.convertValue(branches, BranchesResponse.class);
        branchesResponse.setMessage("Филиал с id  " + branchId+ " успешно изменён");
        return branchesResponse;
    }

    @Override
    public BranchesResponse deleteClient(UUID branchId) {
        Branches branches = branchesRepository.findByBranchId(branchId)
                .orElseThrow(() -> new RuntimeException("Филиал с id "
                        + branchId + " не найден"));

        branchesRepository.delete(branches);

        BranchesResponse branchesResponse = objectMapper.convertValue(branches, BranchesResponse.class);
        branchesResponse.setMessage("Филиал с id  " + branchId+ " успешно удалён");
        return branchesResponse;
    }

    @Override
    public List<BranchesResponse> getAllBranches() {
        List<Branches> branches = branchesRepository.findAll();

        return (List<BranchesResponse>) branches.stream()
                .map(branch -> BranchesResponse.builder()
                        .message("Найден филиал в городе "+ branch.getCity())
                        .branchId(branch.getBranchId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<TechniqueResponse> getTechniquesByBranchId(UUID branchId) {
        Branches branch = branchesRepository.findByBranchId(branchId)
                .orElseThrow(() -> new RuntimeException("Филиал с id " + branchId + " не найден"));

        return branch.getTechniques().stream()
                .map(technique -> TechniqueResponse.builder()
                        .message("Техника найдена в филиале " + branch.getCity())
                        .stateNumber(technique.getStateNumber())
                        .yearOfProduction(technique.getYearOfProduction())
                        .loadCapacity(technique.getLoadCapacity())
                        .weight(technique.getWeight())
                        .color(technique.getColor())
                        .baseCost(technique.getBaseCost())
                        .typeTechnique(technique.getTypeTechnique())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public BranchesResponse addTechniqueToBranch(UUID branchId, String techniqueStateNumber) {
        Branches branches = branchesRepository.findByBranchId(branchId)
                .orElseThrow(() -> new RuntimeException("Филиал с id "
                        + branchId + " не найден"));

        Technique technique = techniqueRepository.findByStateNumber(techniqueStateNumber).
                orElseThrow(() -> new RuntimeException("Техника с госномером " +
                        techniqueStateNumber + " не найден"));

        branches.getTechniques().add(technique);
        branchesRepository.save(branches);

        BranchesResponse branchesResponse = objectMapper.convertValue(branches, BranchesResponse.class);
        branchesResponse.setMessage("Техника c госномером "+ techniqueStateNumber+ " успешно добавленна в филиал" + branchId);
        return branchesResponse;
    }
}
