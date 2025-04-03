package com.example.rentalmaster.model.dto.response;

import com.example.rentalmaster.model.enums.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeesResponse {

    @Schema(description = "Сообщение о результате операции")
    private String message;

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
    @Schema(description = "Роль")
    private Roles role;

}
