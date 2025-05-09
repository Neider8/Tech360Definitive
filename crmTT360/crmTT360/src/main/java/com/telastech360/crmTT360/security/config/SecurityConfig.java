// src/main/java/com/telastech360/crmTT360/security/config/SecurityConfig.java
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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Para disable CSRF
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.telastech360.crmTT360.controller.AuthController;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración principal de Spring Security para la aplicación CRM TT360.
 * Habilita la seguridad web, la seguridad a nivel de método (para anotaciones como @PreAuthorize),
 * configura la autenticación basada en JWT, define el manejo de excepciones de autenticación,
 * y establece las reglas de autorización para las rutas HTTP.
 */
@Configuration
@EnableWebSecurity // Habilita la integración de Spring Security con Spring MVC
@EnableMethodSecurity( // Habilita el uso de anotaciones de seguridad en métodos
        securedEnabled = true, // Habilita @Secured
        jsr250Enabled = true // Habilita @RolesAllowed
        // prePostEnabled = true // @PreAuthorize y @PostAuthorize están habilitadas por defecto con EnableMethodSecurity
)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Nuestro servicio para cargar usuarios

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler; // Nuestro manejador para accesos no autorizados

    /**
     * Define el bean para nuestro filtro JWT personalizado {@link JwtRequestFilter}.
     * Este filtro interceptará las solicitudes para validar el token JWT.
     * @return Una instancia de JwtRequestFilter.
     */
    @Bean
    public JwtRequestFilter authenticationJwtTokenFilter() {
        return new JwtRequestFilter();
    }

    /**
     * Configura el proveedor de autenticación principal ({@link DaoAuthenticationProvider}).
     * Utiliza nuestro {@link UserDetailsServiceImpl} para obtener los detalles del usuario
     * y el {@link PasswordEncoder} definido para verificar la contraseña.
     * @return Una instancia configurada de DaoAuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expone el {@link AuthenticationManager} como un bean de Spring.
     * Es necesario para procesar las solicitudes de autenticación, por ejemplo, en {@link AuthController}.
     * @param authConfig La configuración de autenticación de Spring.
     * @return El AuthenticationManager configurado.
     * @throws Exception Si ocurre un error al obtener el AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Define el bean para el codificador de contraseñas.
     * Se utiliza BCryptPasswordEncoder, que es el estándar recomendado actualmente.
     * @return Una instancia de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Define qué rutas son públicas, cuáles requieren autenticación, deshabilita CSRF,
     * establece la política de sesión como STATELESS (apropiado para APIs REST con JWT),
     * configura el manejo de excepciones de autenticación y añade el filtro JWT.
     *
     * @param http El objeto HttpSecurity para configurar la seguridad web.
     * @return El SecurityFilterChain configurado.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (Cross-Site Request Forgery) - Común en APIs stateless
                .csrf(AbstractHttpConfigurer::disable)
                // Configurar el manejo de excepciones de autenticación
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // Configurar la gestión de sesiones como STATELESS (no crear sesiones HTTP)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configurar la autorización de solicitudes HTTP
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso público a rutas de autenticación y documentación Swagger/OpenAPI
                        .requestMatchers(
                                "/api/auth/**",         // Endpoints de login/registro
                                "/swagger-ui.html",     // Acceso a la UI de Swagger
                                "/swagger-ui/**",       // Recursos estáticos de Swagger UI
                                "/v3/api-docs/**",      // Definición OpenAPI v3 (JSON/YAML)
                                "/api-docs/**",         // Alias común para /v3/api-docs
                                "/webjars/**"           // Dependencias webjars (usadas por Swagger UI)
                        ).permitAll()
                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                );

        // Registrar nuestro proveedor de autenticación personalizado
        http.authenticationProvider(authenticationProvider());

        // Añadir nuestro filtro JWT personalizado ANTES del filtro estándar de autenticación por usuario/contraseña
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Construir y devolver la cadena de filtros configurada
        return http.build();
    }
}