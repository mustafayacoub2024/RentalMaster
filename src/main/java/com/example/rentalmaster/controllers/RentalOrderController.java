package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.dto.request.RentalOrderRequest;
import com.example.rentalmaster.model.dto.request.RentalOrderUpdateRequest;
import com.example.rentalmaster.model.dto.response.RentalOrderGetAllResponse;
import com.example.rentalmaster.model.dto.response.RentalOrderResponse;
import com.example.rentalmaster.service.RentalOrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PutMapping("/{rentalOrderId}/in_progress")
    @Operation(summary = "Редактирование статуса заявки, IN_PROGRESS")
    public RentalOrderResponse updateRentalStatusByInProgress(@PathVariable String rentalOrderId) {
        return rentalOrderService.updateStatusByInProgress(rentalOrderId);
    }

    @PutMapping("/{rentalOrderId}/completed")
    @Operation(summary = "Редактирование статуса заявки, COMPLETED")
    public RentalOrderResponse updateRentalStatusByCombleted(@PathVariable String rentalOrderId) {
        return rentalOrderService.updateStatusByCombleted(rentalOrderId);

    }

    @PutMapping("/{rentalOrderId}/rejected")
    @Operation(summary = "Редактирование статуса заявки, REJECTED")
    public RentalOrderResponse updateRentalStatusByRejected(@PathVariable String rentalOrderId) {
        return rentalOrderService.updateStatusByRejected(rentalOrderId);

    }

    @PutMapping("/{rentalOrderId}")
    @Operation(summary = "Редактирование заявки")
    public RentalOrderResponse updateRentalStatusById(@PathVariable String rentalOrderId,
                                                      @RequestBody RentalOrderUpdateRequest rentalOrderRequest) {
        return rentalOrderService.updateRentalOrder(rentalOrderId, rentalOrderRequest);
    }

    @GetMapping()
    @Operation(summary = "Получить список всех заявок")
    public List<RentalOrderGetAllResponse> getRentalOrders() {
        return rentalOrderService.getAllRentalOrders();
    }

    @GetMapping("/{rentalOrderId}")
    @Operation(summary = "Получение информации о заявке")
    public RentalOrderResponse getRentalOrderById(@PathVariable String rentalOrderId) {
        return rentalOrderService.getInfoToOrderById(rentalOrderId);

    }
}
