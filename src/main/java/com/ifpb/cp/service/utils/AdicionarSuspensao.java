package com.ifpb.cp.service.utils;

import com.ifpb.cp.dto.SuspensaoDTO;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Utilitário para adicionar dias de suspensão ao período de prescrição.
 */
public final class AdicionarSuspensao {

    private AdicionarSuspensao() {
        // evita instanciação
    }

    /**
     * Adiciona ao período atual (em dias) o total de dias de todas as suspensões.
     *
     * @param suspensoes         lista de SuspensaoDTO, cada qual com início e fim
     * @param periodoAtualDias   período de prescrição já contado, em dias
     * @return novo período em dias, já somando as suspensões
     */
    public static long adicionar(List<SuspensaoDTO> suspensoes, long periodoAtualDias) {
        if (suspensoes == null || suspensoes.isEmpty()) {
            return periodoAtualDias;
        }

        long totalDiasSuspensao = 0;
        for (SuspensaoDTO s : suspensoes) {
            if (s.getInicio() != null && s.getFim() != null && !s.getFim().isBefore(s.getInicio())) {
                long dias = ChronoUnit.DAYS.between(s.getInicio(), s.getFim());
                totalDiasSuspensao += dias;
            }
        }

        return periodoAtualDias + totalDiasSuspensao;
    }
}
