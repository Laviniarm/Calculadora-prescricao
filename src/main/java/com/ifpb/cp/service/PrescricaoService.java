package com.ifpb.cp.service;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.model.Prescricao;
import com.ifpb.cp.model.Usuario;
import com.ifpb.cp.repository.PrescricaoRepository;
import com.ifpb.cp.repository.UsuarioRepository;
import com.ifpb.cp.service.calculo.PrescricaoCalculator;
import com.ifpb.cp.service.calculo.impl.AbstrataPrescricaoCalculator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrescricaoService {

    @Autowired
    private AbstrataPrescricaoCalculator abstrataCalculator;

    @Autowired
    private PrescricaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final String EMAIL_PADRAO = "usuario@ficticio.com";

    // Se houver outras modalidades, injete-as aqui…

    public PrescricaoResponseDTO calcularPrescricao(PrescricaoRequestDTO dto) {
        PrescricaoCalculator calculator;
        if (dto.getTipoPrescricao() == null) {
            throw new IllegalArgumentException("O tipo de prescriçao não pode ser nulo");
        }
        switch (dto.getTipoPrescricao()) {
            case ABSTRATA:
                calculator = abstrataCalculator;
                break;

            default:
                throw new IllegalArgumentException(
                        "Tipo de prescrição inválido: " + dto.getTipoPrescricao()
                );
        }

        return calculator.calcular(dto);
    }

    public Prescricao salvarPrescricao(@Valid PrescricaoRequestDTO dto) {
        // 1) Executa o cálculo, que retorna apenas os valores de saída
        PrescricaoResponseDTO resultado = calcularPrescricao(dto);

        // 2) Busca o usuário fictício (tempo sendo)
        Usuario usuario = usuarioRepository.findByEmail(EMAIL_PADRAO)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // 3) Mapeia DTO de entrada, DTO de saída e usuário para a entidade
        Prescricao entidade = new Prescricao();
        entidade.setNomeAcusado(dto.getNomeAcusado());
        entidade.setNumeroProcesso(dto.getNumeroProcesso());
        entidade.setDataNascimento(dto.getDataNascimento());
        entidade.setTipoPrescricao(dto.getTipoPrescricao());
        entidade.setPenaAnos(dto.getPenaAnos());
        entidade.setPenaMeses(dto.getPenaMeses());
        entidade.setPenaDias(dto.getPenaDias());
        entidade.setDataFato(dto.getDataFato());
        entidade.setDataRecebimentoDaDenuncia(dto.getDataRecebimentoDaDenuncia());
        entidade.setCrimeTentado(dto.isCrimeTentado());
        entidade.setCausasAumento(dto.isCausasAumento());
        entidade.setCausasReducao(dto.isCausasReducao());
        entidade.setProcessoSuspenso(dto.isProcessoSuspenso());
        entidade.setTribunalJuri(dto.isTribunalJuri());

        entidade.setPena(resultado.getPena());
        entidade.setPrazoPrescricional(resultado.getPrazoPrescricional());
        entidade.setDataProvavel(resultado.getDataProvavel());
        entidade.setFaixaEtaria(resultado.getFaixaEtaria());

        entidade.setObservacao(dto.getObservacao());
        entidade.setElaboradoPor(dto.getElaboradoPor());
        entidade.setUsuario(usuario);

        repository.save(entidade);

        return entidade;
    }
}
