package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.request.EmployeesRequest;
import com.example.rentalmaster.model.dto.response.ClientsResponse;
import com.example.rentalmaster.model.dto.response.EmployeesResponse;
import com.example.rentalmaster.service.ClientsService;
import com.example.rentalmaster.service.EmployeesService;
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

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeesController {

    private final EmployeesService employeesService;

    @PostMapping
    @Operation(summary = "Создать сотрудника")
    public ResponseEntity<EmployeesResponse> addEmployee(@RequestBody @Valid EmployeesRequest employeesRequest) {
        EmployeesResponse employeesResponse = employeesService.addEmployee(employeesRequest);
        return ResponseEntity.ok(employeesResponse);
    }

    @PutMapping("/{personalNumber}")
    @Operation(summary = "Изменить данные сотрудника")
    public ResponseEntity<EmployeesResponse> updateEmployee(@PathVariable @Valid String personalNumber,
                                                            @RequestBody @Valid EmployeesRequest employeesRequest) {

        EmployeesResponse employeesResponse = employeesService.updateEmployee(personalNumber, employeesRequest);
        return ResponseEntity.ok(employeesResponse);
    }

    @DeleteMapping("/{personalNumber}")
    @Operation(summary = "Удалить данные сотрудника")
    public ResponseEntity<EmployeesResponse> deleteEmployee(@PathVariable("personalNumber") String personalNumber) {
        EmployeesResponse employeesResponse = employeesService.deleteEmployee(personalNumber);
        return ResponseEntity.ok(employeesResponse);
    }

    @GetMapping("/{personalNumber}")
    @Operation(summary = "Получить данные сотрудника по табельному номеру")
    public ResponseEntity<EmployeesResponse> getEmployeeByPersonalNumber(@PathVariable("personalNumber") String personalNumber) {
        EmployeesResponse employeesResponse = employeesService.getEmployeeByPersonalNumber(personalNumber);
        return ResponseEntity.ok(employeesResponse);
    }


}