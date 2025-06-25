package com.ifpb.cp.service.calculo;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.service.utils.*;

import java.time.LocalDate;
import java.util.Optional;

public abstract class AbstractPrescricaoCalculator implements PrescricaoCalculator {

    @Override
    public final PrescricaoResponseDTO calcular(PrescricaoRequestDTO dto) {
        // 1) determina data inicial genérica (FATO ou TRÂNSITO, conforme subclasse)
        LocalDate dataInicial = obterDataInicial(dto);
        if (dataInicial == null) {
            throw new IllegalArgumentException("Data inicial não pode ser null");
        }

        // 2) interrupção comum
        Optional<LocalDate> interOpt = VerificarInterrupcao.verificarInterrupcao(
                dto.getDataRecebimentoDaDenuncia(),
                dto.getDataPronuncia(),
                dto.getDataConfirmatoriaDaPronuncia(),
                dto.getDataPublicacaoDaSentencaOuAcordao(),
                dto.getDataInicioDoCumprimentoDaPena(),
                dto.getDataContinuacaoDoCumprimentoDaPena(),
                dto.getDataReincidencia()
        );
        if (interOpt.isPresent()) {
            LocalDate d = interOpt.get();
            if (d.isAfter(dataInicial)) {
                dataInicial       = d;
            }
        }

        // 3) conversão de pena e cálculo de prazo-base
        long penaDias = ParaDias.converter(dto.getPenaAnos(), dto.getPenaMeses(), dto.getPenaDias());
        long prazoDiasBase = PrazoPrecricional.calcularPrazo(penaDias);

        // 4) faixa etária (comum)
        LocalDate hoje = LocalDate.now();
        if (VerificarFaixaEtaria.isMenorQue21(dto.getDataNascimento(), hoje)
                || VerificarFaixaEtaria.isMaiorQue70(dto.getDataNascimento(), hoje)) {
            prazoDiasBase /= 2;
        }

        // 5) suspensão bruta (comum)
        LocalDate dataLimiteInicial = dataInicial.plusDays(prazoDiasBase);
        long diasSuspBruto = VerificarSuspensao.calcularDiasSuspensao(dto.getSuspensoes(), dataInicial);
        long diasSuspLimit = Math.min(diasSuspBruto, prazoDiasBase);

        // 6) hook para ajustes específicos (ex.: recomeçar prazo, aplicar art.109 etc.)
        CustomResult custom = executarLogicaEspecifica(
                dto,
                dataInicial,
                prazoDiasBase,
                dataLimiteInicial,
                diasSuspLimit
        );

        // 7) data provável final e montagem de DTO
        LocalDate dataProvavel = custom.getDataLimiteInicial()
                .plusDays(custom.getDiasSuspensaoLimitado());

        return ResponseGenerator.gerar(
                dto.getPenaAnos(),
                dto.getPenaMeses(),
                dto.getPenaDias(),
                dto.getDataNascimento(),
                hoje,
                custom.getPrazoDiasAjustado(),
                dataProvavel
        );
    }

    /**
     * Cada subclasse define onde começa a contagem:
     * - Abstrata → data do fato
     * - Executória → data do trânsito em julgado
     * - Concreta retroativa → data do fato, mas com outro tratamento no hook
     */
    protected abstract LocalDate obterDataInicial(PrescricaoRequestDTO dto);

    /**
     * Ponto de extensão: todas as interrupções, faixas etárias e suspensões
     * já foram aplicadas no fluxo acima. Aqui a subclasse devolve:
     *  - prazo ajustado (pode ser = prazoDiasBase)
     *  - data-limite inicial (pode ser = dataLimiteInicial)
     *  - dias de suspensão limitado (pode ser = diasSuspLimit)
     */
    protected abstract CustomResult executarLogicaEspecifica(
            PrescricaoRequestDTO dto,
            LocalDate dataInicial,
            long prazoDiasBase,
            LocalDate dataLimiteInicial,
            long diasSuspensaoLimitado
    );

    /** Wrapper para retorno múltiplo do hook */
    public static class CustomResult {
        private final long prazoDiasAjustado;
        private final LocalDate dataLimiteInicial;
        private final long diasSuspensaoLimitado;

        public CustomResult(long prazoDiasAjustado,
                            LocalDate dataLimiteInicial,
                            long diasSuspensaoLimitado) {
            this.prazoDiasAjustado    = prazoDiasAjustado;
            this.dataLimiteInicial    = dataLimiteInicial;
            this.diasSuspensaoLimitado = diasSuspensaoLimitado;
        }

        public long getPrazoDiasAjustado() { return prazoDiasAjustado; }
        public LocalDate getDataLimiteInicial() { return dataLimiteInicial; }
        public long getDiasSuspensaoLimitado() { return diasSuspensaoLimitado; }
    }
}
