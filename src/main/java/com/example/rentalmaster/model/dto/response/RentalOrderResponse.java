package com.example.rentalmaster.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RentalOrderResponse {

    @Schema(description = "Сообщение о результате операции")
    private String message;

    @Schema(description = "rental_order")
    private UUID rentalOrderId;

}
