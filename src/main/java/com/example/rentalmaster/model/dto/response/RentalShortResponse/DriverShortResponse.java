package com.example.rentalmaster.model.dto.response.RentalShortResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DriverShortResponse {
    @Schema(description = "Персональный номер водителя")
    private String personalNumber;
}