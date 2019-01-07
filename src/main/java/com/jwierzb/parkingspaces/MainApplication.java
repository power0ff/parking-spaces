package com.jwierzb.parkingspaces;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);
        SpringApplication.run(MainApplication.class, args);
    }
}