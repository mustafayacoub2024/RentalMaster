package com.example.rentalmaster.model.dto.request;

import com.example.rentalmaster.model.enums.TypeTechnique;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class TechniqueRequest {

    @NotEmpty
    @Schema(description = "Государственный номер")
    private String stateNumber;

    @NotEmpty
    @Schema(description = "Год выпуска")
    private String yearOfProduction;

    @NotEmpty
    @Schema(description = "Грузоподьёемность")
    private String loadCapacity;

    @NotEmpty
    @Schema(description = "Масса")
    private String weight;

    @NotEmpty
    @Schema(description = "Цвет")
    private String color;

    @NotNull(message = "Базовая стоимость не может быть пустой")
    @Min(value = 0, message = "Стоимость не может быть отрицательной")
    @Schema(description = "Базовая стоимость аренды за 1 час в руб")
    private Double baseCost;

    @NotNull
    @Schema(description = "Тип технике")
    private TypeTechnique typeTechnique;

}
