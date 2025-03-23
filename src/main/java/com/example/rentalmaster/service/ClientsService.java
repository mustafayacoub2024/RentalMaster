package com.example.rentalmaster.service;

import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.response.ClientsResponse;

import java.util.UUID;

public interface ClientsService {
    ClientsResponse addClient(ClientsRequest clientsRequest);

    ClientsResponse updateClient(String inn, ClientsRequest clientsRequest);

    ClientsResponse deleteClient(String inn);
}
