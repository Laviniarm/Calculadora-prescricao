package com.ifpb.cp.service.utils;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utilitário para verificar a data mais recente de interrupção
 * do prazo prescricional, dentre várias causas previstas.
 */
public final class VerificarInterrupcao {

    private VerificarInterrupcao() {
        // evita instanciação
    }

    /**
     * Retorna, se houver, a data mais recente dentre as fornecidas.
     *
     * @param dataRecebimentoDaDenuncia         data de recebimento da denúncia
     * @param dataPronuncia                     data de pronúncia
     * @param dataConfirmatoriaDaPronuncia      data confirmatória da pronúncia
     * @param dataPublicacaoDaSentencaOuAcordao data de publicação da sentença ou acórdão
     * @param dataInicioDoCumprimentoDaPena     data de início do cumprimento da pena
     * @param dataContinuacaoDoCumprimentoDaPena data de continuação do cumprimento da pena
     * @param dataReincidencia                  data de reincidência
     * @return Optional contendo a data mais recente, ou vazio se todas forem nulas
     */
    public static Optional<LocalDate> verificarInterrupcao(
            LocalDate dataRecebimentoDaDenuncia,
            LocalDate dataPronuncia,
            LocalDate dataConfirmatoriaDaPronuncia,
            LocalDate dataPublicacaoDaSentencaOuAcordao,
            LocalDate dataInicioDoCumprimentoDaPena,
            LocalDate dataContinuacaoDoCumprimentoDaPena,
            LocalDate dataReincidencia) {

        return Stream.of(
                        dataRecebimentoDaDenuncia,
                        dataPronuncia,
                        dataConfirmatoriaDaPronuncia,
                        dataPublicacaoDaSentencaOuAcordao,
                        dataInicioDoCumprimentoDaPena,
                        dataContinuacaoDoCumprimentoDaPena,
                        dataReincidencia
                )
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo);
    }
}

