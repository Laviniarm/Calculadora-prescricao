package com.ifpb.cp.service.calculo.impl;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.service.calculo.AbstractPrescricaoCalculator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ExecutoriaPrescricaoCalculator extends AbstractPrescricaoCalculator {

    /**
     * Aqui definimos que, para executória, a contagem sempre inicia
     * na data do trânsito em julgado informada pelo usuário.
     */
    @Override
    protected LocalDate obterDataInicial(PrescricaoRequestDTO dto) {
        LocalDate transito = dto.getDataTransitoEmJulgado();
        if (transito == null) {
            throw new IllegalArgumentException(
                    "Para prescrição executória, dataTransitoEmJulgado é obrigatória."
            );
        }
        return transito;
    }

    /**
     * Não há ajuste adicional depois do fluxo comum:
     * - interrupção e suspensão já foram aplicadas na super-classe
     * - mantemos o prazo-base e as datas calculadas
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
