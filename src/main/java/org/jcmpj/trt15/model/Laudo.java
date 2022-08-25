package org.jcmpj.trt15.model;

import lombok.*;

/**
 *
 * @author jcmpj
 */
@Getter
@Setter
public class Laudo {

    private int id;
    private String numProcesso;
    private String nomeReclamante;
    private String nomeReclamada;
    private String dataVistoria;
    private String horaVistoria;
    private String localVistoria;
    private String enderecoVistoriado;
    private String dataInicioPeriodoReclamado;
    private String dataFimPeriodoReclamado;
    private String funcaoExercida;
    private String cidadeEmissao;
    private String dataEmissao;
    private String acompanhantesReclamante;
    private String acompanhantesReclamada;
    private String dataCriacao;

    public Laudo() {
    }

}
