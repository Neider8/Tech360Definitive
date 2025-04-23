package com.telastech360.crmTT360.security.config;

import com.telastech360.crmTT360.security.auth.AuthEntryPointJwt;
import com.telastech360.crmTT360.security.jwt.JwtRequestFilter;
import com.telastech360.crmTT360.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Configuración principal de Spring Security
@Configuration
@EnableWebSecurity // Habilita la seguridad web
@EnableMethodSecurity( // Habilita seguridad a nivel de método (ej: @PreAuthorize)
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Define el filtro JWT que creamos
    @Bean
    public JwtRequestFilter authenticationJwtTokenFilter() {
        return new JwtRequestFilter();
    }

    // Configura el proveedor de autenticación con nuestro UserDetailsService y PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Define el AuthenticationManager necesario para autenticar usuarios
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Define el PasswordEncoder que se usará para cifrar/verificar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Puedes usar otro, pero BCrypt es común y seguro
    }

    // Configura la cadena de filtros de seguridad HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                        "/api/auth/**", // Rutas de autenticación ya permitidas
                                        "/swagger-ui.html", // Permitir el archivo principal de Swagger UI
                                        "/swagger-ui/**",   // Permitir los recursos estáticos de Swagger UI (CSS, JS, etc.)
                                        "/v3/api-docs/**",  // Permitir la definición de la API en formato OpenAPI 3
                                        "/api-docs/**",     // Alias común para las definiciones de API
                                        "/webjars/**"       // Permitir recursos de webjars (usados por Swagger UI)
                                ).permitAll() // Permitir acceso a estas rutas sin autenticación
                                .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
                );

        // Añade el proveedor de autenticación
        http.authenticationProvider(authenticationProvider());

        // Añade nuestro filtro JWT antes del filtro estándar de nombre de usuario/contraseña
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}