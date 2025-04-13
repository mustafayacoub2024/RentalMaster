package com.example.rentalmaster.model.dto.response;

import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.ClientShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.DriverShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.EmployeeShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.TechniqueShortResponse;
import com.example.rentalmaster.model.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
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
    private String rentalOrderId;

    @Schema(description = "Количество арендных дней",
            example = "5",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer rentalDays;

    @Schema(description = "Статус заявки")
    private Status status;

    @Schema(description = "Дата создание заявки")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Schema(description = "Дата обновление заявки")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Schema(description = "Адрес")
    private String address;

    @Schema(description = "Дата начало аренды")
    private LocalDateTime startDate;

    @Schema(description = "Дата окончание аренды")
    private LocalDateTime endDate;

    @Schema(description = "Стоимость аренды")
    private Double totalCost;

    @Schema(description = "Сотрудник")
    private EmployeeShortResponse employees;

    @Schema(description = "Водитель")
    private List<DriverShortResponse> drivers;

    @Schema(description = "Техника")
    private List<TechniqueShortResponse> techniques;

    @Schema(description = "Клиент")
    private ClientShortResponse clients;

}



