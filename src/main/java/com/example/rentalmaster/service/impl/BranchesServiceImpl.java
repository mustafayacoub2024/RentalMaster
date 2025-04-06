package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.db.repository.BranchesRepository;
import com.example.rentalmaster.model.db.repository.TechniqueRepository;
import com.example.rentalmaster.model.dto.request.BranchesRequest;
import com.example.rentalmaster.model.dto.request.BranchesRequestUpdate;
import com.example.rentalmaster.model.dto.response.BranchesResponse;
import com.example.rentalmaster.model.dto.response.TechniqueInfoResponse;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;
import com.example.rentalmaster.service.BranchesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        branchesRepository.findByBranchName(branchesRequest.getBranchName()).ifPresent(branches -> {
            throw new CommonBackendException("Филиал" + branchesRequest.getBranchName() +
                    " уже существует", HttpStatus.CONFLICT);
        });
        Branches branches = objectMapper.convertValue(branchesRequest, Branches.class);
        Branches savedBranch = branchesRepository.save(branches);

        BranchesResponse branchesResponse = objectMapper.convertValue(savedBranch, BranchesResponse.class);
        branchesResponse.setMessage("Филиал " + branchesRequest.getBranchName() + " успешно создан");
        return branchesResponse;

    }

    @Override
    public BranchesResponse updateBranches(String branchName, BranchesRequestUpdate branchesRequest) {
        Branches branches = branchesRepository.findByBranchName(branchName)
                .orElseThrow(() -> new CommonBackendException("Филиал "
                        + branchName + " не найден", HttpStatus.NOT_FOUND));

        branches.setBranchName(branchesRequest.getBranchName());
        branches.setCity(branchesRequest.getCity());
        branches.setPhone(branchesRequest.getPhone());
        branches.setAddress(branchesRequest.getAddress());
        branches.setEmail(branchesRequest.getEmail());
        branches.setEmployees(branchesRequest.getEmployees());
        branches.setCoefficient(branchesRequest.getCoefficient());
        branchesRepository.save(branches);

        BranchesResponse branchesResponse = objectMapper.convertValue(branches, BranchesResponse.class);
        branchesResponse.setMessage("Филиал с id  " + branchName + " успешно изменён");
        return branchesResponse;
    }

    @Override
    @Transactional
    public BranchesResponse deleteBranch(String branchName) {
        Branches branches = branchesRepository.findByBranchName(branchName)
                .orElseThrow(() -> new CommonBackendException("Филиал "
                        + branchName + " не найден", HttpStatus.NOT_FOUND));

        branchesRepository.delete(branches);

        BranchesResponse branchesResponse = objectMapper.convertValue(branches, BranchesResponse.class);
        branchesResponse.setMessage("Филиал " + branchName + " успешно удалён");
        return branchesResponse;
    }

    @Override
    public List<BranchesResponse> getAllBranches() {
        List<Branches> branches = branchesRepository.findAll();

        if (branches.isEmpty()) {
            throw new CommonBackendException(
                    "Филиалы отсутствуют в системе",
                    HttpStatus.NOT_FOUND
            );
        }
        return (List<BranchesResponse>) branches.stream()
                .map(branch -> BranchesResponse.builder()
                        .message("Найден филиал в городе " + branch.getCity())
                        .branchName(branch.getBranchName())
                        .build())
                .collect(Collectors.toList());

    }

    @Override
    public List<TechniqueInfoResponse> getTechniquesByBranchName(String branchName) {
        Branches branch = branchesRepository.findByBranchName(branchName)
                .orElseThrow(() -> new CommonBackendException("Филиал " + branchName + " не найден"
                        , HttpStatus.NOT_FOUND));

        return branch.getTechniques().stream()
                .map(technique -> TechniqueInfoResponse.builder()
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
    public BranchesResponse addTechniqueToBranch(String branchName, String techniqueStateNumber) {
        Branches branches = branchesRepository.findByBranchName(branchName)
                .orElseThrow(() -> new CommonBackendException("Филиал "
                        + branchName + " не найден", HttpStatus.NOT_FOUND));

        Technique technique = techniqueRepository.findByStateNumber(techniqueStateNumber).
                orElseThrow(() -> new CommonBackendException("Техника с госномером " +
                        techniqueStateNumber + " не найден", HttpStatus.NOT_FOUND));

        branches.getTechniques().add(technique);
        branchesRepository.save(branches);

        BranchesResponse branchesResponse = objectMapper.convertValue(branches, BranchesResponse.class);
        branchesResponse.setMessage("Техника c госномером " + techniqueStateNumber + " успешно добавленна в филиал" + branchName);
        return branchesResponse;
    }

    @Override
    public BranchesResponse deleteTechniqueToBranch(String branchName, String techniqueStateNumber) {
        Branches branches = branchesRepository.findByBranchName(branchName)
                .orElseThrow(() -> new CommonBackendException("Филиал "
                        + branchName + " не найден", HttpStatus.NOT_FOUND));

        Technique technique = techniqueRepository.findByStateNumber(techniqueStateNumber).
                orElseThrow(() -> new CommonBackendException("Техника с госномером " +
                        techniqueStateNumber + " не найден", HttpStatus.NOT_FOUND));

        if (!branches.getTechniques().contains(technique)) {
            throw new CommonBackendException("Техника не принадлежит указанному филиалу", HttpStatus.BAD_REQUEST);
        }
            branches.getTechniques().remove(technique);
            branchesRepository.save(branches);

            BranchesResponse branchesResponse = objectMapper.convertValue(branches, BranchesResponse.class);
            branchesResponse.setMessage("Техника c госномером " + techniqueStateNumber + " успешно удалена из филиала" + branchName);
            return branchesResponse;

        }
    }
