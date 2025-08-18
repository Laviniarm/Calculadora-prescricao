package com.ifpb.cp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequest(
        @NotBlank @Size(min = 2, max = 100) String nome,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6, max = 128) String senha
) {}

