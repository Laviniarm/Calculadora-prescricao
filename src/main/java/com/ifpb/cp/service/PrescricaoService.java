package com.ifpb.cp.service;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.model.Prescricao;
import com.ifpb.cp.repository.PrescricaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PrescricaoService {
    private final PrescricaoRepository prescricaoRepository;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PrescricaoResponseDTO calcularPrescricao(PrescricaoRequestDTO dto) {
        String penaStr = String.format("%da%dm%dd", dto.getPenaAnos(), dto.getPenaMeses(), dto.getPenaDias());

        int idade = Period.between(dto.getDataNascimento(), dto.getDataFato()).getYears();
        String faixa = (idade <= 20 ? "Menos de 21 anos" :
                idade <= 70 ? "Entre 21 anos e 70 anos" :
                        "Acima de 70 anos");

        int totalMeses = dto.getPenaAnos() * 12 + dto.getPenaMeses();
        int anosPrazo = calcularPrazoEmAnos(totalMeses);
        String prazoStr = String.format("%da%dm%dd", anosPrazo, 0, 0);

        LocalDate dataProvavel = dto.getDataFato().plusYears(anosPrazo);
        String dataProvStr = dataProvavel.format(fmt);

        String resultado = prazoStr.equals("0a0m0d")
                ? "Imediato"
                : dataProvavel.isBefore(LocalDate.now()) ? "Prescrito" : "Não prescrito";

        Prescricao ent = new Prescricao(
                null,
                dto.getNomeAcusado(),
                dto.getNumeroProcesso(),
                dto.getDataNascimento(),
                dto.getTipoPrescricao(),
                dto.getPenaAnos(),
                dto.getPenaMeses(),
                dto.getPenaDias(),
                dto.getDataFato(),
                dto.getDataRecebimentoDaDenuncia(),
                dto.isCrimeTentado(),
                dto.isCausasAumento(),
                dto.isCausasReducao(),
                dto.isProcessoSuspenso(),
                dto.isTribunalJuri(),
                dto.getDataSentencaPronuncia(),
                dto.getDataAcordaoPronuncia(),
                dto.getDataSentencaCondenatoria(),
                dto.getDataAcordaoCondenatorio(),
                resultado,
                dataProvavel,
                LocalDate.now(),
                dto.getObservacao(),
                dto.getElaboradoPor()
        );

        prescricaoRepository.save(ent);
        return new PrescricaoResponseDTO(penaStr, faixa, prazoStr, dataProvStr);
    }


    private int calcularPrazoEmAnos(int totalMeses) {
        if (totalMeses > 144) return 20;
        if (totalMeses > 96) return 16;
        if (totalMeses > 48) return 12;
        if (totalMeses > 24) return 8;
        return 4;
    }

}
