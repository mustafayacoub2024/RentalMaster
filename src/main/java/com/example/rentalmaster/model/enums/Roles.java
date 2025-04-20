package com.example.rentalmaster.model.enums;

public enum Roles {
    MANAGER("Менеджер"),
    DIRECTOR("Директор");

    private final String russianName;

    Roles(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
