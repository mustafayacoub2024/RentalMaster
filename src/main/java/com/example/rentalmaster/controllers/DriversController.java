package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.DriversRequest;
import com.example.rentalmaster.model.dto.response.DriverInfoResponse;
import com.example.rentalmaster.model.dto.response.DriverResponse;
import com.example.rentalmaster.service.DriversService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
//    } @PostMapping
//    @Operation(summary = "Создать водителя")
//    public ResponseEntity<DriverResponse> addDriver(@RequestBody @Valid DriversRequest driversRequest) {
//        DriverResponse driverResponse = driversService.addDriver(driversRequest);
//        return ResponseEntity.ok(driverResponse);
//
//    }

    @DeleteMapping("/{personal_number}")
    @Operation(summary = "Удалить водителя")
    public ResponseEntity<DriverResponse> deleteDriver(@PathVariable("personal_number") String personalNumber) {
        DriverResponse driverResponse = driversService.deleteDriver(personalNumber);
        return ResponseEntity.ok(driverResponse);

    }

    @PutMapping("/{personal_number}")
    @Operation(summary = "Изменить данные водителя")
    public ResponseEntity<DriverResponse> updateDriver(@PathVariable("personal_number") String personalNumber,
                                                       @RequestBody DriversRequest driversRequest) {
        DriverResponse driverResponse = driversService.updateDriver(personalNumber, driversRequest);
        return ResponseEntity.ok(driverResponse);

    }

    @GetMapping("/{personal_number}")
    @Operation(summary = "Данные о водителе")
    public ResponseEntity<DriverInfoResponse> getDriver(@PathVariable("personal_number") String personalNumber) {
        DriverInfoResponse driverResponse = driversService.getInfoDriver(personalNumber);
        return ResponseEntity.ok(driverResponse);

    }

    @GetMapping()
    @Operation(summary = "Список всех водителей")
        public ResponseEntity<List<DriverResponse>> getAllDrivers() {
       List<DriverResponse>  driverResponse = driversService.getAllDrivers();
        return ResponseEntity.ok(driverResponse);
    }

}
