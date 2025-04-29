package com.example.rentalmaster.model.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/*Филиал, в каждом городе свой отдельный филиал*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "branches")
public class Branches {

    @Id
    @Column(name = "branchName")
    private String branchName;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "коэфициент наценки")
    private double coefficient;

    @OneToOne
    private Employees employees;

    @OneToMany
    private List<Technique> techniques;

    @OneToMany
    private List<Drivers> drivers;
}