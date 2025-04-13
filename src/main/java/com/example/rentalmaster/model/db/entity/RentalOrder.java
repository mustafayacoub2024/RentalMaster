package com.example.rentalmaster.model.db.entity;
/*создание нового заказа, изменение существующего заказа, расчет стоимости аренды*/

import com.example.rentalmaster.model.enums.Status;
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
import java.util.UUID;

/*Заявка на аренду строительной техники*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "rentalOrder")
public class RentalOrder {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "rental_order_id", updatable = false, nullable = false)
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

    @Transient
    public Integer getRentalDays() {
        return (int) ChronoUnit.DAYS.between(
                startDate.toLocalDate(),
                endDate.toLocalDate()
        );
    }

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "total_cost")
    private Double totalCost;

    @ManyToOne
    @JsonBackReference(value = "employees_order")
    private Employees employees;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<Drivers> drivers;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<Technique> techniques;

    @ManyToOne
    @JsonBackReference(value = "client_order")
    @JoinColumn(name = "clientsId")
    private Clients clients;

    @ManyToOne
    @JoinColumn(name = "branchName")
    private Branches branch;

}
