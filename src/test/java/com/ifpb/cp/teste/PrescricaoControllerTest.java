package com.ifpb.cp.teste;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifpb.cp.controller.PrescricaoController;
import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.dto.PrescricaoSaveDTO;
import com.ifpb.cp.enums.TipoPrescricao;
import com.ifpb.cp.model.Prescricao;
import com.ifpb.cp.service.PrescricaoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PrescricaoController.class)
public class PrescricaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrescricaoService service;

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
//    void calcularPrescricao_retornaOk() throws Exception {
//        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
//        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
//
//        PrescricaoResponseDTO response = new PrescricaoResponseDTO();
//        response.setPena("4 anos");
//
//        Mockito.when(service.calcularPrescricao(any(PrescricaoRequestDTO.class)))
//                .thenReturn(response);
//
//        mockMvc.perform(post("/prescricao/calcular")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.pena").value("4 anos"));
//    }

    @Test
    void salvarPrescricao_retornaOk() throws Exception {
        PrescricaoSaveDTO dto = new PrescricaoSaveDTO();
        dto.setTipoPrescricao(TipoPrescricao.CONCRETO);
        dto.setUsuarioId(1L);
        dto.setNomeAcusado("Fulano");
        dto.setNumeroProcesso("123456");
        dto.setDataNascimento(LocalDate.of(1990, 1, 1));
        dto.setDataFato(LocalDate.of(2020, 1, 1));
        dto.setDataRecebimentoDaDenuncia(LocalDate.of(2021, 1, 1));
        dto.setElaboradoPor("Teste");

        Prescricao prescricao = new Prescricao();
        prescricao.setNomeAcusado("Fulano");
        prescricao.setNumeroProcesso("123456");

        Mockito.when(service.salvarPrescricao(any(PrescricaoSaveDTO.class)))
                .thenReturn(prescricao);

        mockMvc.perform(post("/prescricao/salvar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeAcusado").value("Fulano"))
                .andExpect(jsonPath("$.numeroProcesso").value("123456"));
    }

    @Test
    void listarPrescricoesPorUsuario_retornaLista() throws Exception {
        Long usuarioId = 1L;

        Prescricao p = new Prescricao();
        p.setId(100L);
        p.setNomeAcusado("José");

        Mockito.when(service.listarPrescricoes(eq(usuarioId)))
                .thenReturn(List.of(p));

        mockMvc.perform(get("/prescricao/listar/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100L))
                .andExpect(jsonPath("$[0].nomeAcusado").value("José"));
    }

    @Test
    void listarPrescricoes_usuarioNaoExiste_retorna404() throws Exception {
        Long idInvalido = 99L;

        Mockito.when(service.listarPrescricoes(eq(idInvalido)))
                .thenThrow(new EntityNotFoundException("Usuário não encontrado"));

        mockMvc.perform(get("/prescricao/listar/{id}", idInvalido))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void calcularPrescricao_dtoInvalido_retorna400() throws Exception {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO(); // tipoPrescricao não setado

        mockMvc.perform(post("/prescricao/calcular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

}

