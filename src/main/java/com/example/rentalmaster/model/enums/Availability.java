package com.example.rentalmaster.model.enums;

public enum Availability {
    AVAILABLE("Доступно"),
    BUSY("Занято"),
    MAINTENANCE("ТО");

    private final String russianName;

    Availability(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }
}
