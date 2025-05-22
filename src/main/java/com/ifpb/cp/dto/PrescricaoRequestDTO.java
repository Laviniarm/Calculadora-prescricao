package com.ifpb.cp.dto;

import com.ifpb.cp.enums.TipoPrescricao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class PrescricaoRequestDTO {
    @NotBlank private String nomeAcusado;
    @NotBlank private String numeroProcesso;
    @NotNull private LocalDate dataNascimento;
    @NotBlank
    private TipoPrescricao tipoPrescricao;

    @Min(0) private int penaAnos;
    @Min(0) private int penaMeses;
    @Min(0) private int penaDias;

    @NotNull private LocalDate dataFato;
    @NotNull
    private LocalDate dataRecebimentoDaDenuncia;

    private boolean crimeTentado;
    private boolean causasAumento;
    private boolean causasReducao;
    private boolean processoSuspenso;

    private boolean tribunalJuri;
    private LocalDate dataSentencaPronuncia;
    private LocalDate dataAcordaoPronuncia;
    private LocalDate dataSentencaCondenatoria;
    private LocalDate dataAcordaoCondenatorio;

    private String observacao;
    private String elaboradoPor;

    private List<SuspensaoDTO> suspensoes;
}
