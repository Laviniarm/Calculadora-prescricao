package com.ifpb.cp.service.calculo.impl;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.service.calculo.AbstractPrescricaoCalculator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AbstrataPrescricaoCalculator extends AbstractPrescricaoCalculator {

    /**
     * Para prescrição abstrata, a contagem sempre começa na data do fato.
     */
    @Override
    protected LocalDate obterDataInicial(PrescricaoRequestDTO dto) {
        return dto.getDataFato();
    }

    /**
     * Não há ajuste adicional para prescrição abstrata:
     * usamos exatamente o prazo, dataLimite e suspensão já calculados no template.
     */
    @Override
    protected CustomResult executarLogicaEspecifica(
            PrescricaoRequestDTO dto,
            LocalDate dataInicial,
            long prazoDiasBase,
            LocalDate dataLimiteInicial,
            long diasSuspensaoLimitado) {

        return new CustomResult(
                prazoDiasBase,
                dataLimiteInicial,
                diasSuspensaoLimitado
        );
    }
}
