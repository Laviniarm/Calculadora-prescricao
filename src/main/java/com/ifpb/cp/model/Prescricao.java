package com.ifpb.cp.model;

import com.ifpb.cp.enums.TipoPrescricao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescricao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeAcusado;
    private String numeroProcesso;
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private TipoPrescricao tipoPrescricao;

    private int penaAnos;
    private int penaMeses;
    private int penaDias;

    private LocalDate dataFato;
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

    private String resultado;
    private LocalDate prazoFinal;
    private LocalDate dataConsulta = LocalDate.now();

    private String observacao;
    private String elaboradoPor;

    //private List<SuspensaoDTO> suspensoes;
}
