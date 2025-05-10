package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.repository.ClientsRepository;
import com.example.rentalmaster.model.db.repository.DriversRepository;
import com.example.rentalmaster.model.dto.request.DriversRequest;
import com.example.rentalmaster.model.dto.response.DriverResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static javax.management.Query.eq;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DriversServiceImplTest {

    @InjectMocks
    private DriversServiceImpl driversService;

    @Mock
    private DriversRepository driversRepository;

    @Spy
    private ObjectMapper objectMapper;



    @Test
    @DisplayName(" Успешное добавление нового водителя")
    public void testAddDriverSuccess() {

       var driversRequest = DriversRequest.builder()
                .personalNumber("123")
                .firstName("Амир")
                .lastName("Якуб")
                .email("ivan@example.com")
                .phone("1234567890")
                .salary(500.0)
                .build();

        var driverEntity = Drivers.builder()
                .personalNumber("123")
                .firstName("Амир")
                .lastName("Якуб")
                .email("ivan@example.com")
                .phone("1234567890")
                .salary(500.0)
                .build();

        when(driversRepository.findByPersonalNumber("123")).thenReturn(Optional.empty());
        when(objectMapper.convertValue(driversRequest, Drivers.class)).thenReturn(driverEntity);
        when(driversRepository.save(driverEntity)).thenReturn(driverEntity);
        when(objectMapper.convertValue(driverEntity, DriverResponse.class))
                .thenReturn(new DriverResponse());

        DriverResponse response = driversService.addDriver(driversRequest);

        assertNotNull(response);
        assertTrue(response.getMessage().contains("успешно создан"));
        verify(driversRepository).save(driverEntity);
    }

    @Test
    @DisplayName("Ошибка при добавлении водителя — табельный номер уже существует")
    public void testAddDriverAlreadyExists() {
        var driversRequest = DriversRequest.builder()
                .personalNumber("123")
                .firstName("Амир")
                .lastName("Якуб")
                .email("ivan@example.com")
                .phone("1234567890")
                .salary(500.0)
                .build();

        var driverEntity = Drivers.builder()
                .personalNumber("123")
                .firstName("Амир")
                .lastName("Якуб")
                .email("ivan@example.com")
                .phone("1234567890")
                .salary(500.0)
                .build();

        when(driversRepository.findByPersonalNumber("123")).thenReturn(Optional.of(driverEntity));

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> driversService.addDriver(driversRequest));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertTrue(exception.getMessage().contains("уже существует"));
        verify(driversRepository, never()).save(any());
    }


    @Test
    @DisplayName("Успешное удаление водителя")
    public void testDeleteDriverSuccess() {
        var driver = Drivers.builder()
                .personalNumber("123")
                .firstName("Амир")
                .lastName("Якуб")
                .build();

        when(driversRepository.findByPersonalNumber("123")).thenReturn(Optional.of(driver));
        when(objectMapper.convertValue(driver, DriverResponse.class)).thenReturn(new DriverResponse());

        DriverResponse response = driversService.deleteDriver("123");

        assertNotNull(response);
        assertTrue(response.getMessage().contains("успешно удалён"));
        verify(driversRepository).delete(driver);
    }

    @Test
    @DisplayName("Ошибка при удалении — водитель не найден")
    public void testDeleteDriverNotFound() {
        when(driversRepository.findByPersonalNumber("999")).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> driversService.deleteDriver("999"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("не найден"));
        verify(driversRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Успешное обновление водителя")
    public void testUpdateDriverSuccess() {
        var existingDriver = Drivers.builder()
                .personalNumber("123")
                .firstName("Иван")
                .lastName("Сидоров")
                .build();

        var request = DriversRequest.builder()
                .personalNumber("123")
                .firstName("Новый")
                .lastName("Фамилия")
                .email("new@mail.com")
                .phone("88888888")
                .salary(777.0)
                .build();

        when(driversRepository.findByPersonalNumber("123")).thenReturn(Optional.of(existingDriver));
        when(driversRepository.save(existingDriver)).thenReturn(existingDriver);
        when(objectMapper.convertValue(existingDriver, DriverResponse.class)).thenReturn(new DriverResponse());

        DriverResponse response = driversService.updateDriver("123", request);

        assertNotNull(response);
        assertTrue(response.getMessage().contains("успешно обновлены"));
        verify(driversRepository).save(existingDriver);
    }

    @Test
    @DisplayName("Ошибка при обновлении — водитель не найден")
    public void testUpdateDriverNotFound() {
        var request = DriversRequest.builder().personalNumber("123").build();
        when(driversRepository.findByPersonalNumber("123")).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> driversService.updateDriver("123", request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("не найден"));
    }

    @Test
    @DisplayName("Успешный запрос информации о водителе")
    public void testGetInfoDriverSuccess() {
        var driver = Drivers.builder()
                .personalNumber("123")
                .firstName("Амир")
                .lastName("Якуб")
                .email("amir@mail.com")
                .phone("9999999")
                .salary(1000.0)
                .build();

        when(driversRepository.findByPersonalNumber("123")).thenReturn(Optional.of(driver));

        var response = driversService.getInfoDriver("123");

        assertNotNull(response);
        assertEquals("123", response.getPersonalNumber());
        assertEquals("Амир", response.getFirstName());
    }

    @Test
    @DisplayName("Ошибка при запросе информации — водитель не найден")
    public void testGetInfoDriverNotFound() {
        when(driversRepository.findByPersonalNumber("123")).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> driversService.getInfoDriver("123"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("не найден"));
    }

    @Test
    @DisplayName("Успешный запрос всех водителей")
    public void testGetAllDriversSuccess() {
        var driver1 = Drivers.builder()
                .personalNumber("123")
                .firstName("Амир")
                .lastName("Якуб")
                .build();

        var driver2 = Drivers.builder()
                .personalNumber("456")
                .firstName("Иван")
                .lastName("Петров")
                .build();


        var driverList = List.of(driver1, driver2);

        var response1 = new DriverResponse();
        response1.setMessage("Водителть Якуб Амир ,табельный номер 123");

        var response2 = new DriverResponse();
        response2.setMessage("Водителть Петров Иван ,табельный номер 456");

        when(driversRepository.findAll()).thenReturn(driverList);

        when(objectMapper.convertValue(driver1, DriverResponse.class)).thenReturn(response1);
        when(objectMapper.convertValue(driver2, DriverResponse.class)).thenReturn(response2);

        var responseList = driversService.getAllDrivers();

        assertEquals(2, responseList.size());
        assertTrue(responseList.get(0).getMessage().contains("123"));
        assertTrue(responseList.get(1).getMessage().contains("456"));

    }

    @Test
    @DisplayName("Ошибка при запросе всех водителей — список пуст")
    public void testGetAllDriversEmpty() {
        when(driversRepository.findAll()).thenReturn(List.of());

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> driversService.getAllDrivers());

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("пуст"));
    }

    @Test
    @DisplayName("Успешный вызов метода getAll()")
    public void testGetAll() {
        var driverList = List.of(Drivers.builder().personalNumber("123").build());
        when(driversRepository.findAll()).thenReturn(driverList);

        List<Drivers> result = driversService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Обновление полей водителя из запроса")
    public void testUpdateDriverInfo() {
        var driver = Drivers.builder().build();
        var request = DriversRequest.builder()
                .firstName("Анна")
                .lastName("Смирнова")
                .email("anna@mail.com")
                .phone("111222333")
                .salary(900.0)
                .build();

        driversService.updateDriverInfo(driver, request);

        assertEquals("Анна", driver.getFirstName());
        assertEquals("Смирнова", driver.getLastName());
        assertEquals("anna@mail.com", driver.getEmail());
        assertEquals("111222333", driver.getPhone());
        assertEquals(Double.valueOf(900.0), driver.getSalary());
    }
}