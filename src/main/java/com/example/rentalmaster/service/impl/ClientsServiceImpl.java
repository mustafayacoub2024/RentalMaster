package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.repository.ClientsRepository;
import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.response.ClientsResponse;
import com.example.rentalmaster.model.dto.response.DriverResponse;
import com.example.rentalmaster.service.ClientsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientsServiceImpl implements ClientsService {

    private final ClientsRepository clientsRepository;
    private final ObjectMapper objectMapper;

    @Override
    public ClientsResponse addClient(ClientsRequest clientsRequest) {

        clientsRepository.findByInn(clientsRequest.getInn()).ifPresent(driver -> {
            throw new RuntimeException("Компания  с инн "
                    + clientsRequest.getInn() + " уже существует");
        });

        Clients clients = objectMapper.convertValue(clientsRequest, Clients.class);
        Clients savedClient = clientsRepository.save(clients);

        ClientsResponse response = objectMapper.convertValue(savedClient, ClientsResponse.class);
        response.setMessage("Компания  с инн  " + savedClient.getInn() + " успешно создан");
        return response;
    }

    @Override
    public ClientsResponse updateClient(String inn, ClientsRequest clientsRequest) {
        Clients client = clientsRepository.findByInn(clientsRequest.getInn())
                .orElseThrow(() -> new RuntimeException("Компания с  инн "
                        + clientsRequest.getInn() + " не найден"));

        client.setActualAddress(clientsRequest.getActualAddress());
        client.setLegalAddress(clientsRequest.getLegalAddress());
        client.setKpp(clientsRequest.getKpp());
        client.setBik(clientsRequest.getBik());
        client.setCurrentAccount(clientsRequest.getCurrentAccount());
        client.setCorrespondentAccount(clientsRequest.getCorrespondentAccount());
        client.setOkpo(clientsRequest.getOkpo());
        client.setOkato(clientsRequest.getOkato());
        client.setOkved(clientsRequest.getOkved());
        client.setOgrn(clientsRequest.getOgrn());
        client.setGeneralManager(clientsRequest.getGeneralManager());
        client.setEmail(clientsRequest.getEmail());
        client.setPhone(clientsRequest.getPhone());
        client.setNameOfOrganization(clientsRequest.getNameOfOrganization());

        Clients updatedClient = clientsRepository.save(client);
        ClientsResponse response = objectMapper.convertValue(updatedClient, ClientsResponse.class);
        response.setMessage("Данные компании " + updatedClient.getNameOfOrganization() + " успешно обновлены");
        return response;
    }

    @Override
    public ClientsResponse deleteClient(String inn) {
        Clients clients = clientsRepository.findByInn(inn)
                .orElseThrow(() -> new RuntimeException("Компания с  инн "
                        + inn + " не найден"));

        clientsRepository.delete(clients);

        ClientsResponse response = new ClientsResponse();
        response.setMessage("Компания " + clients.getNameOfOrganization() + " успешно удалена");
        return response;
    }
}
