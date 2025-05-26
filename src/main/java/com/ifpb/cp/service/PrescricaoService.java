package com.ifpb.cp.service;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.enums.TipoPrescricao;
import com.ifpb.cp.model.Prescricao;
import com.ifpb.cp.repository.PrescricaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrescricaoService {

    private final PrescricaoRepository prescricaoRepository;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PrescricaoResponseDTO calcularPrescricao(PrescricaoRequestDTO dto) {
        int penaMeses = dto.getPenaAnos() * 12 + dto.getPenaMeses();
        if (dto.getPenaDias() >= 15) penaMeses++; // arredondamento

        int prazo = calcularPrazoPrescricao(penaMeses);

        // Reduções ou aumentos legais
        if (isMenor21ouMaior70(dto, dto.getTipoPrescricao())) prazo /= 2;
        if (dto.isCrimeTentado()) prazo /= 2;
        if (dto.isCausasAumento()) prazo *= 1.5;
        if (dto.isCausasReducao()) prazo *= 0.66;

        // Data base conforme o tipo
        LocalDate dataBase = (dto.getTipoPrescricao() == TipoPrescricao.ABSTRATO)
                ? dto.getDataFato()
                : Optional.ofNullable(dto.getDataAcordaoCondenatorio())
                .orElse(dto.getDataSentencaCondenatoria());

        LocalDate dataPrescricao = dataBase.plusYears((int) prazo);

        String status = dataPrescricao.isBefore(LocalDate.now()) ? "Prescrito" : "Não prescrito";

        Prescricao prescricao = new Prescricao(
                null, dto.getNomeAcusado(), dto.getNumeroProcesso(), dto.getDataNascimento(),
                dto.getTipoPrescricao(), dto.getPenaAnos(), dto.getPenaMeses(), dto.getPenaDias(),
                dto.getDataFato(), dto.getDataRecebimentoDaDenuncia(), dto.isCrimeTentado(), dto.isCausasAumento(),
                dto.isCausasReducao(), dto.isProcessoSuspenso(), dto.isTribunalJuri(),
                dto.getDataSentencaPronuncia(), dto.getDataAcordaoPronuncia(),
                dto.getDataSentencaCondenatoria(), dto.getDataAcordaoCondenatorio(),
                status, dataPrescricao, LocalDate.now(), dto.getObservacao(), dto.getElaboradoPor()
        );

        prescricaoRepository.save(prescricao);

        return new PrescricaoResponseDTO(
                formatarPena(dto), faixaEtaria(dto.getDataNascimento(), dataBase),
                prazo + " anos", fmt.format(dataPrescricao)
        );
    }

    private boolean isMenor21ouMaior70(PrescricaoRequestDTO dto, TipoPrescricao tipo) {
        LocalDate base = (tipo == TipoPrescricao.ABSTRATO) ? dto.getDataFato()
                : Optional.ofNullable(dto.getDataAcordaoCondenatorio())
                .orElse(dto.getDataSentencaCondenatoria());
        int idade = Period.between(dto.getDataNascimento(), base).getYears();
        return idade < 21 || idade >= 70;
    }

    private int calcularPrazoPrescricao(int penaMeses) {
        int anos = penaMeses / 12;
        if (anos > 12) return 20;
        if (anos > 8) return 16;
        if (anos > 4) return 12;
        if (anos > 2) return 8;
        if (anos > 1) return 4;
        return 3;
    }

    private String faixaEtaria(LocalDate nascimento, LocalDate base) {
        int idade = Period.between(nascimento, base).getYears();
        if (idade < 21) return "Menor de 21 anos";
        if (idade >= 70) return "Maior de 70 anos";
        return "Entre 21 e 69 anos";
    }

    private String formatarPena(PrescricaoRequestDTO dto) {
        return String.format("%da%dm%dd", dto.getPenaAnos(), dto.getPenaMeses(), dto.getPenaDias());
    }
}
