package org.example.projetofinal_calcnumerico.model;

public class ResultadoCalculo {
    private final boolean sucesso;
    private final String resultadoFinal;
    private final String detalhes;

    private ResultadoCalculo(boolean sucesso, String resultadoFinal, String detalhes) {
        this.sucesso = sucesso;
        this.resultadoFinal = resultadoFinal;
        this.detalhes = detalhes;
    }

    public static ResultadoCalculo sucesso(String resultadoFinal, String detalhes) {
        return new ResultadoCalculo(true, resultadoFinal, detalhes);
    }

    public static ResultadoCalculo erro(String mensagemErro) {
        return new ResultadoCalculo(false, mensagemErro, "");
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public String getResultadoFinal() {
        return resultadoFinal;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public String formatarParaTela() {
        if (!sucesso) {
            return "ERRO: " + resultadoFinal;
        }
        if (detalhes == null || detalhes.isBlank()) {
            return resultadoFinal;
        }
        return resultadoFinal + "\n\n--- Passos / Iterações ---\n" + detalhes;
    }
}