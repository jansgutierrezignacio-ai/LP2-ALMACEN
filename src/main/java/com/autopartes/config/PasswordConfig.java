package com.autopartes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Codificador de claves de usuario (BCrypt).
 *
 * Se usa únicamente el módulo de cifrado {@code spring-security-crypto}; NO se
 * aplica el starter de Spring Security: la sesión y el control de acceso por
 * rol siguen a cargo de {@code AuthFilter} y {@code Autorizacion}. Las claves
 * se almacenan como hash BCrypt de 60 caracteres (columna clave VARCHAR(100)),
 * por lo que la base de datos no guarda texto plano.
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
