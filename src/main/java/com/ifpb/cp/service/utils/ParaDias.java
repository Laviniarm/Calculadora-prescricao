package com.ifpb.cp.service.utils;

import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 * Utilitário para converter períodos dados em anos, meses e dias
 * para um total aproximado em dias.
 */
public final class ParaDias {

    private static final int DIAS_POR_ANO = 365;
    private static final int DIAS_POR_MES = 30;

    // Construtor privado para evitar instanciação
    private ParaDias() {
    }

    /**
     * Converte anos, meses e dias em um total aproximado de dias.
     *
     * @param anos   número de anos
     * @param meses  número de meses
     * @param dias   número de dias
     * @return total de dias equivalente
     */
    public static long converter(int anos, int meses, int dias) {
        return (long) anos * DIAS_POR_ANO
                + (long) meses * DIAS_POR_MES
                + dias;
    }

    /**
     * Converte um objeto Period em um total aproximado de dias.
     *
     * @param periodo Period contendo anos, meses e dias
     * @return total de dias equivalente
     */
    public static long converter(Period periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("Período não pode ser null");
        }
        return converter(periodo.getYears(), periodo.getMonths(), periodo.getDays());
    }

    /**
     * Converte um objeto Period em dias exatos, usando ChronoUnit
     * se associado a uma data de referência.
     *
     * @param periodo        Period contendo anos, meses e dias
     * @param dataReferencia data de início para cálculo exato
     * @return número de dias reais entre dataReferencia e dataReferencia+periodo
     */
    public static long converterExato(Period periodo, java.time.LocalDate dataReferencia) {
        if (periodo == null || dataReferencia == null) {
            throw new IllegalArgumentException("Periodo e data de referência não podem ser null");
        }
        return ChronoUnit.DAYS.between(dataReferencia, dataReferencia.plus(periodo));
    }
}

