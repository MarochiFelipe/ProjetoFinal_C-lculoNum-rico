package org.example.projetofinal_calcnumerico.metodos.raizes;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

final class SaidaRaizes {

    private static final String FORMATO_COLUNA = " %16s";

    private final String[] cabecalhos;
    private final StringBuilder tabela = new StringBuilder();
    private final DecimalFormat decimal;
    private final DecimalFormat cientifico;
    private final CasasDecimais casasDecimais;
    private final DecimalFormat decimalTabela;
    private final DecimalFormat cientificoTabela;

    SaidaRaizes(CasasDecimais casasDecimais, String... cabecalhos) {
        this.casasDecimais = casasDecimais;
        this.cabecalhos = cabecalhos;
        DecimalFormatSymbols simbolos = DecimalFormatSymbols.getInstance(Locale.ROOT);
        decimal = new DecimalFormat("0.########", simbolos);
        cientifico = new DecimalFormat("0.#####E0", simbolos);

        if (casasDecimais == CasasDecimais.AUTOMATICO) {
            decimalTabela = decimal;
            cientificoTabela = cientifico;
        } else {
            String padrao = "0." + "0".repeat(casasDecimais.quantidade());
            decimalTabela = new DecimalFormat(padrao, simbolos);
            cientificoTabela = new DecimalFormat(padrao + "E0", simbolos);
        }
    }

    void registrar(int iteracao, double... valores) {
        if (tabela.isEmpty()) {
            tabela.append("ITERAÇÕES\n");
            tabela.append(String.format("%-7s", "Iter."));
            for (String cabecalho : cabecalhos) {
                tabela.append(String.format(FORMATO_COLUNA, cabecalho));
            }
            tabela.append('\n');
        }

        tabela.append(String.format("%-7d", iteracao));
        for (double valor : valores) {
            tabela.append(String.format(FORMATO_COLUNA, numeroTabela(valor)));
        }
        tabela.append('\n');
    }

    ResultadoCalculo sucesso(double raiz, int iteracoes, String motivo, String verificacao) {
        String raizExibida = casasDecimais == CasasDecimais.AUTOMATICO ? Double.toString(raiz) : numeroTabela(raiz);
        String resumo = "Raiz aproximada = " + raizExibida + "\nIterações = " + iteracoes;
        return ResultadoCalculo.sucesso(resumo, detalhes(motivo, verificacao));
    }

    ResultadoCalculo erro(String motivo, String verificacao) {
        return ResultadoCalculo.erro("Cálculo interrompido.\n\n" + detalhes(motivo, verificacao));
    }

    String raizExata(double x) {
        return "f(" + numero(x) + ") = 0";
    }

    String verificarIntervalo(double a, double b, double tolerancia) {
        return "b - a = " + numero(b) + " - (" + numero(a) + ") = " + numero(b - a) + "\n" + numero(b - a) + " <= " + numero(tolerancia);
    }

    String verificarResiduo(double x, double fx, double tolerancia) {
        return "|f(" + numero(x) + ")| = |" + numero(fx) + "| = " + numero(Math.abs(fx)) + "\n" + numero(Math.abs(fx)) + " <= " + numero(tolerancia);
    }

    String verificarVariacao(double anterior, double atual, double tolerancia) {
        double diferenca = Math.abs(atual - anterior);
        return "|x atual - x anterior| = |" + numero(atual) + " - (" + numero(anterior) + ")| = " + numero(diferenca) + "\n" + numero(diferenca) + " <= " + numero(tolerancia);
    }

    String numero(double valor) {
        return (usaCientifico(valor) ? cientifico : decimal).format(valor);
    }

    private String numeroTabela(double valor) {
        return (usaCientifico(valor) ? cientificoTabela : decimalTabela).format(valor);
    }

    private boolean usaCientifico(double valor) {
        double modulo = Math.abs(valor);
        return modulo >= 1_000_000 || (modulo > 0 && modulo < 0.000001);
    }

    private String detalhes(String motivo, String verificacao) {
        String historico = tabela.isEmpty() ? "" : tabela + "\n";
        return historico + "MOTIVO DA PARADA\n" + motivo + "\n\nVERIFICAÇÃO\n" + verificacao;
    }
}
