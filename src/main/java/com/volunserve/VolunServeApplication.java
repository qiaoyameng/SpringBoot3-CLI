package com.volunserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VolunServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(VolunServeApplication.class, args);
    }
}
