package com.example.rentalmaster.model.db.entity;

import com.example.rentalmaster.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

/*Юридическое лицо, фирма */
/* html страница: http://localhost:8080/clients.html */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "clients")
public class Clients {

    @Id
    @Column(name = "inn")
    private String inn;

    @Column(name = "nameOfOrganization") //название организаций
    private String nameOfOrganization;

    @Column(name = "legalAddress") // юридический адрес
    private String legalAddress;

    @Column(name = "actualAddress") //Фактический адрес
    private String actualAddress;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @JsonManagedReference(value = "client_order")
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<RentalOrder> orders;




}