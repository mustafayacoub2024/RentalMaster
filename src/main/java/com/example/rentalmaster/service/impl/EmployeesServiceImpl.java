package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeesServiceImpl implements EmployeesService {

    private final EmployeesRepository employeesRepository;
    private final ObjectMapper objectMapper;

    @Override
    public EmployeesResponse addEmployee(EmployeesRequest employeesRequest) {
        employeesRepository.findByPersonalNumber(employeesRequest.getPersonalNumber()).ifPresent(employees -> {
            throw new CommonBackendException("Сотрудник с табельным номером "
                    + employeesRequest.getPersonalNumber() + " уже существует", HttpStatus.CONFLICT);
        });

        Employees employees = objectMapper.convertValue(employeesRequest, Employees.class);
        Employees employeesSave = employeesRepository.save(employees);

        EmployeesResponse response = objectMapper.convertValue(employeesSave, EmployeesResponse.class);
        response.setMessage("Сотрудник с табельным номером " + employeesSave.getPersonalNumber() + " успешно создан");
        return response;
    }

    @Override
    public EmployeesResponse updateEmployee(String personalNumber, EmployeesRequest employeesRequest) {
        Employees employees = employeesRepository.findByPersonalNumber(employeesRequest.getPersonalNumber())
                .orElseThrow(() -> new CommonBackendException("Сотрудник с табельным номером "
                        + employeesRequest.getPersonalNumber() + " не найден", HttpStatus.NOT_FOUND));

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
    public EmployeesResponse deleteEmployee(String personalNumber) {
        Employees employees = employeesRepository.findByPersonalNumber(personalNumber)
                .orElseThrow(() -> new CommonBackendException("Сотрудник с табельным номером "
                        + personalNumber + " не найден", HttpStatus.NOT_FOUND));

        employeesRepository.delete(employees);

        EmployeesResponse response = objectMapper.convertValue(employees, EmployeesResponse.class);
        response.setMessage("Сотрудник с табельным номером " + personalNumber + " успешно удалён");
        return response;
    }

    @Override
    public EmployeesResponse getEmployeeByPersonalNumber(String personalNumber) {
        Employees employees = employeesRepository.findByPersonalNumber(personalNumber)
                .orElseThrow(() -> new CommonBackendException("Сотрудник с табельным номером "
                        + personalNumber + " не найден", HttpStatus.NOT_FOUND));

        EmployeesResponse response = EmployeesResponse.builder()
                .personalNumber(employees.getPersonalNumber())
                .firstName(employees.getFirstName())
                .lastName(employees.getLastName())
                .email(employees.getEmail())
                .phone(employees.getPhone())
                .role(employees.getRole())
                .message("Данные сотрудника успешно получены")
                .build();

        return response;
    }

    @Override
    public List<EmployeesResponse> getAllEmployees() {
        List<Employees> employees = employeesRepository.findAll();

        if (employees.isEmpty()) {
            throw new CommonBackendException("Список сотрудников пуст", HttpStatus.NOT_FOUND);
        }
        return employees.stream()
                .map(employee -> {
                    EmployeesResponse response = objectMapper.convertValue(employee, EmployeesResponse.class);
                    response.setMessage("Сотрудник " + employee.getLastName() + " " + employee.getFirstName() +
                            " ,табельный номер" + employee.getPersonalNumber());
                    return response;
                }).toList();
    }
}
