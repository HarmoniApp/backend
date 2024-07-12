package org.harmoniapp.harmoniwebapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
//@ComponentScan(basePackages = "org.harmoniapp.harmonidata")
//@EnableJpaRepositories(basePackages = "org.harmoniapp.harmonidata.repositories")

public class HarmoniWebApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarmoniWebApiApplication.class, args);
    }

}
