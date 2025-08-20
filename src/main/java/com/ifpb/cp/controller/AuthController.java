package com.ifpb.cp.controller;
import com.ifpb.cp.model.Usuario;
import com.ifpb.cp.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;






@RestController
@RequestMapping("/api")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // DTO simples (sem senha)
    public record UsuarioDTO(Long id, String nome, String email) {
        public static UsuarioDTO from(Usuario u) {
            return new UsuarioDTO(u.getId(), u.getNome(), u.getEmail());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> me(Authentication authentication) {
        // authentication.getName() = username (no seu caso, email)
        String email = authentication.getName();
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return ResponseEntity.ok(UsuarioDTO.from(usuario));
    }
}