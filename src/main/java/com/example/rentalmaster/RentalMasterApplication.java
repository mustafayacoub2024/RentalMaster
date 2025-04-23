package com.example.rentalmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RentalMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalMasterApplication.class, args);
    }
}