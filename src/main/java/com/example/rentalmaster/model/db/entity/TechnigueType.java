package com.example.rentalmaster.model.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "technique_type")
public class TechnigueType {
    @Id
    private Long technigueTypeId;

    @Column(name = "technigueType")
    private String technigueType;

    @OneToOne(mappedBy = "type")
    @JsonBackReference(value = "technique_type")
    private Technique technique;

}

