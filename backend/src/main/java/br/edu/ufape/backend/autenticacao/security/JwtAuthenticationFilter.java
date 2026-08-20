package br.edu.ufape.backend.autenticacao.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.autenticacao.service.TokenBlacklistService;
import br.edu.ufape.backend.comum.exception.ErroResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtService jwtService;
        private final UserDetailsService userDetailsService;
        private final TokenBlacklistService tokenBlacklistService;
        private final ObjectMapper objectMapper;

        public JwtAuthenticationFilter(
                        JwtService jwtService,
                        UserDetailsService userDetailsService,
                        TokenBlacklistService tokenBlacklistService,
                        ObjectMapper objectMapper) {
                this.jwtService = jwtService;
                this.userDetailsService = userDetailsService;
                this.tokenBlacklistService = tokenBlacklistService;
                this.objectMapper = objectMapper;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                // Libera requisições preflight do CORS imediatamente
                if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                        filterChain.doFilter(request, response);
                        return;
                }

                String authHeader = request.getHeader("Authorization");

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        filterChain.doFilter(request, response);
                        return;
                }

                String token = authHeader.substring(7);

                if (!jwtService.isTokenValid(token)
                                || tokenBlacklistService.isTokenBlacklisted(token)) {
                        escreverErroUnauthorized(
                                        response,
                                        "Token inválido, expirado ou revogado");
                        return;
                }

                String username = jwtService.extractUsername(token);

                if (username != null
                                && SecurityContextHolder.getContext().getAuthentication() == null) {
                        try {
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                userDetails.getAuthorities());

                                authenticationToken.setDetails(
                                                new WebAuthenticationDetailsSource()
                                                                .buildDetails(request));

                                SecurityContextHolder.getContext()
                                                .setAuthentication(authenticationToken);

                        } catch (UsernameNotFoundException ex) {
                                escreverErroUnauthorized(
                                                response,
                                                "Usuário do token não encontrado");
                                return;
                        }
                }

                filterChain.doFilter(request, response);
        }

        private void escreverErroUnauthorized(
                        HttpServletResponse response,
                        String mensagem) throws IOException {

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());

                ErroResponse erro = new ErroResponse(
                                mensagem,
                                HttpStatus.UNAUTHORIZED.value());

                objectMapper.writeValue(response.getWriter(), erro);
        }
}