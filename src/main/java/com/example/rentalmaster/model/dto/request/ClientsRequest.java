package com.example.rentalmaster.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
public class ClientsRequest {

    @NotEmpty
    @Schema(description = "название организаций")
    private String nameOfOrganization;

    @NotEmpty
    @Schema(description = " юридический адрес")
    private String legalAddress;

    @NotEmpty
    @Schema(description = "Фактический адрес")
    private String actualAddress;

    @NotEmpty
    @Schema(description = "ИНН организаций")
    private String inn;

    @NotEmpty
    @Schema(description = "КПП организаций")
    private String kpp;

    @NotEmpty
    @Schema(description = "БИК организаций")
    private String bik;

    @NotEmpty
    @Schema(description = "p/c организаций")
    private String currentAccount;

    @NotEmpty
    @Schema(description = "к/с организаций")
    private String correspondentAccount;

    @NotEmpty
    @Schema(description = "ОКПО организация")
    private String okpo;

    @NotEmpty
    @Schema(description = "ОКАТО организаций")
    private String okato;

    @NotEmpty
    @Schema(description = "ОКВЭД организация")
    private String okved;

    @NotEmpty
    @Schema(description = "ОГРН организаций")
    private String ogrn;

    @NotEmpty
    @Schema(description = "ФИО генирального директора")
    private String generalManager;

    @NotEmpty
    @Schema(description = "email")
    private String email;

    @NotEmpty
    @Schema(description = "Номер телефона организаций")
    private String phone;

}
