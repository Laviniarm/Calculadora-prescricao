package com.ifpb.cp.service.calculo;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;

public interface PrescricaoCalculator {
    PrescricaoResponseDTO calcular(PrescricaoRequestDTO dto);
}
