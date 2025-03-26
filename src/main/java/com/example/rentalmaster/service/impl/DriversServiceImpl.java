package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.repository.DriversRepository;
import com.example.rentalmaster.model.dto.request.DriversRequest;
import com.example.rentalmaster.model.dto.response.DriverResponse;
import com.example.rentalmaster.service.DriversService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriversServiceImpl implements DriversService {

    private final DriversRepository driversRepository;
    private final ObjectMapper objectMapper;

    @Override
    public DriverResponse addDriver(DriversRequest driversRequest) {

        driversRepository.findByPersonalNumber(driversRequest.getPersonalNumber()).ifPresent(driver -> {
            throw new RuntimeException("Водитель с табельным номером "
                    + driversRequest.getPersonalNumber() + " уже существует");
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
                .orElseThrow(() -> new RuntimeException("Водитель с табельным номером "
                        + personalNumber + " не найден"));

        driversRepository.delete(driver);

        DriverResponse response = new DriverResponse();
        response.setMessage("Водитель с табельным номером " + driver.getPersonalNumber() + " успешно удалён");
        return response;
    }
}

