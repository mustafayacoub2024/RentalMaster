package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.model.db.repository.EmployeesRepository;
import com.example.rentalmaster.model.dto.request.EmployeesRequest;
import com.example.rentalmaster.model.dto.response.DriverResponse;
import com.example.rentalmaster.model.dto.response.EmployeesResponse;
import com.example.rentalmaster.service.EmployeesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeesServiceImpl implements EmployeesService {

    private final EmployeesRepository employeesRepository;
    private final ObjectMapper objectMapper;

    @Override
    public EmployeesResponse addClient(EmployeesRequest employeesRequest) {
        employeesRepository.findByPersonalNumber(employeesRequest.getPersonalNumber()).ifPresent(employees -> {
            throw new RuntimeException("Сотрудник с табельным номером "
                    + employeesRequest.getPersonalNumber() + " уже существует");
        });

       Employees employees = objectMapper.convertValue(employeesRequest, Employees.class);
       Employees employeesSave= employeesRepository.save(employees);

        EmployeesResponse response = objectMapper.convertValue(employeesSave, EmployeesResponse.class);
        response.setMessage("Сотрудник с табельным номером " + employeesSave.getPersonalNumber() + " успешно создан");
        return response;
    }

    @Override
    public EmployeesResponse updateClient(String personalNumber, EmployeesRequest employeesRequest) {
        Employees employees = employeesRepository.findByPersonalNumber(employeesRequest.getPersonalNumber())
                .orElseThrow(() -> new RuntimeException("Сотрудник с табельным номером "
                        + employeesRequest.getPersonalNumber() + " не найден"));

        employees.setPersonalNumber(employeesRequest.getPersonalNumber());
        employees.setLastName(employeesRequest.getLastName());
        employees.setFirstName(employeesRequest.getFirstName());
        employees.setEmail(employeesRequest.getEmail());
        employees.setPhone(employeesRequest.getPhone());
        employees.setRole(employees.getRole());
        Employees employeesUpdate = employeesRepository.save(employees);

        EmployeesResponse response = objectMapper.convertValue(employees, EmployeesResponse.class);
        response.setMessage("Сотрудник с табельным номером " + employeesUpdate.getPersonalNumber() + " успешно изминён");
        return response;
    }

    @Override
    public EmployeesResponse deleteClient(String personalNumber) {
        Employees employees = employeesRepository.findByPersonalNumber(personalNumber)
                .orElseThrow(() -> new RuntimeException("Сотрудник с табельным номером "
                        + personalNumber + " не найден"));

        employeesRepository.delete(employees);

        EmployeesResponse response = objectMapper.convertValue(employees, EmployeesResponse.class);
        response.setMessage("Сотрудник с табельным номером " + personalNumber + " успешно удалён");
        return response;
    }
}
