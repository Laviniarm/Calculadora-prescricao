package com.ifpb.cp.teste;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.dto.SuspensaoDTO;
import com.ifpb.cp.enums.TipoPrescricao;
import com.ifpb.cp.enums.TipoSuspensao;
import com.ifpb.cp.service.calculo.impl.AbstrataPrescricaoCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AbstrataPrescricaoCalculatorTest {

    private final AbstrataPrescricaoCalculator calculator = new AbstrataPrescricaoCalculator();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Test
    public void calculaPrescricaoSemInterrupcaoOuSuspensao() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setNomeAcusado("João da Silva");
        dto.setNumeroProcesso("0001234-56.2025.8.15.0001");
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setDataNascimento(LocalDate.of(1980, 3, 20));
        dto.setPenaAnos(2);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);
        dto.setDataFato(LocalDate.of(2000, 1, 1));

        // sem causas interruptivas
        dto.setDataRecebimentoDaDenuncia(null);
        dto.setDataPronuncia(null);
        dto.setDataConfirmatoriaDaPronuncia(null);
        dto.setDataPublicacaoDaSentencaOuAcordao(null);
        dto.setDataInicioDoCumprimentoDaPena(null);
        dto.setDataContinuacaoDoCumprimentoDaPena(null);
        dto.setDataReincidencia(null);

        // sem suspensões
        dto.setSuspensoes(List.of());

        PrescricaoResponseDTO resp = calculator.calcular(dto);

        assertEquals("2 anos, 0 meses e 0 dias", resp.getPena());
        assertEquals("Entre 21 e 70 anos", resp.getFaixaEtaria());
        assertEquals("4 anos", resp.getPrazoPrescricional());
        assertEquals("31/12/2003", resp.getDataProvavel());
    }

    @Test
    public void calculaPrescricaoComSeisMesesDeSuspensao() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setNomeAcusado("Maria Souza");
        dto.setNumeroProcesso("0005678-90.2025.8.15.0001");
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setDataNascimento(LocalDate.of(1970, 12, 5));
        dto.setPenaAnos(2);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);
        dto.setDataFato(LocalDate.of(2000, 1, 1));

        // sem causas interruptivas
        dto.setDataRecebimentoDaDenuncia(null);
        dto.setDataPronuncia(null);
        dto.setDataConfirmatoriaDaPronuncia(null);
        dto.setDataPublicacaoDaSentencaOuAcordao(null);
        dto.setDataInicioDoCumprimentoDaPena(null);
        dto.setDataContinuacaoDoCumprimentoDaPena(null);
        dto.setDataReincidencia(null);

        // suspensao de 05/06/2002 a 05/12/2002 (~183 dias)
        SuspensaoDTO susp = new SuspensaoDTO();
        susp.setTipo(TipoSuspensao.PREJUDICIAL);
        susp.setInicio(LocalDate.of(2002, 6, 5));
        susp.setFim(LocalDate.of(2002, 12, 5));
        dto.setSuspensoes(List.of(susp));

        PrescricaoResponseDTO resp = calculator.calcular(dto);

        assertEquals("2 anos, 0 meses e 0 dias", resp.getPena());
        assertEquals("Entre 21 e 70 anos", resp.getFaixaEtaria());
        assertEquals("4 anos", resp.getPrazoPrescricional());
        assertEquals("01/07/2004", resp.getDataProvavel());
    }

    //não ta passando no teste
    @Test
    void calculaPrescricaoMenorDe21Anos() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setDataNascimento(LocalDate.of(2006, 6, 15));
        dto.setPenaAnos(4);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);
        dto.setDataFato(LocalDate.of(2018, 6, 16));
        dto.setSuspensoes(List.of());

        PrescricaoResponseDTO resp = calculator.calcular(dto);

        assertEquals("4 anos, 0 meses e 0 dias", resp.getPena());
        assertEquals("Menor de 21 anos", resp.getFaixaEtaria());
        assertEquals("6 anos", resp.getPrazoPrescricional());
        assertEquals("16/06/2024", resp.getDataProvavel());
    }

    //não ta passando no teste
    @Test
    void calculaPrescricaoMaiorDe70Anos() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setDataNascimento(LocalDate.of(1940, 1, 1));
        dto.setPenaAnos(5);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);
        dto.setDataFato(LocalDate.of(2020, 1, 1));
        dto.setSuspensoes(List.of());

        PrescricaoResponseDTO resp = calculator.calcular(dto);

        assertEquals("5 anos, 0 meses e 0 dias", resp.getPena());
        assertEquals("Maior de 70 anos", resp.getFaixaEtaria());
        assertEquals("10 anos", resp.getPrazoPrescricional());
        // 05/01/2020 + 10 anos = 05/01/2030
        assertEquals("05/01/2030", resp.getDataProvavel());
    }

    @Test
    void deveLancarErroQuandoDataFatoNull() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setDataFato(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calcular(dto)
        );
        assertEquals("Data inicial não pode ser null", ex.getMessage());
    }

    //não ta passando no teste
    @Test
    void deveRecomecarContagemComRecebimentoDenuncia() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setDataNascimento(LocalDate.of(1980,1,1));
        dto.setPenaAnos(2);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);

        dto.setDataFato(LocalDate.of(2020,1,1));
        // interrupção após o fato
        dto.setDataRecebimentoDaDenuncia(LocalDate.of(2022,1,1));

        // sem suspensões
        dto.setSuspensoes(List.of());

        PrescricaoResponseDTO resp = calculator.calcular(dto);

        // 2 anos de pena → prazo de 4 anos a partir de 2022-01-01
        String esperado = LocalDate.of(2022,1,1)
                .plusYears(4)
                .format(fmt);
        assertEquals(esperado, resp.getDataProvavel());
    }

    /**
     * Teste 3.2 — Não deve recomeçar se interrupção for antes da data do fato
     */
    //não ta passando no teste
    @Test
    void naoDeveRecomecarComRecebimentoAntesDoFato() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setDataNascimento(LocalDate.of(1980,1,1));
        dto.setPenaAnos(2);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);

        dto.setDataFato(LocalDate.of(2020,1,1));
        // interrupção antes do fato
        dto.setDataRecebimentoDaDenuncia(LocalDate.of(2019,1,1));

        dto.setSuspensoes(List.of());

        PrescricaoResponseDTO resp = calculator.calcular(dto);

        // prazo de 4 anos a partir de 2020-01-01
        String esperado = LocalDate.of(2020,1,1)
                .plusYears(4)
                .format(fmt);
        assertEquals(esperado, resp.getDataProvavel());
    }

    /**
     * Teste 4.1 — Deve somar dias de suspensão ao prazo
     */
    //não ta passando no teste

    @Test
    void deveSomarDiasSuspensaoAoPrazo() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setDataNascimento(LocalDate.of(1980,1,1));
        dto.setPenaAnos(1);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);

        LocalDate fato = LocalDate.of(2020,1,1);
        dto.setDataFato(fato);

        // sem interrupções
        dto.setDataRecebimentoDaDenuncia(null);

        // suspensão de 181 dias (01/01 – 01/07)
        SuspensaoDTO susp = new SuspensaoDTO();
        susp.setInicio(LocalDate.of(2021,1,1));
        susp.setFim(LocalDate.of(2021,7,1));
        dto.setSuspensoes(List.of(susp));

        PrescricaoResponseDTO resp = calculator.calcular(dto);

        // pena 1 ano → prazo 3 anos (1095 dias) + 181 dias suspensão
        LocalDate esperado = fato
                .plusDays(1095L + 181L);
        assertEquals(esperado.format(fmt), resp.getDataProvavel());
    }

    /**
     * Teste 4.2 — Deve limitar dias de suspensão ao prazo base
     */
    //não ta passando no teste

    @Test
    void deveLimitarDiasSuspensaoAoPrazoBase() {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setTipoPrescricao(TipoPrescricao.ABSTRATA);
        dto.setDataNascimento(LocalDate.of(1980,1,1));
        dto.setPenaAnos(1);
        dto.setPenaMeses(0);
        dto.setPenaDias(0);

        LocalDate fato = LocalDate.of(2020,1,1);
        dto.setDataFato(fato);

        // sem interrupções
        dto.setDataRecebimentoDaDenuncia(null);

        // suspensão de 2000 dias (maior que o prazo base de 1095 dias)
        SuspensaoDTO susp = new SuspensaoDTO();
        susp.setInicio(LocalDate.of(2021,1,1));
        susp.setFim(LocalDate.of(2026,6,1));
        dto.setSuspensoes(List.of(susp));

        PrescricaoResponseDTO resp = calculator.calcular(dto);

        // prazo base 1095 dias, logo data provável = fato + 1095 dias
        LocalDate esperado = fato.plusDays(1095L);
        assertEquals(esperado.format(fmt), resp.getDataProvavel());
    }

}
