package com.ifpb.cp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "O nome é obrigatório")
        private String nome;

        @Email(message = "E-mail inválido")
        @NotBlank(message = "O e-mail é obrigatório")
        @Column(unique = true, nullable = false)
        private String email;

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        @Column(nullable = false)
        private String senha;

}
