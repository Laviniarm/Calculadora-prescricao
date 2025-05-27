package com.ifpb.cp.service.utils;

import com.ifpb.cp.dto.SuspensaoDTO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * Utilitário para calcular os dias de suspensão válidos
 * (após a última interrupção) num prazo prescricional.
 */
public final class VerificarSuspensao {

    private VerificarSuspensao() {
        // evita instanciação
    }

    /**
     * Soma todos os dias de suspensão cujas datas de início
     * sejam **posteriores** à última interrupção informada.
     *
     * @param suspensoes         lista de SuspensaoDTO com início e fim
     * @param ultimaInterrupcao  data da última interrupção (pode ser null)
     * @return total de dias de suspensão a considerar
     */
    public static long calcularDiasSuspensao(
            List<SuspensaoDTO> suspensoes,
            LocalDate ultimaInterrupcao) {

        if (suspensoes == null || suspensoes.isEmpty()) {
            return 0;
        }

        long total = 0;
        for (SuspensaoDTO s : suspensoes) {
            LocalDate inicio = s.getInicio();
            LocalDate fim    = s.getFim();
            if (Objects.nonNull(inicio)
                    && Objects.nonNull(fim)
                    && !fim.isBefore(inicio)
                    // só conta se não houver última interrupção,
                    // ou se o início for **strictly** depois dela
                    && (ultimaInterrupcao == null
                    || inicio.isAfter(ultimaInterrupcao))) {

                // ChronoUnit.DAYS.between conta (fim – início)
                total += ChronoUnit.DAYS.between(inicio, fim);
            }
        }
        return total;
    }
}

