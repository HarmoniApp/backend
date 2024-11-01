package org.harmoniapp.harmoniwebapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HarmoniWebApiApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().filename(".env").load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(HarmoniWebApiApplication.class, args);
    }

}
