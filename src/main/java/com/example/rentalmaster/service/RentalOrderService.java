package com.example.rentalmaster.service;

import com.example.rentalmaster.model.dto.request.RentalOrderRequest;
import com.example.rentalmaster.model.dto.response.RentalOrderResponse;

public interface RentalOrderService {
    RentalOrderResponse addRentalOrder(RentalOrderRequest rentalOrderRequest);

    RentalOrderResponse updateStatusByInProgress(String rentalOrderId);

    RentalOrderResponse updateStatusByCombleted(String rentalOrderId);
}
