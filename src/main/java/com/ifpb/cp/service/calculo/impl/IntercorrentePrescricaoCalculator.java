package com.ifpb.cp.service.calculo.impl;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.service.calculo.AbstractPrescricaoCalculator;
import com.ifpb.cp.service.calculo.AbstractPrescricaoCalculator.CustomResult;
import com.ifpb.cp.service.utils.VerificarSuspensao;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class IntercorrentePrescricaoCalculator extends AbstractPrescricaoCalculator {

    /**
     * Para intercorrente, a contagem só começa na publicação da sentença condenatória.
     */
    @Override
    protected LocalDate obterDataInicial(PrescricaoRequestDTO dto) {
        LocalDate pub = dto.getDataPublicacaoDaSentencaOuAcordao();
        if (pub == null) {
            throw new IllegalArgumentException(
                    "Para prescrição intercorrente, dataPublicacaoDaSentencaOuAcordao é obrigatória."
            );
        }
        return pub;
    }

    /**
     * Executa o cálculo específico de prescrição intercorrente:
     * - Verifica se o prazo (art.117) já foi consumido entre publicação e trânsito.
     * - Se consumido, devolve prescrição à data em que se completou o prazo.
     * - Caso contrário, retorna o prazo restante e reinicia a contagem a partir do trânsito,
     *   aplicando só as suspensões posteriores.
     */
    @Override
    protected CustomResult executarLogicaEspecifica(
            PrescricaoRequestDTO dto,
            LocalDate dataInicialPub,
            long prazoDiasBase,
            LocalDate dataLimiteInicialBase,
            long diasSuspensaoLimitadoBase) {

        LocalDate transito = dto.getDataTransitoEmJulgado();
        if (transito == null) {
            throw new IllegalArgumentException(
                    "Para prescrição intercorrente, dataTransitoEmJulgado é obrigatória."
            );
        }

        // 1) Dias efetivamente passados entre publicação e trânsito
        long diasDecorridos = ChronoUnit.DAYS.between(dataInicialPub, transito);

        if (diasDecorridos >= prazoDiasBase) {
            // 2a) Prazo já consumido antes do trânsito → prescrição ocorrida
            LocalDate dataPrescricao = dataInicialPub.plusDays(prazoDiasBase);
            return new CustomResult(
                    0,              // prazo restante
                    dataPrescricao, // data-limite = data de prescrição
                    0               // sem suspensão adicional
            );
        }

        // 2b) Ainda resta prazo: recomeça no trânsito
        long prazoRestante = prazoDiasBase - diasDecorridos;
        LocalDate novoLimiteBase = transito.plusDays(prazoRestante);

        // 3) Suspensões válidas após o trânsito
        long diasSuspensaoAposTransito = VerificarSuspensao.calcularDiasSuspensao(
                dto.getSuspensoes(),
                transito
        );
        long suspLimit = Math.min(diasSuspensaoAposTransito, prazoRestante);

        return new CustomResult(
                prazoRestante,
                novoLimiteBase,
                suspLimit
        );
    }
}
