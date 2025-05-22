package com.ifpb.cp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PrescricaoResponseDTO {
    private String pena;
    private String faixaEtaria;
    private String prazoPrescricional;
    private String dataProvavel;

}
