package org.harmoniapp;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication(exclude = {SessionAutoConfiguration.class})
@EnableAsync(proxyTargetClass = true)
@EnableCaching
@EnableScheduling
@EnableWebSecurity
@EnableWebSocketMessageBroker
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class HarmoniWebApiApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().filename(".env").load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(HarmoniWebApiApplication.class, args);
    }

}
