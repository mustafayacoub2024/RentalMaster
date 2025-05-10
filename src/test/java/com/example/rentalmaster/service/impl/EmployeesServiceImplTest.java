package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.model.db.repository.EmployeesRepository;
import com.example.rentalmaster.model.dto.request.EmployeesRequest;
import com.example.rentalmaster.model.dto.response.EmployeesResponse;
import com.example.rentalmaster.model.enums.Roles;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmployeesServiceImplTest {

    @Mock
    private EmployeesRepository employeesRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmployeesServiceImpl employeesService;

    private EmployeesRequest employeesRequest;
    private Employees employeesEntity;
    private EmployeesResponse employeesResponse;

    @Test
    @DisplayName("Позитивный сценарий — успешное добавление нового сотрудника")
    public void testAddEmployeeSuccess() {

       var employeesRequest = EmployeesRequest.builder()
                .personalNumber("12345")
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .phone("1234567890")
                .role(Roles.DIRECTOR)
                .build();

       var employeesEntity = Employees.builder()
                .personalNumber("12345")
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .phone("1234567890")
                .role(Roles.DIRECTOR)
                .build();

       var employeesResponse = EmployeesResponse.builder()
                .personalNumber("12345")
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .phone("1234567890")
                .role(Roles.DIRECTOR)
                .message("Сотрудник с табельным номером 12345 успешно создан")
                .build();

        when(employeesRepository.findByPersonalNumber("12345")).thenReturn(Optional.empty());
        when(objectMapper.convertValue(employeesRequest, Employees.class)).thenReturn(employeesEntity);
        when(employeesRepository.save(employeesEntity)).thenReturn(employeesEntity);
        when(objectMapper.convertValue(employeesEntity, EmployeesResponse.class)).thenReturn(employeesResponse);

        EmployeesResponse result = employeesService.addEmployee(employeesRequest);

        assertNotNull(result);
        assertEquals("12345", result.getPersonalNumber());
        assertEquals("Сотрудник с табельным номером 12345 успешно создан", result.getMessage());

        verify(employeesRepository).save(employeesEntity);
    }

    @Test
    @DisplayName("Негативный сценарий — сотрудник с таким табельным номером уже существует")
    public void testAddEmployeeAlreadyExists() {

        employeesRequest = EmployeesRequest.builder()
                .personalNumber("12345")
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .phone("1234567890")
                .role(Roles.DIRECTOR)
                .build();

        employeesEntity = Employees.builder()
                .personalNumber("12345")
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .phone("1234567890")
                .role(Roles.DIRECTOR)
                .build();

        employeesResponse = EmployeesResponse.builder()
                .personalNumber("12345")
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .phone("1234567890")
                .role(Roles.DIRECTOR)
                .message("Сотрудник с табельным номером 12345 успешно создан")
                .build();
        when(employeesRepository.findByPersonalNumber("12345")).thenReturn(Optional.of(employeesEntity));

        CommonBackendException exception = assertThrows(CommonBackendException.class, () -> {
            employeesService.addEmployee(employeesRequest);
        });

        assertEquals("Сотрудник с табельным номером 12345 уже существует", exception.getMessage());
        verify(employeesRepository, never()).save(any());
    }

    @Test
    @DisplayName("Позитивный сценарий — обновление сотрудника")
    public void testUpdateEmployeeSuccess() {
        String personalNumber = "12345";

        EmployeesRequest updateRequest = EmployeesRequest.builder()
                .personalNumber("12345")
                .firstName("Пётр")
                .lastName("Петров")
                .email("petr@example.com")
                .phone("0987654321")
                .role(Roles.MANAGER)
                .build();

        Employees existingEmployee = Employees.builder()
                .personalNumber("12345")
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .phone("1234567890")
                .role(Roles.MANAGER)
                .build();

        Employees updatedEmployee = Employees.builder()
                .personalNumber("12345")
                .firstName("Пётр")
                .lastName("Петров")
                .email("petr@example.com")
                .phone("0987654321")
                .role(Roles.MANAGER)
                .build();

        EmployeesResponse response = EmployeesResponse.builder()
                .personalNumber("12345")
                .firstName("Пётр")
                .lastName("Петров")
                .email("petr@example.com")
                .phone("0987654321")
                .role(Roles.MANAGER)
                .message("Сотрудник с табельным номером 12345 успешно изминён")
                .build();

        when(employeesRepository.findByPersonalNumber(personalNumber)).thenReturn(Optional.of(existingEmployee));
        when(employeesRepository.save(any())).thenReturn(updatedEmployee);
        when(objectMapper.convertValue(updatedEmployee, EmployeesResponse.class)).thenReturn(response);

        EmployeesResponse result = employeesService.updateEmployee(personalNumber, updateRequest);

        assertNotNull(result);
        assertEquals("Пётр", result.getFirstName());
        assertEquals("Сотрудник с табельным номером 12345 успешно изминён", result.getMessage());
    }

    @Test
    @DisplayName("Позитивный сценарий — удаление сотрудника")
    public void testDeleteEmployeeSuccess() {
        String personalNumber = "12345";

        Employees employee = Employees.builder()
                .personalNumber("12345")
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .phone("1234567890")
                .role(Roles.DIRECTOR)
                .build();

        EmployeesResponse response = EmployeesResponse.builder()
                .personalNumber("12345")
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .phone("1234567890")
                .role(Roles.DIRECTOR)
                .message("Сотрудник с табельным номером 12345 успешно удалён")
                .build();

        when(employeesRepository.findByPersonalNumber(personalNumber)).thenReturn(Optional.of(employee));
        when(objectMapper.convertValue(employee, EmployeesResponse.class)).thenReturn(response);

        EmployeesResponse result = employeesService.deleteEmployee(personalNumber);

        assertNotNull(result);
        assertEquals("12345", result.getPersonalNumber());
        assertEquals("Сотрудник с табельным номером 12345 успешно удалён", result.getMessage());
        verify(employeesRepository).delete(employee);
    }

    @Test
    @DisplayName("Позитивный сценарий — получить сотрудника по табельному номеру")
    public void testGetEmployeeByPersonalNumberSuccess() {
        String personalNumber = "12345";

        Employees employee = Employees.builder()
                .personalNumber("12345")
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .phone("1234567890")
                .role(Roles.DIRECTOR)
                .build();

        when(employeesRepository.findByPersonalNumber(personalNumber)).thenReturn(Optional.of(employee));

        EmployeesResponse result = employeesService.getEmployeeByPersonalNumber(personalNumber);

        assertNotNull(result);
        assertEquals("12345", result.getPersonalNumber());
        assertEquals("Данные сотрудника успешно получены", result.getMessage());
    }

    @Test
    @DisplayName("Позитивный сценарий — получить список всех сотрудников")
    public void testGetAllEmployeesSuccess() {
        Employees employee1 = Employees.builder()
                .personalNumber("11111")
                .firstName("Анна")
                .lastName("Смирнова")
                .email("anna@example.com")
                .phone("1111111111")
                .role(Roles.MANAGER)
                .build();

        Employees employee2 = Employees.builder()
                .personalNumber("22222")
                .firstName("Борис")
                .lastName("Кузнецов")
                .email("boris@example.com")
                .phone("2222222222")
                .role(Roles.DIRECTOR)
                .build();

        when(employeesRepository.findAll()).thenReturn(List.of(employee1, employee2));

        when(objectMapper.convertValue(employee1, EmployeesResponse.class)).thenReturn(
                EmployeesResponse.builder()
                        .personalNumber("11111")
                        .firstName("Анна")
                        .lastName("Смирнова")
                        .email("anna@example.com")
                        .phone("1111111111")
                        .role(Roles.MANAGER)
                        .build());

        when(objectMapper.convertValue(employee2, EmployeesResponse.class)).thenReturn(
                EmployeesResponse.builder()
                        .personalNumber("22222")
                        .firstName("Борис")
                        .lastName("Кузнецов")
                        .email("boris@example.com")
                        .phone("2222222222")
                        .role(Roles.DIRECTOR)
                        .build());

        List<EmployeesResponse> result = employeesService.getAllEmployees();

        assertEquals(2, result.size());
        assertEquals("11111", result.get(0).getPersonalNumber());
        assertEquals("22222", result.get(1).getPersonalNumber());
    }

    @Test
    @DisplayName("Негативный сценарий — список сотрудников пуст")
    public void testGetAllEmployeesEmpty() {
        when(employeesRepository.findAll()).thenReturn(List.of());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                employeesService.getAllEmployees());

        assertEquals("Список сотрудников пуст", exception.getMessage());
    }

    @Test
    @DisplayName("Негативный сценарий — сотрудник не найден")
    public void testGetEmployeeNotFound() {
        String personalNumber = "99999";
        when(employeesRepository.findByPersonalNumber(personalNumber)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                employeesService.getEmployeeByPersonalNumber(personalNumber));

        assertEquals("Сотрудник с табельным номером 99999 не найден", exception.getMessage());
    }
}
