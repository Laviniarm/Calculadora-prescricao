package com.ifpb.cp.teste;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.enums.TipoPrescricao;
import com.ifpb.cp.enums.TipoSuspensao;
import com.ifpb.cp.dto.SuspensaoDTO;
import com.ifpb.cp.service.calculo.impl.ConcretaRetroativaPrescricaoCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcretaRetroativaPrescricaoCalculatorTest {

    private final ConcretaRetroativaPrescricaoCalculator calculator =
            new ConcretaRetroativaPrescricaoCalculator();

    @Test
    public void prescritoAntesDaPublicacao() {
        // Cenário: pena de 2 anos (prazo base = 4 anos = 1460 dias)
        // Data do fato: 01/01/2000
        // Recebimento da denúncia: 01/01/2000
        // Publicação da sentença: 01/01/2006 (mais de 1460 dias após denúncia)
        // Já prescrito: data provável = 01/01/2000 + 1460 dias = 01/01/2004

        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setNomeAcusado("Teste Prescrito");
        dto.setNumeroProcesso("0004");
        dto.setDataNascimento(LocalDate.of(1980, 1, 1));  // idade entre 21 e 70
        dto.setTipoPrescricao(TipoPrescricao.RETROATIVA);
        dto.setPenaAnos(2);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);
        dto.setDataFato(LocalDate.of(2000, 1, 1));
        dto.setDataRecebimentoDaDenuncia(LocalDate.of(2000, 1, 1));
        dto.setDataPublicacaoDaSentencaOuAcordao(LocalDate.of(2006, 1, 1));
        dto.setSuspensoes(List.of());
        // demais causas interruptivas nulas
        dto.setDataPronuncia(null);
        dto.setDataConfirmatoriaDaPronuncia(null);
        dto.setDataInicioDoCumprimentoDaPena(null);
        dto.setDataContinuacaoDoCumprimentoDaPena(null);
        dto.setDataReincidencia(null);

        PrescricaoResponseDTO resp = calculator.calcular(dto);

        // A pena permanece a mesma proposta pela request (2 anos)
        assertEquals("2 anos, 0 meses e 0 dias", resp.getPena());
        // O prazo prescricional também continua 4 anos
        assertEquals("4 anos", resp.getPrazoPrescricional());
        // Data provável deve ser 31/12/2003
        assertEquals("31/12/2003", resp.getDataProvavel());
    }
}
