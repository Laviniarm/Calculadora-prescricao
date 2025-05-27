package com.ifpb.cp.enums;

public enum TipoSuspensao {

    CONDICIONAL("Acusado beneficiado com a suspensão condicional do processo, nos termos do artigo 89, § 6º, da Lei nº 9.099/95."),
    NAOCOMPARECE("Acusado citado por edital que não comparece e não constitui advogado (vide artigo 366 do CPP e Súmula 415 do STJ)."),
    PREJUDICIAL("Existência de questão prejudicial, nos termos do artigo 92 e seguintes do Código de Processo Penal."),
    PAISESTRANGEIRO("Cumprimento de pena pelo acusado em país estrangeiro, salvo em caso de fato atípico (vide artigo 386, III, do CPP)."),
    SUSTACAO("Sustação de processo criminal contra parlamentar, nos termos do artigo 53, §§ 3º a 5º da CF."),
    ROGATORIA("Citação mediante carta rogatória de acusado em país estrangeiro, em lugar sabido, nos termos do artigo 368 do CPP.");

    private final String description;

    // Construtor compatível com os valores que você passou acima
    private TipoSuspensao(String description) {
        this.description = description;
    }

    // Getter para recuperar a descrição
    public String getDescription() {
        return description;
    }
}
