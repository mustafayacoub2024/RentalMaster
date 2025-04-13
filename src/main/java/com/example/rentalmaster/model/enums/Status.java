package com.example.rentalmaster.model.enums;

public enum Status {
    NEW("Новая"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершено");

    private final String russianName;

    Status(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }
}