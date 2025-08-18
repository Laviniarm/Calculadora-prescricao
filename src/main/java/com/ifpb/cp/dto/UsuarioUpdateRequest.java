package com.ifpb.cp.dto;

import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
            @Size(min = 2, max = 100) String nome
            // acrescente campos editáveis conforme seu modelo (NÃO inclua senha aqui)
    ) {}
