package org.harmoniapp.harmoniwebapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HarmoniWebApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarmoniWebApiApplication.class, args);
    }

}
