package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.db.repository.BranchesRepository;
import com.example.rentalmaster.model.db.repository.DriversRepository;
import com.example.rentalmaster.model.db.repository.TechniqueRepository;
import com.example.rentalmaster.model.dto.request.BranchesRequest;
import com.example.rentalmaster.model.dto.request.BranchesRequestUpdate;
import com.example.rentalmaster.model.dto.response.BranchesResponse;
import com.example.rentalmaster.model.dto.response.DriverInfoResponse;
import com.example.rentalmaster.model.dto.response.TechniqueInfoResponse;
import com.example.rentalmaster.service.BranchesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchesServiceImpl implements BranchesService {

    private final BranchesRepository branchesRepository;

    private final ObjectMapper objectMapper;

    private final TechniqueRepository techniqueRepository;

    private final DriversRepository driversRepository;

    @Override
    public BranchesResponse addBranches(BranchesRequest branchesRequest) {
        log.info("Создание нового филиала:{}", branchesRequest.getBranchName());
        validateBranchNotExists(branchesRequest.getBranchName());
        Branches branches = objectMapper.convertValue(branchesRequest, Branches.class);
        Branches savedBranch = branchesRepository.save(branches);
        log.info("Сохранение нового филиала в бд:{}", savedBranch.getBranchName());
        BranchesResponse branchesResponse = objectMapper.convertValue(savedBranch, BranchesResponse.class);
        branchesResponse.setMessage("Филиал " + branchesRequest.getBranchName() + " успешно создан");
        log.info("Филиал успешно сохранён:{}", branchesResponse.getBranchName());
        return branchesResponse;

    }

    @Override
    public BranchesResponse updateBranches(String branchName, BranchesRequestUpdate branchesRequest) {
        log.info("Обновление данных филиала:{}", branchName);
        Branches branches = validateBranchNoFound(branchName);
        log.info("Старые данные филиала:{}", branches);
        updateBranch(branches, branchesRequest);
        Branches updateBranches = branchesRepository.save(branches);
        log.info("Новые данные филиала:{}", updateBranches);
        BranchesResponse branchesResponse = objectMapper.convertValue(branches, BranchesResponse.class);
        branchesResponse.setMessage("Филиал с id  " + branchName + " успешно изменён");
        log.info("Данные филиала:{} успешно обнавлены", branchName);
        return branchesResponse;
    }

    @Override
    @Transactional
    public BranchesResponse deleteBranch(String branchName) {
        log.info("Удаление филиала:{}", branchName);
        Branches branches = validateBranchNoFound(branchName);
        branchesRepository.delete(branches);
        log.info("Филиал:{} успешно удалён", branchName);
        BranchesResponse branchesResponse = objectMapper.convertValue(branches, BranchesResponse.class);
        branchesResponse.setMessage("Филиал " + branchName + " успешно удалён");
        return branchesResponse;
    }

    @Override
    public List<BranchesResponse> getAllBranches() {
        log.info("Запрос списка всех филиалов");
        List<Branches> branches = branchesRepository.findAll();
        validateBranchNoEmpty(branches);
        log.info("Всего филиалов найдено:{}", branches.size());
        return branches.stream()
                .map(branch -> BranchesResponse.builder()
                        .message("Найден филиал в городе " + branch.getCity())
                        .branchName(branch.getBranchName())
                        .build())
                .collect(Collectors.toList());

    }

    @Override
    public List<TechniqueInfoResponse> getTechniquesByBranchName(String branchName) {
        log.info("Запрос списка технике в филиале");
        Branches branch = validateBranchNoFound(branchName);
        log.info("Всего технике в филиале:{}", branch.getTechniques().size());
        return convertTechniquesToResponses(branch);
    }

    @Override
    public BranchesResponse addTechniqueToBranch(String branchName, String techniqueStateNumber) {
        log.info("Добавление технике:{} в филиал", techniqueStateNumber);
        Branches branch = validateBranchNoFound(branchName);
        Technique technique = validateNoFoundTechnique(techniqueStateNumber);

        branch.getTechniques().add(technique);
        branchesRepository.save(branch);
        log.info("Техника:{} успешно добавлена в филиал:{}", techniqueStateNumber, branchName);
        BranchesResponse branchesResponse = objectMapper.convertValue(branch, BranchesResponse.class);
        branchesResponse.setMessage("Техника c госномером " + techniqueStateNumber + " успешно добавленна в филиал" + branchName);
        return branchesResponse;
    }

    @Override
    public BranchesResponse deleteTechniqueToBranch(String branchName, String techniqueStateNumber) {
        log.info("Удаление технике:{} из филиала:{}", techniqueStateNumber, branchName);
        Branches branch = validateBranchNoFound(branchName);
        Technique technique = validateNoFoundTechnique(techniqueStateNumber);

        validateTechniqueBelongsToBranch(branch, technique);
        branch.getTechniques().remove(technique);
        branchesRepository.save(branch);
        log.info("Техника:{} удалена из филиала:{}", techniqueStateNumber, branchName);
        BranchesResponse branchesResponse = objectMapper.convertValue(branch, BranchesResponse.class);
        branchesResponse.setMessage("Техника c госномером " + techniqueStateNumber + " успешно удалена из филиала" + branchName);
        return branchesResponse;

    }

    @Override
    public List<Branches> getAll() {
        return branchesRepository.findAll();
    }

    @Override
    public Branches getBranchByBranchName(String branchName) {
        return validateBranchNoFound(branchName);
    }

    @Override
    public List<DriverInfoResponse> getDriversByBranchName(String branchName) {
        log.info("Запрос списка водителей на филиале:{}", branchName);
        Branches branch = validateBranchNoFound(branchName);
        log.info("Колчество водителей:{} в филиале:{}", branch.getDrivers().size(), branchName);
        return convertDriverToResponses(branch);
    }

    @Override
    public BranchesResponse addDriverToBranch(String branchName, String personalNumber) {
        log.info("добавление водителя:{} в филиал:{}", personalNumber, branchName);
        Branches branch = validateBranchNoFound(branchName);
        Drivers driver = validateDriverNoFound(personalNumber);

        branch.getDrivers().add(driver);
        branchesRepository.save(branch);
        log.info("Водитель:{} добавлен в филиал:{}", personalNumber, branchName);
        BranchesResponse branchesResponse = objectMapper.convertValue(branch, BranchesResponse.class);
        branchesResponse.setMessage("Водитель с табельным номером " + personalNumber + " успешно добавлен в филиал" + branchName);
        return branchesResponse;
    }

    @Override
    public BranchesResponse deleteDriverToBranch(String branchName, String personalNumber) {
        log.info("Удаление водителя:{} из филиала:{}", personalNumber, branchName);
        Branches branch = validateBranchNoFound(branchName);
        Drivers driver = validateDriverNoFound(personalNumber);

        branch.getDrivers().remove(driver);
        branchesRepository.save(branch);
        log.info("Водител:{} удалён из филиала:{}", personalNumber, branchName);
        BranchesResponse branchesResponse = objectMapper.convertValue(branch, BranchesResponse.class);
        branchesResponse.setMessage("Водитель с табельным номером " + personalNumber + " успешно удален из филиала " + branchName);
        return branchesResponse;
    }

    private void validateBranchNotExists(String branchName) {
        branchesRepository.findByBranchName(branchName)
                .ifPresent(b -> {
                    throw new CommonBackendException(
                            "Филиал " + branchName + " уже существует",
                            HttpStatus.CONFLICT
                    );
                });
    }

    private Branches validateBranchNoFound(String branchName) {
        return branchesRepository.findByBranchName(branchName)
                .orElseThrow(() -> new CommonBackendException("Филиал "
                        + branchName + " не найден", HttpStatus.NOT_FOUND));
    }

    private void updateBranch(Branches branches, BranchesRequestUpdate branchesRequest) {
        branches.setBranchName(branchesRequest.getBranchName());
        branches.setCity(branchesRequest.getCity());
        branches.setPhone(branchesRequest.getPhone());
        branches.setAddress(branchesRequest.getAddress());
        branches.setEmail(branchesRequest.getEmail());
        branches.setEmployees(branchesRequest.getEmployees());
        branches.setCoefficient(branchesRequest.getCoefficient());

    }

    private void validateBranchNoEmpty(List<Branches> branches) {
        if (branches.isEmpty()) {
            throw new CommonBackendException(
                    "Филиалы отсутствуют в системе",
                    HttpStatus.NOT_FOUND
            );
        }
    }

    private TechniqueInfoResponse buildTechniqueResponse(Technique technique, Branches branch) {
        return TechniqueInfoResponse.builder()
                .message("Техника найдена в филиале " + branch.getCity())
                .stateNumber(technique.getStateNumber())
                .yearOfProduction(technique.getYearOfProduction())
                .loadCapacity(technique.getLoadCapacity())
                .weight(technique.getWeight())
                .color(technique.getColor())
                .baseCost(technique.getBaseCost())
                .typeTechnique(technique.getTypeTechnique())
                .build();
    }

    private List<TechniqueInfoResponse> convertTechniquesToResponses(Branches branch) {
        return branch.getTechniques().stream()
                .map(technique -> buildTechniqueResponse(technique, branch))
                .collect(Collectors.toList());
    }

    private Technique validateNoFoundTechnique(String techniqueStateNumber) {
        return techniqueRepository.findByStateNumber(techniqueStateNumber).
                orElseThrow(() -> new CommonBackendException("Техника с госномером " +
                        techniqueStateNumber + " не найден", HttpStatus.NOT_FOUND));
    }

    private void validateTechniqueBelongsToBranch(Branches branch, Technique technique) {
        if (!branch.getTechniques().contains(technique)) {
            throw new CommonBackendException("Техника не принадлежит указанному филиалу", HttpStatus.BAD_REQUEST);
        }
    }

    private DriverInfoResponse buildDriverResponse(Drivers driver, Branches branch) {
        return DriverInfoResponse.builder()
                .message("Водитель найден в филиале " + branch.getBranchName())
                .personalNumber(driver.getPersonalNumber())
                .lastName(driver.getLastName())
                .firstName(driver.getFirstName())
                .phone(driver.getPhone())
                .email(driver.getEmail())
                .salary(driver.getSalary())
                .build();
    }

    private List<DriverInfoResponse> convertDriverToResponses(Branches branch) {
        return branch.getDrivers().stream()
                .map(driver -> buildDriverResponse(driver, branch))
                .collect(Collectors.toList());
    }

    private Drivers validateDriverNoFound(String personalNumber) {
        return driversRepository.findByPersonalNumber(personalNumber)
                .orElseThrow(() -> new CommonBackendException(
                        "Водитель с табельным номером " + personalNumber + " не найден", HttpStatus.NOT_FOUND));

    }
}
