package com.example.rentalmaster.model.db.entity;

/*данные о сотрудниках
* html страница: http://localhost:8080/driver.html */

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "drivers")
public class Drivers {
    @Id
    @Column(name = "personalNumber")
    private String personalNumber;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name= "salary")
    private Double salary;
}
