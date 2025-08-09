package com.ifpb.cp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescricaoResponseDTO {
    private String pena;
    private String faixaEtaria;
    private String prazoPrescricional;
    private String dataProvavel;

}
