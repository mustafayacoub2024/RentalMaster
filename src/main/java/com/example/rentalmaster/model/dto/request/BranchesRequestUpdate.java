package com.example.rentalmaster.model.dto.request;

import com.example.rentalmaster.model.db.entity.Employees;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class BranchesRequestUpdate {

    @Schema(description = "Название филиала")
    private String branchName;

    @Schema(description = "Город")
    private String city;

    @Schema(description = "Адрес")
    private String address;

    @Schema(description = "email")
    private String email;

    @Schema(description = "Номер телефона")
    private String phone;

    @Schema(description = "коэфициент наценки")
    private double coefficient;

    @Schema(description = "Персональный табельный номер сотрудника")
    private Employees employees;
}