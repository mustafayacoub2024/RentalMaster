package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.DriversRequest;
import com.example.rentalmaster.model.dto.response.DriverResponse;
import com.example.rentalmaster.service.DriversService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;


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

    @DeleteMapping("/{personal_number}")
    @Operation(summary = "Удалить водителя")
    public DriverResponse deleteDriver(@PathVariable("personal_number") String personalNumber) {
        try {
            return driversService.deleteDriver(personalNumber);
        } catch (RuntimeException e) {
            DriverResponse driverResponse = new DriverResponse();
            driverResponse.setMessage(e.getMessage());
            return driverResponse;
        }
    }
}
