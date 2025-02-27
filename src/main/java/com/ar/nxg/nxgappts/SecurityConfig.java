package com.ar.nxg.nxgappts;

import com.ar.nxg.nxgappts.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //.csrf().csrfTokenRepository(csrfTokenRepository()) // Configurar el repositorio de tokens CSRF
                .csrf().disable()
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login/**", "/register/**", "/registerUser/**", "/confirm", "/confirmation",
                                "/assets/**",  "/css/**", "/js/**", "/images/**", "/image/**", "/vendor/**", "/reset-password-request",
                                "/reset-password").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                //.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)  // Añadir el filtro de autenticación JWT
                .formLogin(t -> t.loginPage("/login").permitAll().defaultSuccessUrl("/", true)
                        //.failureUrl("/login?error"))
                        .failureHandler(customAuthenticationFailureHandler())) // Configurar el handler de fallos
                .logout();
        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            String errorMessage = "Usuario o clave incorrectos."; // Mensaje genérico

            // Detectar si es un error de usuario desactivado
            if (exception.getMessage().contains("user.not.active")) {
                errorMessage = "El usuario no está activo o no tiene permiso para iniciar sesión";
            }
            System.out.println(errorMessage);
            // Redirigir al login con el mensaje de error como parámetro
            response.sendRedirect("/login?error=" + errorMessage);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

}
