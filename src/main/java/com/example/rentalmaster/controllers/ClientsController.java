package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.request.DriversRequest;
import com.example.rentalmaster.model.dto.response.ClientsResponse;
import com.example.rentalmaster.model.dto.response.DriverResponse;
import com.example.rentalmaster.service.ClientsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientsController {

    private final ClientsService clientsService;

    @PostMapping
    @Operation(summary = "Создать клиента")
    public ClientsResponse addClient(@RequestBody @Valid ClientsRequest clientsRequest) {
        try {
            return clientsService.addClient(clientsRequest);
        } catch (RuntimeException e) {
            ClientsResponse clientsResponse = new ClientsResponse();
            clientsResponse.setMessage(e.getMessage());
            return clientsResponse;
        }
    }

    @PutMapping("/{inn}")
    @Operation(summary = "Изменить данные клиента")
    public ClientsResponse updateClient(@PathVariable @Valid String inn,@RequestBody @Valid ClientsRequest clientsRequest) {
        try {
            return clientsService.updateClient(inn, clientsRequest);
        } catch (RuntimeException e) {
            ClientsResponse clientsResponse = new ClientsResponse();
            clientsResponse.setMessage(e.getMessage());
            return clientsResponse;
        }
    }

    @DeleteMapping("/{inn}")
    @Operation(summary = "Удалить клиента")
    public ClientsResponse deleteClient(@PathVariable("inn") String inn) {
        try {
            return clientsService.deleteClient(inn);
        } catch (RuntimeException e) {
            ClientsResponse clientsResponse = new ClientsResponse();
            clientsResponse.setMessage(e.getMessage());
            return clientsResponse;
        }
    }
}
