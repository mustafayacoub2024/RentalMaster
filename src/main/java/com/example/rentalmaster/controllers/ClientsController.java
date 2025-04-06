package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.request.ClientsUpdateRequest;
import com.example.rentalmaster.model.dto.response.ClientsInfoResponse;
import com.example.rentalmaster.model.dto.response.ClientsResponse;
import com.example.rentalmaster.service.ClientsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientsController {

    private final ClientsService clientsService;

    @PostMapping
    @Operation(summary = "Создать клиента")
    public ResponseEntity<ClientsResponse> addClient(@RequestBody @Valid ClientsRequest clientsRequest) {
        ClientsResponse clientsResponse = clientsService.addClient(clientsRequest);
        return ResponseEntity.ok(clientsResponse);

    }

    @PutMapping("/{inn}")
    @Operation(summary = "Изменить данные клиента")
    public ResponseEntity<ClientsResponse> updateClient(@PathVariable @Valid String inn,
                                                        @RequestBody @Valid ClientsUpdateRequest clientsRequest) {
        ClientsResponse clientsResponse = clientsService.updateClient(inn, clientsRequest);
        return ResponseEntity.ok(clientsResponse);

    }

    @DeleteMapping("/{inn}")
    @Operation(summary = "Удалить клиента")
    public ResponseEntity<ClientsResponse> deleteClient(@PathVariable("inn") String inn) {
            ClientsResponse clientsResponse =  clientsService.deleteClient(inn);
            return ResponseEntity.ok(clientsResponse);

    }

    @GetMapping
    @Operation(summary = "Получить список всех клиентов")
    public ResponseEntity<List<ClientsResponse>> getClients() {
        List<ClientsResponse> clients = clientsService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{inn}")
    @Operation(summary = "Получить данных о клиенте")
    public ResponseEntity<ClientsInfoResponse> getInfoClient(@PathVariable("inn") String inn) {
        ClientsInfoResponse client = clientsService.getInofClient(inn);
        return ResponseEntity.ok(client);
    }
}
