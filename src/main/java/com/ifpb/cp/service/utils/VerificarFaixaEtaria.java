package com.ifpb.cp.service.utils;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Utilitário para verificar faixa etária do sujeito.
 */
public final class VerificarFaixaEtaria {

    private VerificarFaixaEtaria() {
        // evita instanciação
    }

    /**
     * Calcula a idade em anos completos entre a data de nascimento
     * e a data de referência (normalmente hoje ou outra data do processo).
     *
     * @param dataNascimento   data de nascimento do sujeito (não nula)
     * @param dataReferencia   data para cálculo da idade (não nula)
     * @return número de anos completos
     */
    public static int calcularIdade(LocalDate dataNascimento, LocalDate dataReferencia) {
        Objects.requireNonNull(dataNascimento, "dataNascimento não pode ser null");
        Objects.requireNonNull(dataReferencia, "dataReferencia não pode ser null");
        if (dataNascimento.isAfter(dataReferencia)) {
            throw new IllegalArgumentException("dataNascimento não pode ser após dataReferencia");
        }
        return Period.between(dataNascimento, dataReferencia).getYears();
    }

    /**
     * @return true se a idade for menor que 21 anos
     */
    public static boolean isMenorQue21(LocalDate dataNascimento, LocalDate dataReferencia) {
        return calcularIdade(dataNascimento, dataReferencia) < 21;
    }

    /**
     * @return true se a idade for maior que 70 anos
     */
    public static boolean isMaiorQue70(LocalDate dataNascimento, LocalDate dataReferencia) {
        return calcularIdade(dataNascimento, dataReferencia) > 70;
    }
}

