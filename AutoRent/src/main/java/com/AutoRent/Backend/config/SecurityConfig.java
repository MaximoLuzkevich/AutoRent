package com.AutoRent.Backend.config;

import com.AutoRent.Backend.security.JwtAuthenticationFilter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/login",
                                "/login.html",
                                "/registro",
                                "/registro.html",
                                "/admin",
                                "/cliente",
                                "/propietario-panel",
                                "/cliente-inicio.html",
                                "/auto-detalle.html",
                                "/cliente-reservas.html",
                                "/cliente-pagos.html",
                                "/cliente-perfil.html",
                                "/propietario-autos.html",
                                "/propietario-auto-detalle.html",
                                "/propietario-solicitudes.html",
                                "/propietario-pagos.html",
                                "/propietario-perfil.html",
                                "/admin-propietarios.html",
                                "/admin-propietario-detalle.html",
                                "/admin-pagos.html",
                                "/propietario.html",
                                "/auto-alta.html",
                                "/css/**",
                                "/js/**",
                                "/api/usuarios/registro",
                                "/api/usuarios/login",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/autos/me/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/autos/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/autos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/logout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/me").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/roles/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/pagos").hasRole("ADMINISTRADOR")
                        .requestMatchers(
                                "/api/pagos/*/aprobar",
                                "/api/pagos/*/rechazar",
                                "/api/pagos/*/estado/aprobado",
                                "/api/pagos/*/estado/rechazado"
                        ).hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/pagos/estado/**", "/api/pagos/fechas/**")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/propietarios/*/verificar").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/propietarios/activos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/propietarios/me").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/propietarios/*").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/autos/**").hasAnyRole("PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/autos/**").hasAnyRole("PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/autos/**").hasAnyRole("PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/autos/*/imagenes").hasAnyRole("PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/autos/*/imagenes/**").hasAnyRole("PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**")
                        .hasAnyRole("CLIENTE", "PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").hasAnyRole("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/reservas/**")
                        .hasAnyRole("CLIENTE", "PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/reservas/*/confirmar",
                                "/api/reservas/*/estado/confirmada"
                        ).hasAnyRole("PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/reservas/*/finalizar",
                                "/api/reservas/*/estado/finalizada"
                        ).hasRole("ADMINISTRADOR")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/reservas/*/cancelar",
                                "/api/reservas/*/estado/cancelada"
                        ).hasAnyRole("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/reservas/**")
                        .hasAnyRole("CLIENTE", "PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/**")
                        .hasAnyRole("CLIENTE", "PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/pagos", "/api/pagos/**")
                        .hasAnyRole("CLIENTE", "PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/pagos/**")
                        .hasAnyRole("CLIENTE", "PROPIETARIO", "ADMINISTRADOR")
                        .requestMatchers("/api/propietarios/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(parseAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private List<String> parseAllowedOrigins() {
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
