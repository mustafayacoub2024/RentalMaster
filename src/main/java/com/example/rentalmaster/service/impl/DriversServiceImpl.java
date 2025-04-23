package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.repository.DriversRepository;
import com.example.rentalmaster.model.dto.request.DriversRequest;
import com.example.rentalmaster.model.dto.response.DriverInfoResponse;
import com.example.rentalmaster.model.dto.response.DriverResponse;
import com.example.rentalmaster.service.DriversService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriversServiceImpl implements DriversService {

    private final DriversRepository driversRepository;
    private final ObjectMapper objectMapper;

    @Override
    public DriverResponse addDriver(DriversRequest driversRequest) {
        log.info("Создание карточке на нового водителя c табельным номером: {}", driversRequest.getPersonalNumber());
        validateDriverNotExists(driversRequest.getPersonalNumber());
        Drivers drivers = objectMapper.convertValue(driversRequest, Drivers.class);
        Drivers savedDriver = driversRepository.save(drivers);
        DriverResponse response = objectMapper.convertValue(savedDriver, DriverResponse.class);
        response.setMessage("Водитель с табельным номером " + savedDriver.getPersonalNumber() + " успешно создан");
        log.info("Водитель c табельным номером: {} успешно добавлен", driversRequest.getPersonalNumber());
        return response;
    }

    @Override
    public DriverResponse deleteDriver(String personalNumber) {
        log.info("Удаление карточки водителя с табельным номером: {}", personalNumber);
        Drivers driver = validateDriverNoFound(personalNumber);
        driversRepository.delete(driver);
        DriverResponse response = objectMapper.convertValue(driver, DriverResponse.class);
        response.setMessage("Водитель с табельным номером " + driver.getPersonalNumber() + " успешно удалён");
        log.info("Карточка водителя с табельным номером: {} успешна удалена", personalNumber);
        return response;
    }

    @Override
    public DriverResponse updateDriver(String personalNumber, DriversRequest driversRequest) {
        log.info("Обновление данных водитлея с табельным номером:{}", personalNumber);
        Drivers driver = validateDriverNoFound(personalNumber);
        updateDriverInfo(driver, driversRequest);
        DriverResponse response = objectMapper.convertValue(driver, DriverResponse.class);
        Drivers drivers = driversRepository.save(driver);
        response.setMessage("Данные компании " + driver.getPersonalNumber() + " успешно обновлены");
        log.info("Данные водителя с табельным номером: {} успешно обновлены", personalNumber);
        return response;

    }

    @Override
    public DriverInfoResponse getInfoDriver(String personalNumber) {
        log.info("Запрос информации о водителе с табельным номером: {}", personalNumber);
        Drivers driver = validateDriverNoFound(personalNumber);
        log.info("Запрос данных о водителя с табельным номером: {} успешно получены", driver.getPersonalNumber());
        return buildDriverResponse(driver, personalNumber);

    }

    @Override
    public List<DriverResponse> getAllDrivers() {
        log.info("Запрос списка всех водителей");
        List<Drivers> drivers = driversRepository.findAll();
        validateDriverNoEmpty(drivers);
        log.info("Всего водителей в списке:{}", drivers.size());
        return drivers.stream()
                .map(driver -> {
                    DriverResponse response = objectMapper.convertValue(driver, DriverResponse.class);
                    response.setMessage("Водителть " + driver.getLastName() + " " + driver.getFirstName() + " ,табельный номер " + driver.getPersonalNumber());
                    return response;
                }).toList();
    }

    @Override
    public List<Drivers> getAll() {
        return driversRepository.findAll();
    }

    private void validateDriverNotExists(String personalNumber) {
        driversRepository.findByPersonalNumber(personalNumber).ifPresent(driver -> {
            throw new CommonBackendException("Водитель с табельным номером "
                    + personalNumber + " уже существует", HttpStatus.CONFLICT);
        });
    }

    private Drivers validateDriverNoFound(String personalNumber) {
        return driversRepository.findByPersonalNumber(personalNumber)
                .orElseThrow(() -> new CommonBackendException("Водитель с табельным номером "
                        + personalNumber + " не найден", HttpStatus.NOT_FOUND));
    }

    public void updateDriverInfo(Drivers driver, DriversRequest driversRequest){
        driver.setEmail(driversRequest.getEmail());
        driver.setPhone(driversRequest.getPhone());
        driver.setSalary(driversRequest.getSalary());
        driver.setFirstName(driversRequest.getFirstName());
        driver.setLastName(driversRequest.getLastName());

    }

    private DriverInfoResponse buildDriverResponse(Drivers driver, String personalNumber){
        return  DriverInfoResponse.builder()
                .message("Данные о водителе с табельным номером " + personalNumber)
                .personalNumber(driver.getPersonalNumber())
                .email(driver.getEmail())
                .phone(driver.getPhone())
                .lastName(driver.getLastName())
                .firstName(driver.getFirstName())
                .salary(driver.getSalary())
                .build();
    }

    private void validateDriverNoEmpty(List<Drivers> driver){
        if (driver.isEmpty()) {
            throw new CommonBackendException("Список водителлей пуст", HttpStatus.NOT_FOUND);
        }
    }
}
