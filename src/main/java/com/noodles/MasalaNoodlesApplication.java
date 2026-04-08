package com.noodles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MasalaNoodlesApplication {

    public static void main(String... args) {
        SpringApplication.run(MasalaNoodlesApplication.class, args);
    }

}