package com.example.rentalmaster.model.enums;

public enum TypeTechnique {
    EXCAVATOR("Экскаватор"),
    BULLDOZER("Бульдозер"),
    GRADER("Автогрейдер"),
    CRANE("Кран"),
    ASPHALT_PAVER("Асфальтоукладчик"),
    ROLLER("Виброкаток"),
    CONCRETE_PUMP("Бетононасос"),
    HYDRAULIC_HAMMER("Гидромолот");

    private final String russianName;

    TypeTechnique(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }
}