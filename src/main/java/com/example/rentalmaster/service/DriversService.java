package com.example.rentalmaster.service;


import com.example.rentalmaster.model.dto.request.DriversRequest;
import com.example.rentalmaster.model.dto.response.DriverResponse;

public interface DriversService {
    DriverResponse addDriver(DriversRequest driversRequest);


}
