package br.edu.ufape.backend.atividadeTest.integracao.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ufape.backend.atividade.controller.AtividadeController;
import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.atividade.dto.AtualizarAtividadeRequestDTO;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.exception.AtividadeNaoEncontradaException;
import br.edu.ufape.backend.atividade.facade.AtividadeFacade;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AtividadeControllerPutIntegrationTest {

        private MockMvc mockMvc;

        @Mock
        private AtividadeFacade atividadeFacade;

        @InjectMocks
        private AtividadeController atividadeController;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(atividadeController).build();
        }

        @Test
        @DisplayName("Deve retornar 200 e dados atualizados ao editar atividade própria com sucesso")
        void deveRetornar200AoEditarAtividadeComSucesso() throws Exception {
                AtividadeResponseDTO responseMock = new AtividadeResponseDTO(
                                1L,
                                "Título Atualizado",
                                "UFAPE",
                                LocalDate.of(2026, 5, 10),
                                20,
                                Natureza.ACC,
                                Categoria.ENSINO,
                                LocalDateTime.of(2026, 5, 1, 10, 0),
                                "estudante@ufape.edu.br");

                Mockito.when(
                                atividadeFacade.atualizarAtividade(
                                                eq(1L),
                                                any(AtualizarAtividadeRequestDTO.class),
                                                any(),
                                                eq("estudante@ufape.edu.br")))
                                .thenReturn(responseMock);

                MockMultipartFile arquivo = new MockMultipartFile(
                                "arquivo",
                                "novo_certificado.pdf",
                                MediaType.APPLICATION_PDF_VALUE,
                                "conteudo".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart(
                                                HttpMethod.PUT,
                                                "/api/v1/atividades/1")
                                                .file(arquivo)
                                                .principal(new UsernamePasswordAuthenticationToken(
                                                                "estudante@ufape.edu.br", "password"))
                                                .param("titulo", "Título Atualizado")
                                                .param("instituicaoResponsavel", "UFAPE")
                                                .param("dataRealizacao", "2026-05-10")
                                                .param("cargaHoraria", "20")
                                                .param("natureza", "ACC")
                                                .param("categoria", "ENSINO"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.titulo").value("Título Atualizado"))
                                .andExpect(jsonPath("$.cargaHorariaEmHoras").value(20));
        }

        @Test
        @DisplayName("Deve retornar 403 quando a atividade pertencer a outro estudante")
        void deveRetornar403QuandoAtividadePertencerAOutroEstudante() throws Exception {
                Mockito.when(
                                atividadeFacade.atualizarAtividade(
                                                eq(1L),
                                                any(AtualizarAtividadeRequestDTO.class),
                                                nullable(MultipartFile.class),
                                                eq("outro.estudante@ufape.edu.br")))
                                .thenThrow(
                                                new AcessoNegadoAtividadeException(
                                                                "Você não tem permissão para editar esta atividade."));

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart(
                                                HttpMethod.PUT,
                                                "/api/v1/atividades/1")
                                                .principal(new UsernamePasswordAuthenticationToken(
                                                                "outro.estudante@ufape.edu.br", "password"))
                                                .param("titulo", "Tentativa Indevida")
                                                .param("dataRealizacao", "2026-05-10")
                                                .param("cargaHoraria", "10")
                                                .param("natureza", "ACC")
                                                .param("categoria", "ENSINO"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 404 quando o ID da atividade não existir")
        void deveRetornar404QuandoAtividadeNaoExistir() throws Exception {
                Mockito.when(
                                atividadeFacade.atualizarAtividade(
                                                eq(999L),
                                                any(AtualizarAtividadeRequestDTO.class),
                                                nullable(MultipartFile.class),
                                                eq("estudante@ufape.edu.br")))
                                .thenThrow(
                                                new AtividadeNaoEncontradaException(
                                                                "Atividade não encontrada."));

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart(
                                                HttpMethod.PUT,
                                                "/api/v1/atividades/999")
                                                .principal(new UsernamePasswordAuthenticationToken(
                                                                "estudante@ufape.edu.br", "password"))
                                                .param("titulo", "Título")
                                                .param("dataRealizacao", "2026-05-10")
                                                .param("cargaHoraria", "10")
                                                .param("natureza", "ACC")
                                                .param("categoria", "ENSINO"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 400 quando campos obrigatórios estiverem inválidos")
        void deveRetornar400QuandoDadosForemInvalidos() throws Exception {
                mockMvc.perform(
                                MockMvcRequestBuilders.multipart(
                                                HttpMethod.PUT,
                                                "/api/v1/atividades/1")
                                                .principal(new UsernamePasswordAuthenticationToken(
                                                                "estudante@ufape.edu.br", "password"))
                                                .param("titulo", "")
                                                .param("cargaHoraria", "0")
                                                .param("natureza", "")
                                                .param("categoria", ""))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 401 quando o estudante não estiver autenticado")
        void deveRetornar401QuandoNaoAutenticado() throws Exception {
                mockMvc.perform(
                                MockMvcRequestBuilders.multipart(
                                                HttpMethod.PUT,
                                                "/api/v1/atividades/1")
                                                .param("titulo", "Título Válido")
                                                .param("dataRealizacao", "2026-05-10")
                                                .param("cargaHoraria", "10")
                                                .param("natureza", "ACC")
                                                .param("categoria", "ENSINO"))
                                .andExpect(status().isUnauthorized());
        }
}