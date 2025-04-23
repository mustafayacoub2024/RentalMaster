package com.example.rentalmaster.service;


import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.dto.request.DriversRequest;
import com.example.rentalmaster.model.dto.response.DriverInfoResponse;
import com.example.rentalmaster.model.dto.response.DriverResponse;

import java.util.List;

public interface DriversService {

    DriverResponse addDriver(DriversRequest driversRequest);

    DriverResponse deleteDriver(String personalNumber);

    DriverResponse updateDriver(String personalNumber, DriversRequest driversRequest);

    DriverInfoResponse getInfoDriver(String personalNumber);

    List<DriverResponse> getAllDrivers();

    List<Drivers> getAll();
}