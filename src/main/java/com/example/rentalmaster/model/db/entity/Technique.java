package com.example.rentalmaster.model.db.entity;

import com.example.rentalmaster.model.enums.Availability;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


/*данные о технике, которая имеется в компании*/


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "technique")
public class Technique {

    @Id
    @Column(name = "stateNumber")
    private String stateNumber;

    @ManyToOne
    @JsonBackReference(value = "technique_branch")
    @JoinColumn(name = "branchId")
    private Branches branch;

    @OneToOne
    @JsonManagedReference(value = "technique_type")
    @JoinColumn(name = "technique_type_id")
    private TechnigueType type;


    @Column(name = "baseCost")
    private Double baseCost; //Базовая стоимость аренды

    @Column(name = "availability")
    private Availability availability; //Доступность технике

    @OneToMany(mappedBy = "techniques")
    @JsonManagedReference(value = "technique_order")
    private List<RentalOrder> rentalOrders;


}
