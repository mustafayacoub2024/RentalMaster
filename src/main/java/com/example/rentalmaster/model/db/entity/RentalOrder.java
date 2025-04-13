package com.example.rentalmaster.model.db.entity;


import com.example.rentalmaster.model.enums.Status;
import com.example.rentalmaster.utils.StatusConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/*Заявка на аренду строительной техники*/


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "rentalOrder")
public class RentalOrder {

    @Id
    @GeneratedValue(generator = "alphanumeric_id")
    @GenericGenerator(
            name = "alphanumeric_id",
            strategy = "com.example.rentalmaster.utils.AlphaNumericIdGenerator"
    )
    @Column(name = "rental_order_id", updatable = false, nullable = false, unique = true, length = 9)
    String rentalOrderId;

    @Column(name = "rentalCost")
    private Double rentalCost;

    @Column(name = "status")
    @Convert(converter = StatusConverter.class)
    private Status status;

    @Column(name = "createdAt")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updatedAt")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "address")
    private String address;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "actual_end_date")
    private LocalDateTime actualEndDate;

    @ManyToOne
    @JoinColumn(name = "personalNumber")
    private Employees employees;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<Drivers> drivers;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<Technique> techniques;

    @ManyToOne
    @JoinColumn(name = "inn")
    private Clients clients;

    @ManyToOne
    @JoinColumn(name = "branchName")
    private Branches branch;

}
