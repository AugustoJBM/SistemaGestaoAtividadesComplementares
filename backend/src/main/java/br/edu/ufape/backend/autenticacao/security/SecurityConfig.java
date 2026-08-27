package br.edu.ufape.backend.autenticacao.security;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
        private final List<String> corsAllowedOrigins;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        UserDetailsService userDetailsService,
                        Environment env,
                        ObjectMapper objectMapper,
                        @Value("${app.cors.allowed-origins}") List<String> corsAllowedOrigins) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.env = env;
                this.objectMapper = objectMapper;
                this.corsAllowedOrigins = corsAllowedOrigins;
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

                                        // Rotas publicas de autenticação e saúde do sistema
                                        auth.requestMatchers(
                                                        "/api/v1/auth/cadastro",
                                                        "/api/v1/auth/login",
                                                        "/api/v1/auth/logout",
                                                        "/api/v1/health",
                                                        "/api/v1/health/database").permitAll();

                                        if (env.acceptsProfiles(Profiles.of("dev"))) {
                                                auth.requestMatchers("/h2-console/**").permitAll();
                                        }

                                        // Avalia e homologa atividades somente administradores e avaliadores
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/atividades/*/avaliar")
                                                        .hasAnyRole("AVALIADOR", "ADMINISTRADOR");

                                        // 2. rotas de gestao de regulamentos (PPC/RAG) - ADMINISTRADOR e AVALIADOR
                                        auth.requestMatchers("/api/v1/regulamentos", "/api/v1/regulamentos/**")
                                                        .hasAnyRole("ADMINISTRADOR", "AVALIADOR");

                                        // 3. rotas de metricas de pesquisa e auditoria - ADMINISTRADOR e AVALIADOR
                                        auth.requestMatchers("/api/v1/metricas-pesquisa",
                                                        "/api/v1/metricas-pesquisa/**")
                                                        .hasAnyRole("ADMINISTRADOR", "AVALIADOR");

                                        // 4. rotas de atividades, cetificados e IA associadas ao Estudante
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/atividades/extrair-certificado")
                                                        .hasRole("ESTUDANTE");

                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/atividades",
                                                        "/api/v1/atividades/**")
                                                        .hasRole("ESTUDANTE");

                                        auth.requestMatchers(HttpMethod.PUT, "/api/v1/atividades/**")
                                                        .hasRole("ESTUDANTE");

                                        auth.requestMatchers(HttpMethod.DELETE, "/api/v1/atividades/**")
                                                        .hasRole("ESTUDANTE");

                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/atividades",
                                                        "/api/v1/atividades/**", "/api/v1/atividades/progresso")
                                                        .hasRole("ESTUDANTE");

                                        // 5. Rotas de relatorios - ESTUDANTE
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/relatorios",
                                                        "/api/v1/relatorios/**")
                                                        .hasRole("ESTUDANTE");

                                        // Rotas de solicitacoes
                                        auth.requestMatchers(HttpMethod.PATCH, "/api/v1/solicitacoes/*/avaliacao")
                                                        .hasRole("AVALIADOR");
                                        auth.requestMatchers(HttpMethod.POST, "/api/v1/solicitacoes")
                                                        .hasRole("ESTUDANTE");
                                        auth.requestMatchers(HttpMethod.GET, "/api/v1/solicitacoes",
                                                        "/api/v1/solicitacoes/{id:[0-9]+}")
                                                        .hasRole("ESTUDANTE");

                                        // outras rotas autenticadas
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
                configuration.setAllowedOrigins(corsAllowedOrigins);
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