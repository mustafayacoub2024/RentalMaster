package com.example.rentalmaster.service;

import com.example.rentalmaster.model.dto.request.EmployeesRequest;
import com.example.rentalmaster.model.dto.response.EmployeesResponse;

public interface EmployeesService {
    EmployeesResponse addClient(EmployeesRequest employeesResponse);

    EmployeesResponse updateClient(String personalNumber, EmployeesRequest employeesResponse);

    EmployeesResponse deleteClient(String personalNumber);
}
