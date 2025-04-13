package com.example.rentalmaster.utils;

import com.example.rentalmaster.model.enums.Roles;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Roles, String> {

    @Override
    public String convertToDatabaseColumn(Roles role) {
        return (role != null) ? role.getRussianName() : null;
    }

    @Override
    public Roles convertToEntityAttribute(String dbValue) {
        return Arrays.stream(Roles.values())
                .filter(r -> r.getRussianName().equalsIgnoreCase(dbValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестная роль: " + dbValue));
    }
}


