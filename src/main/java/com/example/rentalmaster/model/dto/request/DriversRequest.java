package com.example.rentalmaster.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class DriversRequest {

    @NotEmpty
    @Schema(description = "Персональный табельный номер")
    private String personalNumber;

    @NotEmpty
    @Schema(description = "Фамилия")
    private String lastName;

    @NotEmpty
    @Schema(description = "Имя")
    private String firstName;

    @NotEmpty
    @Schema(description = "Адрес электронной почты")
    private String email;

    @NotEmpty
    @Schema(description = "Номер телфона")
    private String phone;

    @NotNull
    @Schema(description = "Зарплата водителя за 1 час работы")
    private Double salary;
}