package com.example.rentalmaster.utils;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.security.SecureRandom;
import java.util.stream.Collectors;

public class AlphaNumericIdGenerator implements IdentifierGenerator {

    @Override
    public String generate(SharedSessionContractImplementor session, Object object) {
        final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String NUMBERS = "0123456789";
        final SecureRandom random = new SecureRandom();


        String letters = random.ints(3, 0, LETTERS.length())
                .mapToObj(LETTERS::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());

        String numbers = random.ints(6,0,NUMBERS.length())
                .mapToObj(NUMBERS::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());

        return letters + numbers;
    }
}
