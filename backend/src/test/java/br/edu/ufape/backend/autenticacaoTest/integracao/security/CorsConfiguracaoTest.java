package br.edu.ufape.backend.autenticacaoTest.integracao.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest
class CorsConfiguracaoTest {

        private MockMvc mockMvc;

        @Autowired
        private WebApplicationContext context;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                .apply(SecurityMockMvcConfigurers.springSecurity())
                                .build();
        }

        @Test
        @DisplayName("Deve permitir pre-flight CORS para origem legitima http://localhost:4200 em rota de login")
        void devePermitirPreflightCorsParaOrigemLegitimaEmRotaDeLogin() throws Exception {
                mockMvc.perform(options("/api/v1/auth/login")
                                .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                                .andExpect(status().isOk())
                                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                                                "http://localhost:4200"))
                                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
        }

        @Test
        @DisplayName("Deve manter cabecalho Access-Control-Allow-Origin para origem legitima http://localhost:4200 em rota protegida")
        void deveManterCabecalhoAllowOriginParaOrigemLegitimaEmRotaProtegida() throws Exception {
                mockMvc.perform(options("/api/v1/atividades")
                                .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                                .andExpect(status().isOk())
                                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                                                "http://localhost:4200"))
                                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
        }

        @Test
        @DisplayName("Nao deve permitir pre-flight CORS para subdominio onrender.com nao configurado explicitamente")
        void naoDevePermitirPreflightCorsParaSubdominioOnrenderNaoConfigurado() throws Exception {
                mockMvc.perform(options("/api/v1/auth/login")
                                .header(HttpHeaders.ORIGIN, "https://malicious.onrender.com")
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, nullValue()));
        }

        @Test
        @DisplayName("Nao deve permitir pre-flight CORS para porta localhost nao configurada explicitamente")
        void naoDevePermitirPreflightCorsParaPortaLocalhostNaoConfigurada() throws Exception {
                mockMvc.perform(options("/api/v1/auth/login")
                                .header(HttpHeaders.ORIGIN, "http://localhost:9999")
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, nullValue()));
        }
}
