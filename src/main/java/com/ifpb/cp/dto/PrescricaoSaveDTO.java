package com.ifpb.cp.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PrescricaoSaveDTO extends PrescricaoRequestDTO {

    @NotNull(message = "O ID do usuário é obrigatório para salvar a prescrição")
    private Long usuarioId;

    public PrescricaoSaveDTO(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public PrescricaoSaveDTO(PrescricaoRequestDTO dto, Long usuarioId) {
        super(
                dto.getNomeAcusado(),
                dto.getNumeroProcesso(),
                dto.getDataNascimento(),
                dto.getTipoPrescricao(),
                dto.getPenaAnos(),
                dto.getPenaMeses(),
                dto.getPenaDias(),
                dto.getDataFato(),
                dto.getDataTransitoEmJulgado(),
                dto.getDataRecebimentoDaDenuncia(),
                dto.getDataPronuncia(),
                dto.getDataConfirmatoriaDaPronuncia(),
                dto.getDataPublicacaoDaSentencaOuAcordao(),
                dto.getDataInicioDoCumprimentoDaPena(),
                dto.getDataContinuacaoDoCumprimentoDaPena(),
                dto.getDataReincidencia(),
                dto.isCrimeTentado(),
                dto.isCausasAumento(),
                dto.isCausasReducao(),
                dto.isProcessoSuspenso(),
                dto.isTribunalJuri(),
                dto.getObservacao(),
                dto.getElaboradoPor(),
                dto.getSuspensoes()
        );
        this.usuarioId = usuarioId;
    }
}
