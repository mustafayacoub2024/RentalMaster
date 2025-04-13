package com.example.rentalmaster.utils;

import com.example.rentalmaster.model.enums.Availability;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class AvailabilityConverter implements AttributeConverter<Availability, String> {

    @Override
    public String convertToDatabaseColumn(Availability status) {
        return (status != null) ? status.getRussianName() : null;
    }

    @Override
    public Availability convertToEntityAttribute(String dbValue) {
        return Arrays.stream(Availability.values())
                .filter(s -> s.getRussianName().equals(dbValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Недопустимый статус: " + dbValue));
    }
}
