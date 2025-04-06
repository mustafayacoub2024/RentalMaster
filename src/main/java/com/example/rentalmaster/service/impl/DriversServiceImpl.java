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

        driversRepository.findByPersonalNumber(driversRequest.getPersonalNumber()).ifPresent(driver -> {
            throw new CommonBackendException("Водитель с табельным номером "
                    + driversRequest.getPersonalNumber() + " уже существует", HttpStatus.CONFLICT);
        });

        Drivers drivers = objectMapper.convertValue(driversRequest, Drivers.class);
        Drivers savedDriver = driversRepository.save(drivers);

        DriverResponse response = objectMapper.convertValue(savedDriver, DriverResponse.class);
        response.setMessage("Водитель с табельным номером " + savedDriver.getPersonalNumber() + " успешно создан");
        return response;
    }

    @Override
    public DriverResponse deleteDriver(String personalNumber) {
        Drivers driver = driversRepository.findByPersonalNumber(personalNumber)
                .orElseThrow(() -> new CommonBackendException("Водитель с табельным номером "
                        + personalNumber + " не найден", HttpStatus.NOT_FOUND));

        driversRepository.delete(driver);

        DriverResponse response = objectMapper.convertValue(driver, DriverResponse.class);
        response.setMessage("Водитель с табельным номером " + driver.getPersonalNumber() + " успешно удалён");
        return response;
    }

    @Override
    public DriverResponse updateDriver(String personalNumber, DriversRequest driversRequest) {
        Drivers driver = driversRepository.findByPersonalNumber(personalNumber)
                .orElseThrow(() -> new CommonBackendException("Водитель с табельным номером "
                        + personalNumber + " не найден", HttpStatus.NOT_FOUND));

        driver.setEmail(driversRequest.getEmail());
        driver.setPhone(driversRequest.getPhone());
        driver.setSalary(driversRequest.getSalary());
        driver.setFirstName(driversRequest.getFirstName());
        driver.setLastName(driversRequest.getLastName());
        Drivers drivers = driversRepository.save(driver);

        DriverResponse response = objectMapper.convertValue(drivers, DriverResponse.class);
        response.setMessage("Данные компании " + drivers.getPersonalNumber() + " успешно обновлены");
        return response;

    }

    @Override
    public DriverInfoResponse getInfoDriver(String personalNumber) {
        Drivers driver = driversRepository.findByPersonalNumber(personalNumber)
                .orElseThrow(() -> new CommonBackendException("Водитель с табельным номером "
                        + personalNumber + " не найден", HttpStatus.NOT_FOUND));
        return DriverInfoResponse.builder()
                .message("Данные о водителе с табельным номером " + personalNumber)
                .personalNumber(driver.getPersonalNumber())
                .email(driver.getEmail())
                .phone(driver.getPhone())
                .lastName(driver.getLastName())
                .firstName(driver.getFirstName())
                .salary(driver.getSalary())
                .build();

    }

    @Override
    public List<DriverResponse> getAllDrivers() {
        List<Drivers> drivers = driversRepository.findAll();

        if (drivers.isEmpty()) {
            throw new CommonBackendException("Список водителлей пуст", HttpStatus.NOT_FOUND);
        }
        return drivers.stream()
                .map(driver -> {
                    DriverResponse response = objectMapper.convertValue(driver, DriverResponse.class);
                    response.setMessage("Водителть "+driver.getLastName()+" "+driver.getFirstName()+" ,табельный номер " + driver.getPersonalNumber());
                    return response;
                }).toList();
    }
}
