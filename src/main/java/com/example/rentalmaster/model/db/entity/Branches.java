package com.example.rentalmaster.model.db.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/*Филиал, в каждом городе свой отдельный филиал*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "branches")
public class Branches {

    @Id
    @Column(name = "branchId")
    private UUID branchId;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "phone")
    private String phone;

    @OneToOne
    private Employees employees;

    @OneToMany
    private List<Technique> techniques;

}