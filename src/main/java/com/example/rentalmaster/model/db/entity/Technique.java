package com.example.rentalmaster.model.db.entity;

import com.example.rentalmaster.model.enums.Availability;
import com.example.rentalmaster.model.enums.TypeTechnique;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*данные о технике, которая имеется в компании*/
/*http://localhost:8080/technique.html*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "technique")
public class Technique {

    @Id
    @Column(name = "stateNumber")
    private String stateNumber;

    @Column(name = "yearOfProduction")
    private String yearOfProduction;

    @Column(name = "loadCapacity")
    private String loadCapacity; //грузоподьёмность

    @Column(name = "weight")
    private String weight; //масса

    @Column(name = "color")
    private String color;

    @Column(name = "baseCost")
    private Double baseCost; //Базовая стоимость аренды за 1 час в руб

    @Column(name = "typeTechnique")
    private TypeTechnique typeTechnique;

    @Column(name = "Статус")
    private Availability availability;




}
