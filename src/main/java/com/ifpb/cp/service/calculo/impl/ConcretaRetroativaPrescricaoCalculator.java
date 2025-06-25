package com.ifpb.cp.service.calculo.impl;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.service.calculo.AbstractPrescricaoCalculator;
import com.ifpb.cp.service.calculo.AbstractPrescricaoCalculator.CustomResult;
import com.ifpb.cp.service.utils.VerificarSuspensao;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@Component
public class ConcretaRetroativaPrescricaoCalculator extends AbstractPrescricaoCalculator {

    @Override
    protected LocalDate obterDataInicial(PrescricaoRequestDTO dto) {
        // A contagem começa na data do fato, mas será interrompida
        // pela publicação da sentença condenatória.
        return dto.getDataFato();
    }

    @Override
    protected CustomResult executarLogicaEspecifica(
            PrescricaoRequestDTO dto,
            LocalDate dataInicialFato,
            long prazoDiasBase,
            LocalDate dataLimiteInicialBase,   // = dataFato + prazoDiasBase
            long diasSuspensaoLimitadoBase     // suspensão a partir de dataFato
    ) {
        // 1) Data de publicação da sentença (interrupção retroativa obrigatória)
        LocalDate pubSentenca = dto.getDataPublicacaoDaSentencaOuAcordao();
        if (pubSentenca == null) {
            throw new IllegalArgumentException(
                    "Para prescrição retroativa, dataPublicacaoDaSentencaOuAcordao é obrigatória."
            );
        }

        // 2) Qual foi a última interrupção ANTES da publicação?
        LocalDate ultimaInterAntesPub = Stream.of(
                        dto.getDataRecebimentoDaDenuncia(),
                        dto.getDataPronuncia(),
                        dto.getDataConfirmatoriaDaPronuncia(),
                        // não inclui dataPublicacaoDaSentencaOuAcordao porque é a própria publicação
                        dto.getDataInicioDoCumprimentoDaPena(),
                        dto.getDataContinuacaoDoCumprimentoDaPena(),
                        dto.getDataReincidencia()
                )
                .filter(d -> d != null && (d.isBefore(pubSentenca) || d.equals(pubSentenca)))
                .max(LocalDate::compareTo)
                .orElse(dataInicialFato);

        // 3) Quantos dias decorrem entre essa última interrupção e a publicação?
        long diasDecorridos = ChronoUnit.DAYS.between(ultimaInterAntesPub, pubSentenca);

        if (diasDecorridos >= prazoDiasBase) {
            // Já se consumiu ttodo o prazo antes da publicação:
            LocalDate dataPrescricao = ultimaInterAntesPub.plusDays(prazoDiasBase);
            // Retornamos o prazo-base (não zero) para exibição:
            return new CustomResult(
                    prazoDiasBase,       // mantém o prazo original em dias
                    dataPrescricao,      // data-limite = data de prescrição
                    0                    // sem suspensão aplicável
            );
        }


        // 4) Caso contrário, resta prazo:
        long prazoRestante = prazoDiasBase - diasDecorridos;

        // 5) A partir da publicação, sem considerar suspensões anteriores
        LocalDate novoTermoInicial = pubSentenca;
        LocalDate novoLimiteBase = novoTermoInicial.plusDays(prazoRestante);

        // 6) Recalcula suspensões ocorridas APÓS a publicação
        long diasSuspensao = VerificarSuspensao.calcularDiasSuspensao(
                dto.getSuspensoes(),
                novoTermoInicial
        );
        long diasSuspLimit = Math.min(diasSuspensao, prazoRestante);

        return new CustomResult(
                prazoRestante,
                novoLimiteBase,
                diasSuspLimit
        );
    }
}
