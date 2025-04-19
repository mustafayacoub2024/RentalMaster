package com.example.rentalmaster.model.enums;

public enum Status {
    NEW("Новая"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершено"),
    REJECTED("Откланено");

    private final String russianName;

    Status(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }
}