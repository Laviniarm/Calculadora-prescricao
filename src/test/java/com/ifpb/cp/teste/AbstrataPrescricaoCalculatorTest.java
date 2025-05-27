package com.ifpb.cp.teste;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.dto.SuspensaoDTO;
import com.ifpb.cp.enums.TipoPrescricao;
import com.ifpb.cp.enums.TipoSuspensao;
import com.ifpb.cp.service.calculo.impl.AbstrataPrescricaoCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstrataPrescricaoCalculatorTest {

    private final AbstrataPrescricaoCalculator calculator = new AbstrataPrescricaoCalculator();

    @Test
    public void calculaPrescricaoSemInterrupcaoOuSuspensao() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setNomeAcusado("João da Silva");
        dto.setNumeroProcesso("0001234-56.2025.8.15.0001");
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setDataNascimento(LocalDate.of(1980, 3, 20));
        dto.setPenaAnos(2);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);
        dto.setDataFato(LocalDate.of(2000, 1, 1));

        // sem causas interruptivas
        dto.setDataRecebimentoDaDenuncia(null);
        dto.setDataPronuncia(null);
        dto.setDataConfirmatoriaDaPronuncia(null);
        dto.setDataPublicacaoDaSentencaOuAcordao(null);
        dto.setDataInicioDoCumprimentoDaPena(null);
        dto.setDataContinuacaoDoCumprimentoDaPena(null);
        dto.setDataReincidencia(null);

        // sem suspensões
        dto.setSuspensoes(List.of());

        PrescricaoResponseDTO resp = calculator.calcular(dto);

        assertEquals("2 anos, 0 meses e 0 dias", resp.getPena());
        assertEquals("Entre 21 e 70 anos", resp.getFaixaEtaria());
        assertEquals("4 anos", resp.getPrazoPrescricional());
        assertEquals("31/12/2003", resp.getDataProvavel());
    }

    @Test
    public void calculaPrescricaoComSeisMesesDeSuspensao() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setNomeAcusado("Maria Souza");
        dto.setNumeroProcesso("0005678-90.2025.8.15.0001");
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setDataNascimento(LocalDate.of(1970, 12, 5));
        dto.setPenaAnos(2);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);
        dto.setDataFato(LocalDate.of(2000, 1, 1));

        // sem causas interruptivas
        dto.setDataRecebimentoDaDenuncia(null);
        dto.setDataPronuncia(null);
        dto.setDataConfirmatoriaDaPronuncia(null);
        dto.setDataPublicacaoDaSentencaOuAcordao(null);
        dto.setDataInicioDoCumprimentoDaPena(null);
        dto.setDataContinuacaoDoCumprimentoDaPena(null);
        dto.setDataReincidencia(null);

        // suspensao de 05/06/2002 a 05/12/2002 (~183 dias)
        SuspensaoDTO susp = new SuspensaoDTO();
        susp.setTipo(TipoSuspensao.PREJUDICIAL);
        susp.setInicio(LocalDate.of(2002, 6, 5));
        susp.setFim(LocalDate.of(2002, 12, 5));
        dto.setSuspensoes(List.of(susp));

        PrescricaoResponseDTO resp = calculator.calcular(dto);

        assertEquals("2 anos, 0 meses e 0 dias", resp.getPena());
        assertEquals("Entre 21 e 70 anos", resp.getFaixaEtaria());
        assertEquals("4 anos", resp.getPrazoPrescricional());
        assertEquals("01/07/2004", resp.getDataProvavel());
    }
}
