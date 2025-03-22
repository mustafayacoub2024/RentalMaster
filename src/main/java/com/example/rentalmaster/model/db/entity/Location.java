package com.example.rentalmaster.model.db.entity;

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
/*Локация, местонахождение филиала*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "location")
public class Location {

    @Id
    @Column(name = "locationId")
    private UUID locationId;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "coefficient")
    private String coefficient;

    @OneToMany(mappedBy = "location")
    @JsonManagedReference(value = "location_branch")
    private List<Branches> branches;

}
