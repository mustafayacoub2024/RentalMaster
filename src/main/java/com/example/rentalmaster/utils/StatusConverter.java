package com.example.rentalmaster.utils;

import com.example.rentalmaster.model.enums.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {
    @Override
    public String convertToDatabaseColumn(Status status) {
        return status.getRussianName();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return Arrays.stream(Status.values())
                .filter(s -> s.getRussianName().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}