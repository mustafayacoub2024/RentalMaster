package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.ClientsRequest;
import com.example.rentalmaster.model.dto.request.ClientsUpdateRequest;
import com.example.rentalmaster.model.dto.response.ClientsInfoResponse;
import com.example.rentalmaster.model.dto.response.ClientsResponse;
import com.example.rentalmaster.service.ClientsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientsController {

    private final ClientsService clientsService;

    @PostMapping
    @Operation(summary = "Создать клиента")
    public ClientsResponse addClient(@RequestBody @Valid ClientsRequest clientsRequest) {
        return clientsService.addClient(clientsRequest);

    }

    @PutMapping("/{inn}")
    @Operation(summary = "Изменить данные клиента")
    public ClientsResponse updateClient(@PathVariable @Valid String inn,
                                                        @RequestBody @Valid ClientsUpdateRequest clientsRequest) {
        return clientsService.updateClient(inn, clientsRequest);

    }

    @DeleteMapping("/{inn}")
    @Operation(summary = "Удалить клиента")
    public ClientsResponse deleteClient(@PathVariable("inn") String inn) {
        return clientsService.deleteClient(inn);

    }

    @GetMapping
    @Operation(summary = "Получить список всех клиентов")
    public List<ClientsResponse> getClients() {
        return clientsService.getAllClients();
    }

    @GetMapping("/{inn}")
    @Operation(summary = "Получить данных о клиенте")
    public ClientsInfoResponse getInfoClient(@PathVariable("inn") String inn) {
        return clientsService.getInofClient(inn);
    }
}
