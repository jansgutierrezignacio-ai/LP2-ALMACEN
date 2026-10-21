package com.autopartes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.autopartes.entity.Usuario;
import com.autopartes.repository.IUsuarioRepository;

@Configuration
public class SecurityConfig {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos públicos y login
                .requestMatchers("/login", "/css/**", "/js/**", "/style/**", "/images/**").permitAll()

                .requestMatchers("/reportes/**").hasRole("ADMIN")
                .requestMatchers("/clientes/eliminar", "/proveedores/eliminar").hasRole("ADMIN")

              
                .requestMatchers("/productos/registrar", "/productos/editar", "/productos/infoEdit", "/productos/eliminar").hasAnyRole("ADMIN", "ALMACENERO")
                .requestMatchers("/productos/**").hasAnyRole("ADMIN", "ALMACENERO", "VENDEDOR")

               
                .requestMatchers("/clientes/registrar", "/clientes/editar", "/clientes/infoEdit").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers("/clientes/**").hasAnyRole("ADMIN", "VENDEDOR", "ALMACENERO")

                
                .requestMatchers("/proveedores/registrar", "/proveedores/editar", "/proveedores/infoEdit").hasAnyRole("ADMIN", "ALMACENERO")
                .requestMatchers("/proveedores/**").hasAnyRole("ADMIN", "ALMACENERO")

               
                .requestMatchers("/movimientos/**").hasAnyRole("ADMIN", "ALMACENERO")
                .requestMatchers("/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")

              
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")         
                .loginProcessingUrl("/login")  
                .defaultSuccessUrl("/inicio", true) 
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/403")
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Usuario usuario = usuarioRepository.findByUsername(username);
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuario no encontrado: " + username);
            }

            String rol = "ALMACENERO"; 
            if (usuario.getIdTipo() != null) {
                if (usuario.getIdTipo() == 1) {
                    rol = "ADMIN";
                } else if (usuario.getIdTipo() == 2) {
                    rol = "VENDEDOR";
                } else if (usuario.getIdTipo() == 3) {
                    rol = "ALMACENERO";
                }
            }

            return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getUsername())
                .password(usuario.getClave()) 
                .roles(rol)
                .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}