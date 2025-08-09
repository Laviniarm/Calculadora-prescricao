package com.ifpb.cp.teste;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ifpb.cp.model.Usuario;
import com.ifpb.cp.repository.UsuarioRepository;
import com.ifpb.cp.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioService usuarioService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        usuarioService = new UsuarioService(usuarioRepository);
    }

    @Test
    void testCadastrarUsuarioSucesso() {
        Usuario usuario = new Usuario(null, "Maria", "maria@example.com", "senha123");

        when(usuarioRepository.existsByEmail("maria@example.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        Usuario usuarioSalvo = usuarioService.cadastrar(usuario);

        assertNotNull(usuarioSalvo.getId());
        assertEquals("Maria", usuarioSalvo.getNome());
        assertNotEquals("senha123", usuarioSalvo.getSenha()); // senha deve estar criptografada
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testCadastrarUsuarioEmailDuplicado() {
        Usuario usuario = new Usuario(null, "João", "joao@example.com", "senha123");

        when(usuarioRepository.existsByEmail("joao@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.cadastrar(usuario);
        });

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testEditarUsuarioSucesso() {
        Usuario usuarioExistente = new Usuario(1L, "Maria", "maria@example.com", "$2a$10$encrypted");
        Usuario usuarioAtualizado = new Usuario(null, "Maria Silva", "maria.silva@example.com", "novasenha");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioEditado = usuarioService.editar(1L, usuarioAtualizado);

        assertEquals("Maria Silva", usuarioEditado.getNome());
        assertEquals("maria.silva@example.com", usuarioEditado.getEmail());
        assertNotEquals("novasenha", usuarioEditado.getSenha());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void testExcluirUsuarioSucesso() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        assertDoesNotThrow(() -> usuarioService.excluir(1L));
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void testExcluirUsuarioNaoExistente() {
        when(usuarioRepository.existsById(2L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.excluir(2L);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository, never()).deleteById(2L);
    }
}
