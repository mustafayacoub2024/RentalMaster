package com.example.rentalmaster.model.db.entity;
/*создание нового заказа, изменение существующего заказа, расчет стоимости аренды*/

import com.example.rentalmaster.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

/*Заявка на аренду строительной техники*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "rentalOrder")
public class RentalOrder {

    @Id
    @Column(name = "rental_order")
    UUID rentalOrderId;

    @Column(name = "rentalCost")
    private Double rentalCost;

    @Column(name = "status")
    private Status status;

    @Column(name = "createdAt")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "address")
    private String address;

    @Column(name = "rental_days")
    private Integer rentalDays;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "total_cost")
    private Double totalCost;

    @ManyToOne
    @JsonBackReference(value = "client_order")
    private Employees employees;

    @ManyToOne
    @JoinColumn(name = "driversId")
    private Drivers drivers;

    @ManyToOne
    @JsonBackReference(value = "technique_order")
    @JoinColumn(name = "techniquesId")
    private Technique techniques;

    @ManyToOne
    @JsonBackReference(value = "client_order")
    @JoinColumn(name = "clientsId")
    private Clients clients;

    @ManyToOne
    @JsonBackReference(value = "branch_order")
    @JoinColumn(name = "branchName")
    private Branches branch;
}
