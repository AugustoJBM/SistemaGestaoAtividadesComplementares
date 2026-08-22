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

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        UserDetailsService userDetailsService,
                        Environment env,
                        ObjectMapper objectMapper) {

                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.env = env;
                this.objectMapper = objectMapper;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> {
                                        auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();

                                        // Permite pre-flight CORS em todos os endpoints
                                        auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                                        // Rotas públicas
                                        auth.requestMatchers(
                                                        "/api/v1/auth/cadastro",
                                                        "/api/v1/auth/login",
                                                        "/api/v1/auth/logout",
                                                        "/api/v1/health",
                                                        "/api/v1/health/database").permitAll();

                                        if (env.acceptsProfiles(Profiles.of("dev"))) {
                                                auth.requestMatchers("/h2-console/**").permitAll();
                                        }

                                        // Rotas de atividades - ESTUDANTE
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/atividades")
                                                        .hasRole("ESTUDANTE");
                                        auth.requestMatchers(HttpMethod.DELETE, "/api/v1/atividades/**")
                                                        .hasRole("ESTUDANTE");
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/atividades",
                                                        "/api/v1/atividades/progresso").hasRole("ESTUDANTE");
                                        // Rotas de relatorios - ESTUDANTE
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/relatorios/atividades")
                                                        .hasRole("ESTUDANTE");

                                        auth.anyRequest().authenticated();
                                })
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(customAuthenticationEntryPoint()))
                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)
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

                        response.setContentType(
                                        MediaType.APPLICATION_JSON_VALUE);

                        response.setCharacterEncoding(
                                        StandardCharsets.UTF_8.name());

                        ErroResponse erro = new ErroResponse(
                                        "Acesso não autorizado. Faça login novamente.",
                                        HttpStatus.UNAUTHORIZED.value());

                        objectMapper.writeValue(
                                        response.getWriter(),
                                        erro);
                };
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Permite origens locais e qualquer subdomínio do Render com ou sem barra final
                configuration.setAllowedOriginPatterns(List.of(
                                "http://localhost:4200",
                                "http://localhost:*",
                                "https://*.onrender.com"));

                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(
                                List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
                configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}