package com.ifpb.cp.teste;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.dto.PrescricaoSaveDTO;
import com.ifpb.cp.enums.TipoPrescricao;
import com.ifpb.cp.model.Prescricao;
import com.ifpb.cp.model.Usuario;
import com.ifpb.cp.repository.PrescricaoRepository;
import com.ifpb.cp.repository.UsuarioRepository;
import com.ifpb.cp.service.PrescricaoService;
import com.ifpb.cp.service.calculo.*;
import com.ifpb.cp.service.calculo.impl.AbstrataPrescricaoCalculator;
import com.ifpb.cp.service.calculo.impl.ConcretaRetroativaPrescricaoCalculator;
import com.ifpb.cp.service.calculo.impl.ExecutoriaPrescricaoCalculator;
import com.ifpb.cp.service.calculo.impl.IntercorrentePrescricaoCalculator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PrescricaoServiceTest {

    @InjectMocks
    private PrescricaoService service;

    @Mock
    private AbstrataPrescricaoCalculator abstrataCalculator;

    @Mock
    private ExecutoriaPrescricaoCalculator concretoCalculator;

    @Mock
    private ConcretaRetroativaPrescricaoCalculator retroativaCalculator;

    @Mock
    private IntercorrentePrescricaoCalculator intercorrenteCalculator;

    @Mock
    private PrescricaoRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private PrescricaoRequestDTO requestDTO;
    private PrescricaoSaveDTO saveDTO;
    private Usuario usuario;
    private PrescricaoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        responseDTO = new PrescricaoResponseDTO();
        responseDTO.setPena("4 anos");
        responseDTO.setPrazoPrescricional("12 anos");
        responseDTO.setDataProvavel(String.valueOf(LocalDate.of(2035, 1, 1)));
        responseDTO.setFaixaEtaria("Adulto");

        saveDTO = new PrescricaoSaveDTO();
        saveDTO.setTipoPrescricao(TipoPrescricao.CONCRETO);
        saveDTO.setUsuarioId(1L);
        saveDTO.setNomeAcusado("Fulano");
        saveDTO.setNumeroProcesso("123");
        saveDTO.setDataNascimento(LocalDate.of(1990, 1, 1));
        saveDTO.setDataFato(LocalDate.of(2020, 1, 1));
        saveDTO.setDataRecebimentoDaDenuncia(LocalDate.of(2021, 1, 1));
        saveDTO.setElaboradoPor("Sistema");

        requestDTO = new PrescricaoRequestDTO();
        requestDTO.setTipoPrescricao(TipoPrescricao.ABSTRATA);
    }

    // --- calcularPrescricao ---

    @Test
    void calcularPrescricao_abstrata_sucesso() {
        when(abstrataCalculator.calcular(requestDTO)).thenReturn(responseDTO);

        PrescricaoResponseDTO result = service.calcularPrescricao(requestDTO);

        assertEquals("4 anos", result.getPena());
        verify(abstrataCalculator).calcular(requestDTO);
    }

    @Test
    void calcularPrescricao_concreto_sucesso() {
        requestDTO.setTipoPrescricao(TipoPrescricao.CONCRETO);
        when(concretoCalculator.calcular(requestDTO)).thenReturn(responseDTO);

        PrescricaoResponseDTO result = service.calcularPrescricao(requestDTO);

        assertEquals("4 anos", result.getPena());
        verify(concretoCalculator).calcular(requestDTO);
    }

    @Test
    void calcularPrescricao_retroativa_sucesso() {
        requestDTO.setTipoPrescricao(TipoPrescricao.RETROATIVA);
        when(retroativaCalculator.calcular(requestDTO)).thenReturn(responseDTO);

        PrescricaoResponseDTO result = service.calcularPrescricao(requestDTO);

        assertEquals("4 anos", result.getPena());
        verify(retroativaCalculator).calcular(requestDTO);
    }

    @Test
    void calcularPrescricao_intercorrente_sucesso() {
        requestDTO.setTipoPrescricao(TipoPrescricao.INTERCORRENTE);
        when(intercorrenteCalculator.calcular(requestDTO)).thenReturn(responseDTO);

        PrescricaoResponseDTO result = service.calcularPrescricao(requestDTO);

        assertEquals("4 anos", result.getPena());
        verify(intercorrenteCalculator).calcular(requestDTO);
    }

    @Test
    void calcularPrescricao_tipoNulo_lancaExcecao() {
        requestDTO.setTipoPrescricao(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.calcularPrescricao(requestDTO));

        assertEquals("O tipo de prescriçao não pode ser nulo", ex.getMessage());
    }

    // --- salvarPrescricao ---

    @Test
    void salvarPrescricao_sucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(concretoCalculator.calcular(saveDTO)).thenReturn(responseDTO);

        Prescricao resultado = service.salvarPrescricao(saveDTO);

        assertEquals("Fulano", resultado.getNomeAcusado());
        assertEquals("4 anos", resultado.getPena());
        assertEquals("Sistema", resultado.getElaboradoPor());
        assertEquals(usuario, resultado.getUsuario());
        verify(repository).save(any(Prescricao.class));
    }

    @Test
    void salvarPrescricao_usuarioNaoExiste_lancaExcecao() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        when(concretoCalculator.calcular(saveDTO)).thenReturn(responseDTO);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.salvarPrescricao(saveDTO));

        assertEquals("Usuário não encontrado.", ex.getMessage());
    }

    // --- listarPrescricoes ---

    @Test
    void listarPrescricoes_usuarioExiste_retornaLista() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        List<Prescricao> lista = List.of(new Prescricao());
        when(repository.listarPrescricaoPorUsuario(1L)).thenReturn(lista);

        List<Prescricao> resultado = service.listarPrescricoes(1L);

        assertEquals(1, resultado.size());
        verify(repository).listarPrescricaoPorUsuario(1L);
    }

    @Test
    void listarPrescricoes_usuarioNaoExiste_lancaExcecao() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.listarPrescricoes(1L));

        assertEquals("Usuário não encontrado.", ex.getMessage());
    }
}
