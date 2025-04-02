package com.example.rentalmaster.model.dto.response;

import com.example.rentalmaster.model.enums.TypeTechnique;
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
public class TechniqueResponse {

    @Schema(description = "Сообщение о результате операции")
    private String message;

    @Schema(description = "Государственный номер техники")
    private String stateNumber;

    @Schema(description = "Год выпуска техники")
    private String yearOfProduction;

    @Schema(description = "Грузоподъемность техники")
    private String loadCapacity;

    @Schema(description = "Вес техники")
    private String weight;

    @Schema(description = "Цвет техники")
    private String color;

    @Schema(description = "Базовая стоимость аренды за 1 час")
    private Double baseCost;

    @Schema(description = "Тип техники")
    private TypeTechnique typeTechnique;

}
