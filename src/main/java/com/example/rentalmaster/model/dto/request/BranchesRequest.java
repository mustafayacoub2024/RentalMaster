package com.example.rentalmaster.model.dto.request;

import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.model.db.entity.Technique;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class BranchesRequest {

    @NotEmpty
    @Schema(description = "Город")
    private String city;

    @NotEmpty
    @Schema(description = "Адрес")
    private String address;

    @NotEmpty
    @Schema(description = "email")
    private String email;

    @NotEmpty
    @Schema(description = "Номер телефона")
    private String phone;

    @NotNull
    @Schema(description = "Персональный табельный номер сотрудника")
    private Employees employees;


}
