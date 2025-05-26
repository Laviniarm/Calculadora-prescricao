package com.ifpb.cp.service;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.enums.TipoPrescricao;
import com.ifpb.cp.repository.PrescricaoRepository;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PrescricaoServiceTest {

    @InjectMocks
    private PrescricaoService service;

    @Mock
    private PrescricaoRepository repository;

    @Test
    public void deveCalcularPrescricaoParaMaior70Anos() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATO);
        dto.setPenaAnos(12);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);
        dto.setDataNascimento(LocalDate.of(1940, 1, 1));
        dto.setDataFato(LocalDate.of(2020, 1, 1));

        PrescricaoResponseDTO resp = service.calcularPrescricao(dto);

        assertNotNull(resp);
        assertTrue(resp.getDataProvavel().contains("/")); // formato dd/MM/yyyy
    }
}