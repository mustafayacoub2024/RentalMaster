package com.example.rentalmaster.service;

import com.example.rentalmaster.model.dto.request.EmployeesRequest;
import com.example.rentalmaster.model.dto.response.EmployeesResponse;

public interface EmployeesService {
    EmployeesResponse addEmployee(EmployeesRequest employeesResponse);

    EmployeesResponse updateEmployee(String personalNumber, EmployeesRequest employeesResponse);

    EmployeesResponse deleteEmployee(String personalNumber);

    EmployeesResponse getEmployeeByPersonalNumber(String personalNumber);
}
