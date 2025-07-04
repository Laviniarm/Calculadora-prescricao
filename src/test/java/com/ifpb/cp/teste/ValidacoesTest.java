package com.ifpb.cp.teste;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.enums.TipoPrescricao;
import com.ifpb.cp.service.PrescricaoService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ValidacoesTest {
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private PrescricaoRequestDTO criarValido() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setNomeAcusado("João Silva");
        dto.setNumeroProcesso("1234567-89.2020.8.01.0001");
        dto.setDataNascimento(LocalDate.of(1980, 1, 1));
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setPenaAnos(1);
        dto.setPenaMeses(2);
        dto.setPenaDias(15);
        dto.setDataFato(LocalDate.of(2020, 5, 20));
        return dto;
    }

    @Test
    void deveValidarComSucessoDTOValido() {
        PrescricaoRequestDTO dto = criarValido();
        Set<ConstraintViolation<PrescricaoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Deveria passar sem violação");
    }

    @Test
    void deveValidarCamposObrigatorios() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO(); // vazio
        Set<ConstraintViolation<PrescricaoRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        List<String> mensagens = violations.stream().map(ConstraintViolation::getMessage).toList();

        assertTrue(mensagens.contains("tipoPrescricao não pode ser nulo"));
        assertTrue(mensagens.contains("must not be blank") || mensagens.contains("não deve estar em branco"));
    }

    @Test
    void naoDeveAceitarPenasNegativas() {
        PrescricaoRequestDTO dto = criarValido();
        dto.setPenaAnos(-1);
        dto.setPenaMeses(-2);
        dto.setPenaDias(-3);

        Set<ConstraintViolation<PrescricaoRequestDTO>> violations = validator.validate(dto);

        assertEquals(3, violations.size());

        List<String> camposInvalidos = violations.stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .toList();

        assertTrue(camposInvalidos.contains("penaAnos"));
        assertTrue(camposInvalidos.contains("penaMeses"));
        assertTrue(camposInvalidos.contains("penaDias"));

        for (ConstraintViolation<PrescricaoRequestDTO> v : violations) {
            assertEquals("deve ser maior que ou igual à 0", v.getMessage());
        }
    }

    @Test
    void devePermitirCamposOpcionaisNulos() {
        PrescricaoRequestDTO dto = criarValido();
        dto.setDataTransitoEmJulgado(null);
        dto.setDataRecebimentoDaDenuncia(null);
        dto.setSuspensoes(null); // campo opcional

        Set<ConstraintViolation<PrescricaoRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Campos opcionais nulos não devem causar erro");
    }

    @Test
    void deveLancarExcecaoParaTipoPrescricaoInvalido() {
        PrescricaoService service = new PrescricaoService();

        PrescricaoRequestDTO dto = criarValido();
        dto.setTipoPrescricao(null); // tipo inválido

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.calcularPrescricao(dto);
        });

        assertEquals("O tipo de prescriçao não pode ser nulo", exception.getMessage());
    }
}
