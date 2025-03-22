package com.example.rentalmaster.model.db.entity;

import com.example.rentalmaster.model.enums.Roles;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/*данные о сотрудниках */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "employees")
public class Employees {
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

    @Column(name = "role")
    private Roles role;


}
