package com.example.rentalmaster.utils;

import com.example.rentalmaster.model.enums.TypeTechnique;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class TypeTechniqueConverter implements AttributeConverter<TypeTechnique, String> {

    @Override
    public String convertToDatabaseColumn(TypeTechnique type) {
        return (type != null) ? type.getRussianName() : null;
    }

    @Override
    public TypeTechnique convertToEntityAttribute(String dbValue) {
        return Arrays.stream(TypeTechnique.values())
                .filter(t -> t.getRussianName().equalsIgnoreCase(dbValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип техники: " + dbValue));
    }
}
