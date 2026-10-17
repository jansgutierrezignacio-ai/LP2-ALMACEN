package com.autopartes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// Habilita el escaneo de @Service y @Controller de todos los paquetes del proyecto
// y registra las clases entidad del paquete "modelo" para la capa JPA/Hibernate.
@SpringBootApplication(
    scanBasePackages = {"com.autopartes", "procesos", "controlador", "interfaces"}
)
@EntityScan("modelo")
public class Application extends SpringBootServletInitializer {

    // Permite desplegar el WAR en un Tomcat externo (Spring arranca desde el contenedor).
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}