package com.example.rentalmaster.model.dto.request;

import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.model.db.entity.Technique;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class RentalOrderRequest {

    @NotNull
    @Schema(description = "Дата создание заявки")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    @Schema(description = "Дата обновление заявки")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @NotEmpty
    @Schema(description = "Адрес")
    private String address;


    @NotNull
    @Schema(description = "Дата начало аренды")
    private LocalDateTime startDate;

    @NotNull
    @Schema(description = "Дата окончание аренды")
    private LocalDateTime endDate;

    @NotNull
    @Schema(description = "Сотрудник")
    private Employees employees;

    @NotNull
    @Schema(description = "Водитель")
    private List<Drivers> drivers;

    @NotNull
    @Schema(description = "Техника")
    private List<Technique> techniques;

    @NotNull
    @Schema(description = "Клиент")
    private Clients clients;

    @NotNull
    @Schema(description = "Филиал")
    private Branches branch;

}