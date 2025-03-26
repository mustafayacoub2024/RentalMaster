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
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public EmployeesResponse addClient(@RequestBody @Valid EmployeesRequest employeesRequest) {
        try {
            return employeesService.addClient(employeesRequest);
        } catch (RuntimeException e) {
            EmployeesResponse employeesResponse = new EmployeesResponse();
            employeesResponse.setMessage(e.getMessage());
            return employeesResponse;
        }
    }

    @PutMapping("/{personalNumber}")
    @Operation(summary = "Изменить данные сотрудника")
    public EmployeesResponse updateClient(@PathVariable @Valid String personalNumber,
                                          @RequestBody @Valid EmployeesRequest employeesRequest) {
        try {
            return employeesService.updateClient(personalNumber, employeesRequest);
        } catch (RuntimeException e) {
            EmployeesResponse employeesResponse = new EmployeesResponse();
            employeesResponse.setMessage(e.getMessage());
            return employeesResponse;
        }
    }

    @DeleteMapping("/{personalNumber}")
    @Operation(summary = "Удалить данные сотрудника")
    public EmployeesResponse deleteClient(@PathVariable("personalNumber") String personalNumber) {
        try {
            return employeesService.deleteClient(personalNumber);
        } catch (RuntimeException e) {
            EmployeesResponse employeesResponse = new EmployeesResponse();
            employeesResponse.setMessage(e.getMessage());
            return employeesResponse;
        }
    }
}
