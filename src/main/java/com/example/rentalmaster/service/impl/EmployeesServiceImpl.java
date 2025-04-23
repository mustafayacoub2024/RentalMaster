package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.model.db.repository.EmployeesRepository;
import com.example.rentalmaster.model.dto.request.EmployeesRequest;
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
        log.info("Создание карточки на сотрудника с персональным номером:{}", employeesRequest);
        validateEmployeeNotExists(employeesRequest.getPersonalNumber());
        Employees employees = objectMapper.convertValue(employeesRequest, Employees.class);
        Employees employeesSave = employeesRepository.save(employees);
        log.info("Сохранене карточки на сотрудника с табельным номером:{} в бд", employeesSave);
        EmployeesResponse response = objectMapper.convertValue(employeesSave, EmployeesResponse.class);
        response.setMessage("Сотрудник с табельным номером " + employeesSave.getPersonalNumber() + " успешно создан");
        return response;
    }

    @Override
    public EmployeesResponse updateEmployee(String personalNumber, EmployeesRequest employeesRequest) {
        log.info("Обновление данных сотрудника:{} c персональным номером", personalNumber);
        Employees employees = validateEmployeeNoFound(personalNumber);
        updateEmployeeInfo(employees, employeesRequest);
        Employees employeesUpdate = employeesRepository.save(employees);
        log.info("Новые данные сотрудника с табельным номером:{} сохранены в бд", employeesUpdate);
        EmployeesResponse response = objectMapper.convertValue(employees, EmployeesResponse.class);
        response.setMessage("Сотрудник с табельным номером " + employeesUpdate.getPersonalNumber() + " успешно изминён");
        log.info("Сотрудник с табельным номером:{} успешно изминены", personalNumber);
        return response;
    }

    @Override
    public EmployeesResponse deleteEmployee(String personalNumber) {
        log.info("Удаление данных сотрудника с табельным номером:{}", personalNumber);
        Employees employees = validateEmployeeNoFound(personalNumber);
        employeesRepository.delete(employees);
        EmployeesResponse response = objectMapper.convertValue(employees, EmployeesResponse.class);
        response.setMessage("Сотрудник с табельным номером " + personalNumber + " успешно удалён");
        log.info("Сотрудника с табельным номером:{} успешно удалён", personalNumber);
        return response;
    }

    @Override
    public EmployeesResponse getEmployeeByPersonalNumber(String personalNumber) {
        log.info("Запрос данных о сотруднике с персональным номером:{}", personalNumber);
        Employees employees = validateEmployeeNoFound(personalNumber);
        log.info("Запрос данных о сотруднике с персональным номером:{} успешно получены", personalNumber);
        return buildEmployeeResponse(employees, personalNumber);
    }

    @Override
    public List<EmployeesResponse> getAllEmployees() {
        log.info("Запрос списка всех сотрудников");
        List<Employees> employees = employeesRepository.findAll();
        log.info("Количество найденых сотрудников:{}",employees.size());
        validateEmployeeNoEmpty(employees);
        return employees.stream()
                .map(employee -> {
                    EmployeesResponse response = objectMapper.convertValue(employee, EmployeesResponse.class);
                    response.setMessage("Сотрудник " + employee.getLastName() + " " + employee.getFirstName() +
                            " ,табельный номер" + employee.getPersonalNumber());
                    return response;
                }).toList();
    }

    @Override
    public Employees getEmployee(String personalNumber) {
        return validateEmployeeNoFound(personalNumber);
    }

    private void validateEmployeeNotExists(String personalNumber) {
        employeesRepository.findByPersonalNumber(personalNumber).ifPresent(employees -> {
            throw new CommonBackendException("Сотрудник с табельным номером "
                    + personalNumber + " уже существует", HttpStatus.CONFLICT);
        });
    }

    private Employees validateEmployeeNoFound(String personalNumber) {
        return employeesRepository.findByPersonalNumber(personalNumber)
                .orElseThrow(() -> new CommonBackendException("Сотрудник с табельным номером "
                        + personalNumber + " не найден", HttpStatus.NOT_FOUND));
    }

    public void updateEmployeeInfo(Employees employees, EmployeesRequest employeesRequest) {
        employees.setPersonalNumber(employeesRequest.getPersonalNumber());
        employees.setLastName(employeesRequest.getLastName());
        employees.setFirstName(employeesRequest.getFirstName());
        employees.setEmail(employeesRequest.getEmail());
        employees.setPhone(employeesRequest.getPhone());
        employees.setRole(employees.getRole());
    }

    private EmployeesResponse buildEmployeeResponse(Employees employees, String personalNumber){
        return  EmployeesResponse.builder()
                .personalNumber(personalNumber)
                .firstName(employees.getFirstName())
                .lastName(employees.getLastName())
                .email(employees.getEmail())
                .phone(employees.getPhone())
                .role(employees.getRole())
                .message("Данные сотрудника успешно получены")
                .build();
    }

    private void validateEmployeeNoEmpty(List<Employees> employees){
        if (employees.isEmpty()) {
            throw new CommonBackendException("Список сотрудников пуст", HttpStatus.NOT_FOUND);
        }
    }
}

