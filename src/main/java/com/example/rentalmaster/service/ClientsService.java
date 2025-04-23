package com.example.rentalmaster.service;

import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.request.ClientsUpdateRequest;
import com.example.rentalmaster.model.dto.response.ClientsInfoResponse;
import com.example.rentalmaster.model.dto.response.ClientsResponse;

import java.util.List;

public interface ClientsService {
    ClientsResponse addClient(ClientsRequest clientsRequest);

    ClientsResponse updateClient(String inn, ClientsUpdateRequest clientsRequest);

    ClientsResponse deleteClient(String inn);

    List<ClientsResponse> getAllClients();

    ClientsInfoResponse getInofClient(String inn);

    Clients getClient(String inn);
}