package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.EmployeesRequest;
import com.example.rentalmaster.model.dto.response.EmployeesResponse;
import com.example.rentalmaster.service.EmployeesService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeesController {

    private final EmployeesService employeesService;

    @PostMapping
    @Operation(summary = "Создать сотрудника")
    @PreAuthorize("hasRole('DIRECTOR')")
    public EmployeesResponse addEmployee(@RequestBody @Valid EmployeesRequest employeesRequest) {
        return employeesService.addEmployee(employeesRequest);

    }

    @PutMapping("/{personalNumber}")
    @Operation(summary = "Изменить данные сотрудника")
    @PreAuthorize("hasRole('DIRECTOR')")
    public EmployeesResponse updateEmployee(@PathVariable @Valid String personalNumber,
                                            @RequestBody @Valid EmployeesRequest employeesRequest) {
        return employeesService.updateEmployee(personalNumber, employeesRequest);

    }

    @DeleteMapping("/{personalNumber}")
    @Operation(summary = "Удалить данные сотрудника")
    @PreAuthorize("hasRole('DIRECTOR')")
    public EmployeesResponse deleteEmployee(@PathVariable("personalNumber") String personalNumber) {
        return employeesService.deleteEmployee(personalNumber);

    }

    @GetMapping("/{personalNumber}")
    @Operation(summary = "Получить данные сотрудника по табельному номеру")
    @PreAuthorize("hasRole('DIRECTOR')")
    public EmployeesResponse getEmployeeByPersonalNumber(@PathVariable("personalNumber") String personalNumber) {
        return employeesService.getEmployeeByPersonalNumber(personalNumber);

    }

    @GetMapping()
    @Operation(summary = "Список всех сотрудников")
    @PreAuthorize("hasRole('DIRECTOR')")
    public List<EmployeesResponse> getAllEmployees() {
        return employeesService.getAllEmployees();

    }
}