package com.ifpb.cp.service;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.repository.PrescricaoRepository;
import com.ifpb.cp.service.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrescricaoService2 {

    private final PrescricaoRepository prescricaoRepository;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PrescricaoResponseDTO calcularPrescricao(PrescricaoRequestDTO dto) {
        // prescrição em abstrato
        // 1 passo: verificar o período em dias da prescrição segundo a tabela, considerando a pena em abstrato do crime sem causas de aumento ou diminuição de pena
        long penaMaximaEmAbstrato = ParaDias.converter(dto.getPenaAnos(), dto.getPenaMeses(), dto.getPenaDias());

        // 2 passo: verificar a data inicial da prescrição - SEMPRE A DATA DO FATO
        LocalDate dataInicial = dto.getDataFato();
        LocalDate dataFinal = dataInicial.plusDays(1);

        // 3º passo: verificar causas de interrupção
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
                dataInicial      = d;
            }
        }

        // 4º passo: calcular prazo prescricional inicial em dias, a partir da pena abstrata
        long prazoPrescricionalDias = PrazoPrecricional.calcularPrazo(penaMaximaEmAbstrato);

        // 4,5 verificara a faixa etária
        LocalDate dataAtual = LocalDate.now();
        if (VerificarFaixaEtaria.isMenorQue21(dto.getDataNascimento(), dto.getDataFato())
                || VerificarFaixaEtaria.isMaiorQue70(dto.getDataNascimento(), dataAtual)) {
            prazoPrescricionalDias = prazoPrescricionalDias / 2;
        }


        // 5º passo: calcular data‐limite *antes* da suspensão
        LocalDate dataLimiteInicial = dataInicial.plusDays(prazoPrescricionalDias);

        // 6º passo: calcular só os dias de suspensão ocorridos após a última interrupção
        long diasSuspensao = VerificarSuspensao.calcularDiasSuspensao(
                dto.getSuspensoes(),   // sua List<SuspensaoDTO>
                ultimaInterrupcao      // pode ser null
        );

        // limita a suspensão ao prazo prescricional
        long diasSuspensaoLimitado = Math.min(diasSuspensao, prazoPrescricionalDias);

        // 7º passo: ajustar a data‐limite final incluindo a suspensão limitada
        LocalDate dataLimiteFinal = dataLimiteInicial.plusDays(diasSuspensaoLimitado);


        PrescricaoResponseDTO response = ResponseGenerator.gerar(
                dto.getPenaAnos(),
                dto.getPenaMeses(),
                dto.getPenaDias(),
                dto.getDataNascimento(),
                LocalDate.now(),
                prazoPrescricionalDias,
                dataLimiteFinal
        );

        return response;

    }

}
