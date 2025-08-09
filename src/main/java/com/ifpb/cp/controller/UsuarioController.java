package com.ifpb.cp.controller;

import com.ifpb.cp.model.Usuario;
import com.ifpb.cp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.cadastrar(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> editar(@PathVariable Long id,  @Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.editar(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

