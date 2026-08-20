package br.edu.ufape.backend.autenticacao.security;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.edu.ufape.backend.comum.exception.ErroResponse;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final Environment env;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService,
            Environment env, ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.env = env;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                            .requestMatchers(
                                    "/api/v1/auth/cadastro",
                                    "/api/v1/auth/login",
                                    "/api/v1/auth/logout")
                            .permitAll();

                    if (env.acceptsProfiles(Profiles.of("dev"))) {
                        auth.requestMatchers("/h2-console/**").permitAll();
                    }

                    // Todas as rotas de atividade exigem o papel ESTUDANTE (acesso por rota explícito)
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/atividades").hasRole("ESTUDANTE");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/v1/atividades/**").hasRole("ESTUDANTE");
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/atividades", "/api/v1/atividades/progresso").hasRole("ESTUDANTE");

                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint()))
                .anonymous(anonymous -> anonymous.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic.disable());
        if (env.acceptsProfiles(Profiles.of("dev"))) {
            http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        }
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            ErroResponse erro = new ErroResponse(
                    "Acesso não autorizado. Faça login novamente.",
                    HttpStatus.UNAUTHORIZED.value());

            objectMapper.writeValue(response.getWriter(), erro);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}