package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.RentalOrderRequest;
import com.example.rentalmaster.model.dto.response.RentalOrderResponse;
import com.example.rentalmaster.service.RentalOrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class RentalOrderController {

    private final RentalOrderService rentalOrderService;

    @PostMapping()
    @Operation(summary = "Создание заявки")
    public RentalOrderResponse addRentalOrder(@RequestBody RentalOrderRequest rentalOrderRequest) {
        return rentalOrderService.addRentalOrder(rentalOrderRequest);

    }
}
