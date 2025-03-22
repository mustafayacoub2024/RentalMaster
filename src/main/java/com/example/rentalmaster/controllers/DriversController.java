package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.DriversRequest;
import com.example.rentalmaster.model.dto.response.DriverResponse;
import com.example.rentalmaster.service.DriversService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriversController {

    private final DriversService driversService;

    @PostMapping
    @Operation(summary = "Создать водителя")
    public DriverResponse addDriver(@RequestBody @Valid DriversRequest driversRequest) {
        try {
            return driversService.addDriver(driversRequest);
        } catch (RuntimeException e) {
            DriverResponse driverResponse = new DriverResponse();
            driverResponse.setMessage(e.getMessage());
            return driverResponse;
        }
    }
}
