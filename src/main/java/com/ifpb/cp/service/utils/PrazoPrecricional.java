package com.ifpb.cp.service.utils;

/**
 * Calcula o prazo prescricional (em dias) a partir da pena máxima (em dias).
 */
public final class PrazoPrecricional {

    private static final int DIAS_POR_ANO = 365;

    private PrazoPrecricional() {
        // evita instanciação
    }

    /**
     * Retorna o prazo prescricional, em dias, de acordo com o máximo da pena.
     *
     * @param penaEmDias pena máxima, em dias
     * @return prazo prescricional correspondente, em dias
     */
    public static long calcularPrazo(long penaEmDias) {
        if (penaEmDias > 12L * DIAS_POR_ANO) {
            // I - >12 anos → 20 anos
            return 20L * DIAS_POR_ANO;
        }
        else if (penaEmDias > 8L * DIAS_POR_ANO) {
            // II - >8 e ≤12 → 16 anos
            return 16L * DIAS_POR_ANO;
        }
        else if (penaEmDias > 4L * DIAS_POR_ANO) {
            // III - >4 e ≤8 → 12 anos
            return 12L * DIAS_POR_ANO;
        }
        else if (penaEmDias > 2L * DIAS_POR_ANO) {
            // IV - >2 e ≤4 → 8 anos
            return 8L * DIAS_POR_ANO;
        }
        else if (penaEmDias >= 1L * DIAS_POR_ANO) {
            // V - ≥1 e ≤2 → 4 anos
            return 4L * DIAS_POR_ANO;
        }
        else {
            // VI - <1 ano → 3 anos
            return 3L * DIAS_POR_ANO;
        }
    }
}
