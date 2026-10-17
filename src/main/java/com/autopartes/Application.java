package com.autopartes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// Esta exclusión le dice a Spring: "Arranca el servidor aunque no haya Base de Datos"
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}