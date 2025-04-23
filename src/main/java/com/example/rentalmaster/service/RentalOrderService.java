package com.example.rentalmaster.service;

import com.example.rentalmaster.model.dto.request.RentalOrderRequest;
import com.example.rentalmaster.model.dto.request.RentalOrderUpdateRequest;
import com.example.rentalmaster.model.dto.response.RentalOrderGetAllResponse;
import com.example.rentalmaster.model.dto.response.RentalOrderResponse;

import java.util.List;

public interface RentalOrderService {
    RentalOrderResponse addRentalOrder(RentalOrderRequest rentalOrderRequest);

    RentalOrderResponse updateStatusByInProgress(String rentalOrderId);

    RentalOrderResponse updateStatusByCombleted(String rentalOrderId);

    RentalOrderResponse updateRentalOrder(String rentalOrderId, RentalOrderUpdateRequest rentalOrderRequest);

    List<RentalOrderGetAllResponse> getAllRentalOrders();

    RentalOrderResponse getInfoToOrderById(String rentalOrderId);

    RentalOrderResponse updateStatusByRejected(String rentalOrderId);
}