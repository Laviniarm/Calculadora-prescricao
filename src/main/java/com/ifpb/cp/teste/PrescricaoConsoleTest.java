package com.ifpb.cp.teste;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.enums.TipoPrescricao;
import com.ifpb.cp.service.calculo.impl.AbstrataPrescricaoCalculator;

import java.time.LocalDate;
import java.util.List;

public class PrescricaoConsoleTest {

    public static void main(String[] args) {
        // 1) Monta o DTO
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setNomeAcusado("João da Silva");
        dto.setNumeroProcesso("0001234-56.2025.8.15.0001");
        dto.setDataNascimento(LocalDate.of(1980, 3, 20));
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setPenaAnos(2);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);
        dto.setDataFato(LocalDate.of(2000, 1, 1));

        // sem interrupções:
        dto.setDataRecebimentoDaDenuncia(LocalDate.of(2003, 3, 20));
        dto.setDataPronuncia(null);
        dto.setDataConfirmatoriaDaPronuncia(null);
        dto.setDataPublicacaoDaSentencaOuAcordao(null);
        dto.setDataInicioDoCumprimentoDaPena(null);
        dto.setDataContinuacaoDoCumprimentoDaPena(null);
        dto.setDataReincidencia(null);

        // sem suspensões
        dto.setSuspensoes(List.of());

        // 2) Instancia diretamente a estratégia de prescrição abstrata
        AbstrataPrescricaoCalculator calc = new AbstrataPrescricaoCalculator();

        // 3) Executa o cálculo
        PrescricaoResponseDTO resp = calc.calcular(dto);

        // 4) Imprime o resultado no console
        System.out.println("=== Resultado do Cálculo de Prescrição Abstrata ===");
        System.out.println("Pena:            " + resp.getPena());
        System.out.println("Faixa Etária:    " + resp.getFaixaEtaria());
        System.out.println("Prazo Prescr.:   " + resp.getPrazoPrescricional());
        System.out.println("Data Provável:   " + resp.getDataProvavel());
    }
}
