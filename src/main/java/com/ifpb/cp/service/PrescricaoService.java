package com.ifpb.cp.service;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.service.calculo.PrescricaoCalculator;
import com.ifpb.cp.service.calculo.impl.AbstrataPrescricaoCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrescricaoService {

    @Autowired
    private AbstrataPrescricaoCalculator abstrataCalculator;

    // Se houver outras modalidades, injete-as aqui…

    public PrescricaoResponseDTO calcularPrescricao(PrescricaoRequestDTO dto) {
        PrescricaoCalculator calculator;
        if (dto.getTipoPrescricao() == null) {
            throw new IllegalArgumentException("O tipo de prescriçao não pode ser nulo");
        }
        switch (dto.getTipoPrescricao()) {
            case ABSTRATA:
                calculator = abstrataCalculator;
                break;

            default:
                throw new IllegalArgumentException(
                        "Tipo de prescrição inválido: " + dto.getTipoPrescricao()
                );
        }

        return calculator.calcular(dto);
    }
}
