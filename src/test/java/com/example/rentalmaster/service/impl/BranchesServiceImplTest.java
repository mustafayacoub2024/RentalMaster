package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.model.enums.Roles;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.model.db.repository.BranchesRepository;
import com.example.rentalmaster.model.dto.request.BranchesRequest;
import com.example.rentalmaster.model.dto.request.BranchesRequestUpdate;
import com.example.rentalmaster.model.dto.response.BranchesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BranchesServiceImplTest {

    @Mock
    private BranchesRepository branchesRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private BranchesServiceImpl service;

    @Test
    @DisplayName("Успешное добавление нового филиала")
    public void testAddBranchSuccess() {
        Employees employee = Employees.builder()
                .personalNumber("12345")
                .lastName("Иванов")
                .firstName("Иван")
                .email("ivanov@test.ru")
                .phone("987654321")
                .role(Roles.MANAGER)
                .build();

        BranchesRequest request = BranchesRequest.builder()
                .branchName("Москва")
                .city("Москва")
                .email("moscow@test.ru")
                .phone("123456789")
                .address("ул. Тестовая, 1")
                .coefficient(1.2)
                .employees(employee)
                .build();

        Branches branch = Branches.builder()
                .branchName("Москва")
                .city("Москва")
                .email("moscow@test.ru")
                .phone("123456789")
                .address("ул. Тестовая, 1")
                .coefficient(1.2)
                .employees(employee)
                .build();

        BranchesResponse expectedResponse = BranchesResponse.builder()
                .branchName("Москва")
                .message("Филиал Москва успешно создан")
                .build();

        when(branchesRepository.findByBranchName("Москва")).thenReturn(Optional.empty());
        when(branchesRepository.save(any(Branches.class))).thenReturn(branch);
        when(objectMapper.convertValue(branch, BranchesResponse.class)).thenReturn(expectedResponse);
        when(objectMapper.convertValue(request, Branches.class)).thenReturn(branch);

        BranchesResponse response = service.addBranches(request);

        assertNotNull(response);
        assertEquals("Москва", response.getBranchName());
        assertEquals("Филиал Москва успешно создан", response.getMessage());

        verify(branchesRepository).findByBranchName("Москва");
        verify(branchesRepository).save(any(Branches.class));
        verify(objectMapper).convertValue(request, Branches.class);
        verify(objectMapper).convertValue(branch, BranchesResponse.class);
    }

    @Test
    @DisplayName("Ошибка при добавлении филиала с уже существующим названием")
    public void testAddBranchConflict() {
        BranchesRequest request = BranchesRequest.builder().branchName("Москва").build();
        when(branchesRepository.findByBranchName("Москва")).thenReturn(Optional.of(new Branches()));

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> service.addBranches(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertTrue(exception.getMessage().contains("уже существует"));
    }

    @Test
    @DisplayName("Успешное обновление филиала")
    public void testUpdateBranchSuccess() {
        Employees employee = Employees.builder()
                .personalNumber("12345")
                .lastName("Иванов")
                .firstName("Иван")
                .email("ivanov@test.ru")
                .phone("987654321")
                .role(Roles.MANAGER)
                .build();

        Branches existingBranch = Branches.builder()
                .branchName("Москва")
                .city("Москва")
                .email("old@test.ru")
                .phone("111111111")
                .address("ул. Старая")
                .coefficient(1.0)
                .employees(employee)
                .build();

        BranchesRequestUpdate updateRequest = BranchesRequestUpdate.builder()
                .branchName("Москва")
                .city("Новосибирск")
                .address("ул. Новая")
                .email("new@test.ru")
                .phone("999999999")
                .coefficient(1.1)
                .employees(employee)
                .build();

        when(branchesRepository.findByBranchName("Москва"))
                .thenReturn(Optional.of(existingBranch));

        when(branchesRepository.save(any(Branches.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BranchesResponse expectedResponse = new BranchesResponse();
        expectedResponse.setBranchName("Москва");
        expectedResponse.setMessage("Филиал с id  Москва успешно изменён");

        when(objectMapper.convertValue(any(Branches.class), eq(BranchesResponse.class)))
                .thenReturn(expectedResponse);

        BranchesResponse result = service.updateBranches("Москва", updateRequest);

        assertNotNull(result);
        assertEquals("Москва", result.getBranchName());
        assertEquals("Филиал с id  Москва успешно изменён", result.getMessage());

        ArgumentCaptor<Branches> branchCaptor = ArgumentCaptor.forClass(Branches.class);
        verify(branchesRepository).save(branchCaptor.capture());

        Branches savedBranch = branchCaptor.getValue();
        assertEquals("Новосибирск", savedBranch.getCity());
        assertEquals("ул. Новая", savedBranch.getAddress());
        assertEquals(1.1, savedBranch.getCoefficient(), 0.001);
    }

    @Test
    @DisplayName("Ошибка при обновлении несуществующего филиала")
    public void testUpdateBranchNotFound() {
        when(branchesRepository.findByBranchName("СПб")).thenReturn(Optional.empty());

        BranchesRequestUpdate update = BranchesRequestUpdate.builder().branchName("СПб").build();

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> service.updateBranches("СПб", update));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("не найден"));
    }

    @Test
    @DisplayName("Получение списка всех филиалов")
    public void testGetAllBranchesSuccess() {
        List<Branches> list = List.of(
                Branches.builder().branchName("Москва").city("Москва").build(),
                Branches.builder().branchName("СПб").city("Санкт-Петербург").build()
        );
        when(branchesRepository.findAll()).thenReturn(list);

        var response = service.getAllBranches();
        assertEquals(2, response.size());
        assertTrue(response.get(0).getMessage().contains("Найден филиал"));
    }

    @Test
    @DisplayName("Ошибка при получении списка филиалов, если список пуст")
    public void testGetAllBranchesEmpty() {
        when(branchesRepository.findAll()).thenReturn(new ArrayList<>());

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                service::getAllBranches);

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Филиалы отсутствуют"));
    }

    @Test
    @DisplayName("Успешное удаление филиала")
    public void testDeleteBranchSuccess() {
        Branches branch = Branches.builder()
                .branchName("Москва")
                .city("Москва")
                .build();

        when(branchesRepository.findByBranchName("Москва"))
                .thenReturn(Optional.of(branch));

        BranchesResponse mockResponse = new BranchesResponse();
        mockResponse.setBranchName("Москва");
        when(objectMapper.convertValue(any(Branches.class), eq(BranchesResponse.class)))
                .thenReturn(mockResponse);

        BranchesResponse response = service.deleteBranch("Москва");

        verify(branchesRepository, times(1)).delete(branch);
        assertNotNull(response);
        assertEquals("Москва", response.getBranchName());
        assertTrue(response.getMessage().contains("успешно удалён"));
    }

    @Test
    @DisplayName("Ошибка при удалении несуществующего филиала")
    public void testDeleteBranchNotFound() {
        when(branchesRepository.findByBranchName("Казань")).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> service.deleteBranch("Казань"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("не найден"));
    }
}
