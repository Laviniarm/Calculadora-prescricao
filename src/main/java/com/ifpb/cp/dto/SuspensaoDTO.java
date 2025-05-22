package com.ifpb.cp.dto;

import com.ifpb.cp.enums.TipoSuspensao;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SuspensaoDTO {
    private TipoSuspensao tipo;
    private LocalDate inicio;
    private LocalDate fim;
}
