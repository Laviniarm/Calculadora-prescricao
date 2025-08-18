package com.ifpb.cp.controller;

import com.ifpb.cp.dto.UsuarioCreateRequest;
import com.ifpb.cp.dto.UsuarioUpdateRequest;
import com.ifpb.cp.dto.UsuarioResponse;
import com.ifpb.cp.model.Usuario;
import com.ifpb.cp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        Usuario u = service.buscarPorId(id);
        return ResponseEntity.ok(toResponse(u));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioCreateRequest req) {
        Usuario salvo = service.cadastrar(req);
        return ResponseEntity.created(URI.create("/usuarios/" + salvo.getId())).body(toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> editar(@PathVariable Long id,
                                                  @Valid @RequestBody UsuarioUpdateRequest req) {
        Usuario editado = service.editar(id, req);
        return ResponseEntity.ok(toResponse(editado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail());
    }
}
