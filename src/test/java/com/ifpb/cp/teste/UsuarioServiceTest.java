package com.ifpb.cp.teste;

import com.ifpb.cp.dto.UsuarioCreateRequest;
import com.ifpb.cp.dto.UsuarioUpdateRequest;
import com.ifpb.cp.model.Usuario;
import com.ifpb.cp.repository.UsuarioRepository;
import com.ifpb.cp.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    @Test
    void testCadastrarUsuarioSucesso() {
        // dado
        var req = new UsuarioCreateRequest("Maria", "Maria@Example.COM", "senha123");
        when(usuarioRepository.findByEmail("maria@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123")).thenReturn("$bcrypt$senha123");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        // quando
        Usuario usuarioSalvo = usuarioService.cadastrar(req);

        // então
        assertNotNull(usuarioSalvo.getId());
        assertEquals("Maria", usuarioSalvo.getNome());
        assertEquals("maria@example.com", usuarioSalvo.getEmail()); // normalizado
        assertEquals("$bcrypt$senha123", usuarioSalvo.getSenha());  // criptografada
        verify(usuarioRepository).findByEmail("maria@example.com");
        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testCadastrarUsuarioEmailDuplicado() {
        // dado
        var req = new UsuarioCreateRequest("João", "joao@example.com", "senha123");
        var existente = new Usuario();
        existente.setId(99L);
        existente.setEmail("joao@example.com");
        when(usuarioRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(existente));

        // quando/então
        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.cadastrar(req));
        assertEquals("Email já cadastrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testEditarUsuarioSucesso() {
        // dado
        var existente = new Usuario();
        existente.setId(1L);
        existente.setNome("Maria");
        existente.setEmail("maria@example.com");
        existente.setSenha("$bcrypt$old");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var reqUpdate = new UsuarioUpdateRequest("Maria Silva");

        // quando
        Usuario editado = usuarioService.editar(1L, reqUpdate);

        // então
        assertEquals(1L, editado.getId());
        assertEquals("Maria Silva", editado.getNome());
        assertEquals("maria@example.com", editado.getEmail()); // email não é alterado no update
        assertEquals("$bcrypt$old", editado.getSenha());       // senha não é alterada aqui
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testBuscarPorIdNaoEncontrado() {
        when(usuarioRepository.findById(42L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.buscarPorId(42L));
        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void testExcluirUsuarioSucesso() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        assertDoesNotThrow(() -> usuarioService.excluir(1L));
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void testExcluirUsuarioNaoExistente() {
        when(usuarioRepository.existsById(2L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.excluir(2L));
        assertEquals("Usuário não encontrado", ex.getMessage());
        verify(usuarioRepository, never()).deleteById(2L);
    }
}
