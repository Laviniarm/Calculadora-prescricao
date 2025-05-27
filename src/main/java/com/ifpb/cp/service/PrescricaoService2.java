package com.ifpb.cp.service;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.repository.PrescricaoRepository;
import com.ifpb.cp.service.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrescricaoService2 {

    private final PrescricaoRepository prescricaoRepository;

    public PrescricaoResponseDTO calcularPrescricao(PrescricaoRequestDTO dto) {
        // 1) converte pena abstrata para dias
        long penaMaximaEmDias = ParaDias.converter(
                dto.getPenaAnos(),
                dto.getPenaMeses(),
                dto.getPenaDias()
        );

        // 2) data inicial é a data do fato
        LocalDate dataInicial = dto.getDataFato();

        // 3) checa interrupções e, se alguma for posterior a dataInicial, reinicia o termo
        Optional<LocalDate> dataInterrupcaoOpt = VerificarInterrupcao.verificarInterrupcao(
                dto.getDataRecebimentoDaDenuncia(),
                dto.getDataPronuncia(),
                dto.getDataConfirmatoriaDaPronuncia(),
                dto.getDataPublicacaoDaSentencaOuAcordao(),
                dto.getDataInicioDoCumprimentoDaPena(),
                dto.getDataContinuacaoDoCumprimentoDaPena(),
                dto.getDataReincidencia()
        );

        LocalDate ultimaInterrupcao = null;
        if (dataInterrupcaoOpt.isPresent()) {
            LocalDate d = dataInterrupcaoOpt.get();
            if (d.isAfter(dataInicial)) {
                ultimaInterrupcao = d;
                dataInicial       = d;
            }
        }

        // 4) prazo inicial em dias pelo art.117
        long prazoPrescricionalDias = PrazoPrecricional.calcularPrazo(penaMaximaEmDias);

        // 5) aplicação da redução para menores de 21 ou maiores de 70
        LocalDate hoje = LocalDate.now();
        if (VerificarFaixaEtaria.isMenorQue21(dto.getDataNascimento(), hoje)
                || VerificarFaixaEtaria.isMaiorQue70(dto.getDataNascimento(), hoje)) {
            prazoPrescricionalDias /= 2;
        }

        // 6) data‐limite antes da suspensão
        LocalDate dataLimiteInicial = dataInicial.plusDays(prazoPrescricionalDias);

        // 7) dias de suspensão válidos (após última interrupção) e limitados ao próprio prazo
        long diasSuspensao = VerificarSuspensao.calcularDiasSuspensao(
                dto.getSuspensoes(),
                ultimaInterrupcao
        );
        long diasSuspensaoLimitado = Math.min(diasSuspensao, prazoPrescricionalDias);

        // 8) data‐limite final incluindo suspensão
        LocalDate dataProvavel = dataLimiteInicial.plusDays(diasSuspensaoLimitado);

        // 9) monta o DTO de resposta
        return ResponseGenerator.gerar(
                dto.getPenaAnos(),
                dto.getPenaMeses(),
                dto.getPenaDias(),
                dto.getDataNascimento(),
                hoje,
                prazoPrescricionalDias,
                dataProvavel
        );
    }
}