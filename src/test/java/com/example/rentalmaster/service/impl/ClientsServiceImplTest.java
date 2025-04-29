package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.db.repository.ClientsRepository;
import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.request.ClientsUpdateRequest;
import com.example.rentalmaster.model.dto.response.ClientsInfoResponse;
import com.example.rentalmaster.model.dto.response.ClientsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientsServiceImplTest {

    @InjectMocks
    private ClientsServiceImpl clientsService;

    @Mock
    private ClientsRepository clientsRepository;

    @Spy
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Позитивный тест на создание карточке на клиента")
    public void addClientPositiveTest() {
        ClientsRequest request = ClientsRequest.builder()
                .nameOfOrganization("TestOrg")
                .legalAddress("Legal Address")
                .actualAddress("Actual Address")
                .inn("123456789012")
                .kpp("123456789")
                .bik("044525999")
                .currentAccount("40817810000000000001")
                .correspondentAccount("30101810200000000999")
                .okpo("12345678")
                .okato("12345678901")
                .okved("62.01")
                .ogrn("1234567890123")
                .generalManager("Иванов И.И.")
                .email("test@example.com")
                .phone("+79990000000")
                .build();

        Clients client = Clients.builder()
                .nameOfOrganization("Test Organization")
                .legalAddress("Legal Address")
                .actualAddress("Actual Address")
                .inn("1234567890")
                .kpp("123456789")
                .bik("123456789")
                .currentAccount("12345678901234567890")
                .correspondentAccount("12345678901234567890")
                .okpo("12345678")
                .okato("12345678901")
                .okved("12.34")
                .ogrn("1234567890123")
                .generalManager("John Doe")
                .email("test@example.com")
                .phone("+1234567890")
                .build();

        ClientsResponse response = new ClientsResponse();
        response.setMessage("Компания Test Organization ,инн 1234567890 успешно создан");

        when(clientsRepository.findByInn(request.getInn())).thenReturn(Optional.empty());
        when(objectMapper.convertValue(request, Clients.class)).thenReturn(client);
        when(clientsRepository.save(client)).thenReturn(client);

        ClientsResponse actualResponse = clientsService.addClient(request);

        assertNotNull(actualResponse);
        assertEquals("Test Organization", client.getNameOfOrganization());
        assertEquals("1234567890", client.getInn());
        assertEquals("Компания Test Organization ,инн 1234567890 успешно создан", actualResponse.getMessage());

        verify(clientsRepository, times(1)).findByInn(request.getInn());
        verify(clientsRepository, times(1)).save(client);
        verify(objectMapper, times(1)).convertValue(client, ClientsResponse.class);
    }

    @Test
    @DisplayName("Негативный тест — попытка добавить клиента с уже существующим ИНН")
    public void addClientWithExistingInn_ShouldThrowException() {
        ClientsRequest request = ClientsRequest.builder()
                .nameOfOrganization("TestOrg")
                .inn("123456789012")
                .build();

        Clients existingClient = Clients.builder()
                .nameOfOrganization("ExistingOrg")
                .inn("123456789012")
                .build();

        when(clientsRepository.findByInn(request.getInn())).thenReturn(Optional.of(existingClient));

        Exception exception = assertThrows(CommonBackendException.class, () -> {
            clientsService.addClient(request);
        });

        assertEquals("Компания TestOrg ,с инн 123456789012 уже существует", exception.getMessage());

        verify(clientsRepository, times(1)).findByInn(request.getInn());
        verify(clientsRepository, never()).save(any());
    }


    @Test
    @DisplayName("Позитивный тест — успешное обновление клиента по ИНН")
    public void updateClientPositiveTest() {
        String inn = "123456789012";

        ClientsUpdateRequest request = new ClientsUpdateRequest();
        request.setNameOfOrganization("UpdatedOrg");
        request.setLegalAddress("Updated Legal Address");
        request.setActualAddress("Updated Actual Address");
        request.setKpp("123456789");
        request.setBik("044525999");
        request.setCurrentAccount("40817810000000000002");
        request.setCorrespondentAccount("30101810200000000998");
        request.setOkpo("87654321");
        request.setOkato("10987654321");
        request.setOkved("62.02");
        request.setOgrn("9876543210123");
        request.setGeneralManager("Петров П.П.");
        request.setEmail("updated@example.com");
        request.setPhone("+79991112233");

        Clients existingClient = Clients.builder()
                .inn(inn)
                .nameOfOrganization("OldOrg")
                .build();

        Clients updatedClient = Clients.builder()
                .inn(inn)
                .nameOfOrganization("UpdatedOrg")
                .legalAddress("Updated Legal Address")
                .actualAddress("Updated Actual Address")
                .kpp("123456789")
                .bik("044525999")
                .currentAccount("40817810000000000002")
                .correspondentAccount("30101810200000000998")
                .okpo("87654321")
                .okato("10987654321")
                .okved("62.02")
                .ogrn("9876543210123")
                .generalManager("Петров П.П.")
                .email("updated@example.com")
                .phone("+79991112233")
                .build();

        when(clientsRepository.findByInn(inn)).thenReturn(Optional.of(existingClient));
        when(clientsRepository.save(any(Clients.class))).thenReturn(updatedClient);
        when(objectMapper.convertValue(updatedClient, ClientsResponse.class))
                .thenReturn(new ClientsResponse("Данные компании UpdatedOrg успешно обновлены"));

        ClientsResponse response = clientsService.updateClient(inn, request);

        assertNotNull(response);
        assertEquals("Данные компании UpdatedOrg успешно обновлены", response.getMessage());

        verify(clientsRepository, times(1)).findByInn(inn);
        verify(clientsRepository, times(1)).save(any(Clients.class));
    }

    @Test
    @DisplayName("Негативный тест — обновление клиента с несуществующим ИНН")
    public void updateClientWithNonExistingInn_ShouldThrowException() {
        String inn = "999999999999";
        ClientsUpdateRequest request = new ClientsUpdateRequest();
        request.setNameOfOrganization("NonExistingOrg");

        when(clientsRepository.findByInn(inn)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CommonBackendException.class, () -> {
            clientsService.updateClient(inn, request);
        });

        assertEquals("Компания с  инн 999999999999 не найден", exception.getMessage());

        verify(clientsRepository, times(1)).findByInn(inn);
        verify(clientsRepository, never()).save(any());
    }

    @Test
    @DisplayName("Позитивный тест — успешное удаление клиента по ИНН")
    public void deleteClientPositiveTest() {
        String inn = "123456789012";

        Clients client = Clients.builder()
                .inn(inn)
                .nameOfOrganization("TestOrg")
                .build();

        when(clientsRepository.findByInn(inn)).thenReturn(Optional.of(client));
        doNothing().when(clientsRepository).delete(client);

        ClientsResponse response = clientsService.deleteClient(inn);

        assertNotNull(response);
        assertEquals("Компания TestOrg успешно удалена", response.getMessage());

        verify(clientsRepository, times(1)).findByInn(inn);
        verify(clientsRepository, times(1)).delete(client);
    }

    @Test
    @DisplayName("Негативный тест — удаление клиента с несуществующим ИНН")
    public void deleteClientWithNonExistingInn_ShouldThrowException() {
        String inn = "999999999999";

        when(clientsRepository.findByInn(inn)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CommonBackendException.class, () -> {
            clientsService.deleteClient(inn);
        });

        assertEquals("Компания с  инн 999999999999 не найден", exception.getMessage());

        verify(clientsRepository, times(1)).findByInn(inn);
        verify(clientsRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Позитивный тест — получение всех клиентов")
    public void getAllClientsPositiveTest() {
        List<Clients> clientList = List.of(
                Clients.builder()
                        .nameOfOrganization("Org1")
                        .inn("1234567890")
                        .email("org1@example.com")
                        .build(),
                Clients.builder()
                        .nameOfOrganization("Org2")
                        .inn("0987654321")
                        .email("org2@example.com")
                        .build()
        );

        List<ClientsResponse> expectedResponses = List.of(
                new ClientsResponse("Компания Org1 ,инн 1234567890 успешно создан"),
                new ClientsResponse("Компания Org2 ,инн 0987654321 успешно создан")
        );

        when(clientsRepository.findAll()).thenReturn(clientList);
        when(objectMapper.convertValue(clientList.get(0), ClientsResponse.class)).thenReturn(expectedResponses.get(0));
        when(objectMapper.convertValue(clientList.get(1), ClientsResponse.class)).thenReturn(expectedResponses.get(1));

        List<ClientsResponse> actualResponses = clientsService.getAllClients();

        assertNotNull(actualResponses);
        assertEquals(2, actualResponses.size());
        assertEquals(expectedResponses, actualResponses);

        verify(clientsRepository, times(1)).findAll();
        verify(objectMapper, times(2)).convertValue(clientList.get(0), ClientsResponse.class);
        verify(objectMapper, times(2)).convertValue(clientList.get(1), ClientsResponse.class);
    }

    @Test
    @DisplayName("Негативный тест — список клиентов пуст")
    public void getAllClientsEmptyListTest() {
        when(clientsRepository.findAll()).thenReturn(Collections.emptyList());

        CommonBackendException exception = assertThrows(CommonBackendException.class, () -> {
            clientsService.getAllClients();
        });

        assertEquals("Списко компании пуст", exception.getMessage());

        verify(clientsRepository, times(1)).findAll();
        verify(objectMapper, never()).convertValue(any(), any(Class.class));
    }


    @Test
    @DisplayName("Позитивный тест: Получение информации о клиенте ")
    public void testGetInfoClient_Success() {
        String clientId = "1234567890";
        Clients expectedClients = Clients.builder()
                .nameOfOrganization("Компания Пример")
                .legalAddress("Москва, ул. Примерная, д. 1")
                .actualAddress("Москва, ул. Реальная, д. 2")
                .inn(clientId)
                .kpp("9876543210")
                .bik("123456789")
                .currentAccount("40817810010010010010")
                .correspondentAccount("30101810400000000000")
                .okpo("12345678")
                .okato("64000000")
                .okved("62.01")
                .ogrn("1234567890123")
                .generalManager("Иванов Иван Иванович")
                .email("example@company.com")
                .phone("+7 900 123-45-67")
                .build();

        when(clientsRepository.findByInn(clientId))
                .thenReturn(Optional.of(expectedClients));

        ClientsInfoResponse expectedClient = ClientsInfoResponse.builder()
                .message("Данные о компании Компания Пример")
                .nameOfOrganization("Компания Пример")
                .legalAddress("Москва, ул. Примерная, д. 1")
                .actualAddress("Москва, ул. Реальная, д. 2")
                .inn("1234567890")
                .kpp("9876543210")
                .bik("123456789")
                .currentAccount("40817810010010010010")
                .correspondentAccount("30101810400000000000")
                .okpo("12345678")
                .okato("64000000")
                .okved("62.01")
                .ogrn("1234567890123")
                .generalManager("Иванов Иван Иванович")
                .email("example@company.com")
                .phone("+7 900 123-45-67")
                .build();

        ClientsInfoResponse actualClient = clientsService.getInofClient(clientId);

        assertNotNull(actualClient);
        assertEquals(expectedClient.getMessage(), actualClient.getMessage());
        assertEquals(expectedClient.getNameOfOrganization(), actualClient.getNameOfOrganization());
        assertEquals(expectedClient.getLegalAddress(), actualClient.getLegalAddress());
        assertEquals(expectedClient.getActualAddress(), actualClient.getActualAddress());
        assertEquals(expectedClient.getInn(), actualClient.getInn());
        assertEquals(expectedClient.getKpp(), actualClient.getKpp());
        assertEquals(expectedClient.getBik(), actualClient.getBik());
        assertEquals(expectedClient.getCurrentAccount(), actualClient.getCurrentAccount());
        assertEquals(expectedClient.getCorrespondentAccount(), actualClient.getCorrespondentAccount());
        assertEquals(expectedClient.getOkpo(), actualClient.getOkpo());
        assertEquals(expectedClient.getOkato(), actualClient.getOkato());
        assertEquals(expectedClient.getOkved(), actualClient.getOkved());
        assertEquals(expectedClient.getOgrn(), actualClient.getOgrn());
        assertEquals(expectedClient.getGeneralManager(), actualClient.getGeneralManager());
        assertEquals(expectedClient.getEmail(), actualClient.getEmail());
        assertEquals(expectedClient.getPhone(), actualClient.getPhone());
    }

    @Test
    @DisplayName("Негативный тест клиент не найден, исключение")
    public void testGetInfoClient_ClientNotFound_Exception() {
        String clientId = "9999";

        assertThrows(CommonBackendException.class, () -> {
            clientsService.getInofClient(clientId);
        });
    }
}