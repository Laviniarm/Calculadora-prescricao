package com.ifpb.cp.service;

import com.ifpb.cp.dto.UsuarioCreateRequest;
import com.ifpb.cp.dto.UsuarioUpdateRequest;
import com.ifpb.cp.model.Usuario;
import com.ifpb.cp.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public Usuario cadastrar(UsuarioCreateRequest req) {
        String emailNorm = req.email().trim().toLowerCase();
        if (repo.findByEmail(emailNorm).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }
        Usuario u = new Usuario();
        u.setNome(req.nome().trim());
        u.setEmail(emailNorm);
        u.setSenha(encoder.encode(req.senha())); // BCrypt
        return repo.save(u);
    }

    @Transactional
    public Usuario editar(Long id, UsuarioUpdateRequest req) {
        Usuario u = buscarPorId(id);
        if (req.nome() != null && !req.nome().isBlank()) {
            u.setNome(req.nome().trim());
        }
        return repo.save(u);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repo.existsById(id)) throw new RuntimeException("Usuário não encontrado");
        repo.deleteById(id);
    }
}
