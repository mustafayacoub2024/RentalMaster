package com.example.rentalmaster.model.dto.response;

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
public class DriverInfoResponse {

    @Schema(description = "Сообщение о результате операции")
    private String message;

    @Schema(description = "Персональный табельный номер")
    private String personalNumber;

    @Schema(description = "Фамилия")
    private String lastName;

    @Schema(description = "Имя")
    private String firstName;

    @Schema(description = "Адрес электронной почты")
    private String email;

    @Schema(description = "Номер телфона")
    private String phone;

    @Schema(description = "Зарплата водителя за 1 час работы")
    private Double salary;
}