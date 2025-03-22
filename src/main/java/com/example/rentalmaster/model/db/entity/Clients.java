package com.example.rentalmaster.model.db.entity;
/*Информация об юр лицах, которые арендуют технику */

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


/*Юридическое лицо, фирма */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "clients")
public class Clients {
    @Id
    private UUID clientsId;

    @Column(name = "nameOfOrganization") //название организаций
    private String nameOfOrganization;

    @Column(name = "legalAddress") // юридический адрес
    private String legalAddress;

    @Column(name = "actualAddress") //Фактический адрес
    private String actualAddress;

    @Column(name = "inn")
    private String inn;

    @Column(name="kpp")
    private String kpp;

    @Column(name="bik")
    private String bik;

    @Column(name="currentAccount") // p/c
    private String currentAccount;

    @Column(name = "correspondentAccount") // к/с
    private String correspondentAccount;

    @Column(name = "okpo")
    private String okpo;

    @Column(name = "okato")
    private String okato;

    @Column(name = "okved")
    private String okved;

    @Column(name = "ogrn")
    private String ogrn;

    @Column(name = "generalManager")
    private String generalManager;

    @Column(name="email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @OneToMany(mappedBy = "clients")
    @JsonManagedReference(value = "client_order")
    private List<RentalOrder> rentalOrders;

}
