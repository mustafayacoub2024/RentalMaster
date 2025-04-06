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
public class ClientsInfoResponse {

    @Schema(description = "Сообщение о результате операции")
    private String message;

    @Schema(description = "название организаций")
    private String nameOfOrganization;

    @Schema(description = " юридический адрес")
    private String legalAddress;

    @Schema(description = "Фактический адрес")
    private String actualAddress;

    @Schema(description = "ИНН организаций")
    private String inn;

    @Schema(description = "КПП организаций")
    private String kpp;

    @Schema(description = "БИК организаций")
    private String bik;

    @Schema(description = "p/c организаций")
    private String currentAccount;

    @Schema(description = "к/с организаций")
    private String correspondentAccount;

    @Schema(description = "ОКПО организация")
    private String okpo;

    @Schema(description = "ОКАТО организаций")
    private String okato;

    @Schema(description = "ОКВЭД организация")
    private String okved;

    @Schema(description = "ОГРН организаций")
    private String ogrn;

    @Schema(description = "ФИО генирального директора")
    private String generalManager;

    @Schema(description = "email")
    private String email;

    @Schema(description = "Номер телефона организаций")
    private String phone;

}
