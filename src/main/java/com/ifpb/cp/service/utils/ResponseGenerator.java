package com.ifpb.cp.service.utils;

import com.ifpb.cp.dto.PrescricaoResponseDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class ResponseGenerator {

    private static final DateTimeFormatter FORMATADOR_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ResponseGenerator() { }

    /**
     * Gera o DTO de resposta usando os componentes de tempo já fornecidos:
     *
     * @param penaAnos      anos da pena (da request DTO)
     * @param penaMeses     meses da pena (da request DTO)
     * @param penaDias      dias da pena (da request DTO)
     * @param dataNascimento data de nascimento do sujeito
     * @param referenciaData data para cálculo de idade
     * @param prazoPrescricionalDias prazo prescricional em dias
     * @param dataLimiteFinal data provável de prescrição
     */
    public static PrescricaoResponseDTO gerar(
            int penaAnos,
            int penaMeses,
            int penaDias,
            LocalDate dataNascimento,
            LocalDate referenciaData,
            long prazoPrescricionalDias,
            LocalDate dataLimiteFinal) {

        // 1) Formata pena a partir dos três componentes
        String pena = String.format("%d anos, %d meses e %d dias",
                penaAnos, penaMeses, penaDias);

        // 2) Calcula faixa etária (como antes)
        int idade = VerificarFaixaEtaria.calcularIdade(dataNascimento, referenciaData);
        String faixaEtaria;
        if (idade < 21) {
            faixaEtaria = "Menor de 21 anos";
        } else if (idade > 70) {
            faixaEtaria = "Maior de 70 anos";
        } else {
            faixaEtaria = "Entre 21 e 70 anos";
        }

        // 3) Formata o prazo prescricional em anos inteiros
        long anosPrazo = prazoPrescricionalDias / 365;
        String prazoPrescricional = anosPrazo + " anos";

        // 4) Formata data provável
        String dataProvavel = dataLimiteFinal.format(FORMATADOR_DATA);

        return new PrescricaoResponseDTO(pena, faixaEtaria, prazoPrescricional, dataProvavel);
    }
}


