package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.db.repository.ClientsRepository;
import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.request.ClientsUpdateRequest;
import com.example.rentalmaster.model.dto.response.ClientsInfoResponse;
import com.example.rentalmaster.model.dto.response.ClientsResponse;
import com.example.rentalmaster.service.ClientsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientsServiceImpl implements ClientsService {

    private final ClientsRepository clientsRepository;
    private final ObjectMapper objectMapper;

    @Override
    public ClientsResponse addClient(ClientsRequest clientsRequest) {

        clientsRepository.findByInn(clientsRequest.getInn()).ifPresent(driver -> {
            throw new CommonBackendException("Компания "+ clientsRequest.getNameOfOrganization() + " ,с инн "
                    + clientsRequest.getInn() + " уже существует", HttpStatus.CONFLICT);
        });

        Clients clients = objectMapper.convertValue(clientsRequest, Clients.class);
        Clients savedClient = clientsRepository.save(clients);

        ClientsResponse response = objectMapper.convertValue(savedClient, ClientsResponse.class);
        response.setMessage("Компания "+ savedClient.getNameOfOrganization()+" ,инн " +
                savedClient.getInn() + " успешно создан");
        return response;
    }

    @Override
    public ClientsResponse updateClient(String inn, ClientsUpdateRequest clientsRequest) {
        Clients client = clientsRepository.findByInn(inn)
                .orElseThrow(() -> new CommonBackendException("Компания с  инн "
                        + inn + " не найден", HttpStatus.NOT_FOUND));

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
                .orElseThrow(() -> new CommonBackendException("Компания с  инн "
                        + inn + " не найден", HttpStatus.NOT_FOUND));

        clientsRepository.delete(clients);

        ClientsResponse response = objectMapper.convertValue(clients, ClientsResponse.class);
        response.setMessage("Компания " + clients.getNameOfOrganization() + " успешно удалена");
        return response;
    }

    @Override
    public List<ClientsResponse> getAllClients() {
        List<Clients> clients = clientsRepository.findAll();

        if (clients.isEmpty()) {
            throw new CommonBackendException("Списко компании пуст", HttpStatus.NOT_FOUND);
        }

        return clients.stream()
                .map(client -> {
                    ClientsResponse clientResponse = objectMapper.convertValue(client, ClientsResponse.class);
                    clientResponse.setMessage("Клиент " + client.getNameOfOrganization() + " ,инн " + client.getInn());
                    return clientResponse;
                })
                .toList();
    }

    @Override
    public ClientsInfoResponse getInofClient(String inn) {
        Clients clients = clientsRepository.findByInn(inn)
                .orElseThrow(() -> new CommonBackendException("Компания с  инн "
                        + inn + " не найден", HttpStatus.NOT_FOUND));

        return ClientsInfoResponse.builder()
                .message("Данные о компании "+clients.getNameOfOrganization())
                .inn(clients.getInn())
                .bik(clients.getBik())
                .actualAddress(clients.getActualAddress())
                .email(clients.getEmail())
                .kpp(clients.getKpp())
                .okato(clients.getOkato())
                .okpo(clients.getOkpo())
                .currentAccount(clients.getCurrentAccount())
                .correspondentAccount(clients.getCorrespondentAccount())
                .ogrn(clients.getOgrn())
                .phone(clients.getPhone())
                .generalManager(clients.getGeneralManager())
                .legalAddress(clients.getLegalAddress())
                .nameOfOrganization(clients.getNameOfOrganization())
                .okved(clients.getOkved())
                .build();
    }
}
