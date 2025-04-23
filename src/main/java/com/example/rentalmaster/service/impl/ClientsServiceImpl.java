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
        log.info("Создание новой карточки на клиента:{}", clientsRequest.getNameOfOrganization());
        validateClientNotExists(clientsRequest.getInn(), clientsRequest.getNameOfOrganization());
        Clients clients = objectMapper.convertValue(clientsRequest, Clients.class);
        Clients savedClient = clientsRepository.save(clients);
        log.info("Клиент:{} добавлень в бд", savedClient.getNameOfOrganization());
        ClientsResponse response = objectMapper.convertValue(savedClient, ClientsResponse.class);
        response.setMessage("Компания " + savedClient.getNameOfOrganization() + " ,инн " +
                savedClient.getInn() + " успешно создан");
        log.info("Клиент:{} успешно добавлен", savedClient.getNameOfOrganization());
        return response;
    }

    @Override
    public ClientsResponse updateClient(String inn, ClientsUpdateRequest clientsRequest) {
        log.info("Обнавление данных клиента:{}", clientsRequest.getNameOfOrganization());
        Clients client = validateClientNoFound(inn);
        updateClientInfo(client, clientsRequest);
        Clients updatedClient = clientsRepository.save(client);
        ClientsResponse response = objectMapper.convertValue(updatedClient, ClientsResponse.class);
        response.setMessage("Данные компании " + updatedClient.getNameOfOrganization() + " успешно обновлены");
        log.info("Данные клиента:{} обновлены", updatedClient.getNameOfOrganization());
        return response;
    }

    @Override
    public ClientsResponse deleteClient(String inn) {
        log.info("Удаление клиента с инн:{}", inn);
        Clients clients = validateClientNoFound(inn);
        clientsRepository.delete(clients);
        ClientsResponse response = objectMapper.convertValue(clients, ClientsResponse.class);
        response.setMessage("Компания " + clients.getNameOfOrganization() + " успешно удалена");
        log.info("Клиент {} удалён", clients.getNameOfOrganization());
        return response;
    }

    @Override
    public List<ClientsResponse> getAllClients() {
        log.info("Запрос списка всех клиентов");
        List<Clients> clients = clientsRepository.findAll();
        validateClientNoEmpty(clients);
        log.info("Всего клиентов найдено:{}", clients.size());
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
        log.info("Запрос информации о клиенте с инн:{}", inn);
        Clients clients = validateClientNoFound(inn);
        log.info("Запрос информации о клиенте с инн:{} успешно выполнен", inn);
        return buildClientResponse(clients);
    }

    @Override
    public Clients getClient(String inn) {
        return validateClientNoFound(inn);
    }

    private void validateClientNotExists(String inn, String nameOfOrganization) {
        clientsRepository.findByInn(inn).ifPresent(driver -> {
            throw new CommonBackendException("Компания " + nameOfOrganization + " ,с инн "
                    + inn + " уже существует", HttpStatus.CONFLICT);
        });
    }

    private Clients validateClientNoFound(String inn) {
        return clientsRepository.findByInn(inn)
                .orElseThrow(() -> new CommonBackendException("Компания с  инн "
                        + inn + " не найден", HttpStatus.NOT_FOUND));
    }

    private void updateClientInfo(Clients client, ClientsUpdateRequest clientsRequest) {
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
    }

    private void validateClientNoEmpty(List<Clients> clients) {
        if (clients.isEmpty()) {
            throw new CommonBackendException("Списко компании пуст", HttpStatus.NOT_FOUND);
        }
    }

    private ClientsInfoResponse buildClientResponse(Clients clients) {
        return ClientsInfoResponse.builder()
                .message("Данные о компании " + clients.getNameOfOrganization())
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
