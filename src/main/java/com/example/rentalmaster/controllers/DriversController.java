package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.DriversRequest;
import com.example.rentalmaster.model.dto.response.DriverInfoResponse;
import com.example.rentalmaster.model.dto.response.DriverResponse;
import com.example.rentalmaster.service.DriversService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriversController {

    private final DriversService driversService;

    @PostMapping
    @Operation(summary = "Создать водителя")
    public DriverResponse addDriver(@RequestBody @Valid DriversRequest driversRequest) {
        return driversService.addDriver(driversRequest);

    }

    @DeleteMapping("/{personal_number}")
    @Operation(summary = "Удалить водителя")
    public DriverResponse deleteDriver(@PathVariable("personal_number") String personalNumber) {
        return driversService.deleteDriver(personalNumber);

    }

    @PutMapping("/{personal_number}")
    @Operation(summary = "Изменить данные водителя")
    public DriverResponse updateDriver(@PathVariable("personal_number") String personalNumber,
                                       @RequestBody DriversRequest driversRequest) {
        return driversService.updateDriver(personalNumber, driversRequest);

    }

    @GetMapping("/{personal_number}")
    @Operation(summary = "Данные о водителе")
    public DriverInfoResponse getDriver(@PathVariable("personal_number") String personalNumber) {
        return driversService.getInfoDriver(personalNumber);

    }

    @GetMapping()
    @Operation(summary = "Список всех водителей")
    public List<DriverResponse> getAllDrivers() {
        return driversService.getAllDrivers();

    }
}
