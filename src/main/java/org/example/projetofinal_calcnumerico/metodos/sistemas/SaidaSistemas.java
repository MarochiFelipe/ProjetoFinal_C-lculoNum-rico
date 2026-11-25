package org.example.projetofinal_calcnumerico.metodos.sistemas;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;

import java.math.BigDecimal;
import java.math.MathContext;

final class SaidaSistemas {

    private SaidaSistemas() {}

    static ResultadoCalculo sucesso(double[][] a, double[] b, double[] solucao, StringBuilder passos) {
        int n = solucao.length;
        StringBuilder resumo = new StringBuilder("SOLUÇÃO\n");

        for (int coluna = 0; coluna < n; coluna++) {
            resumo.append("x").append(coluna + 1).append(" = ").append(numero(solucao[coluna])).append('\n');
        }

        passos.append("\nVERIFICAÇÃO NO SISTEMA ORIGINAL\n");
        for (int linha = 0; linha < n; linha++) {
            passos.append("Equação ").append(linha + 1).append(":\n");
            double calculado = 0.0;

            for (int coluna = 0; coluna < n; coluna++) {
                if (coluna > 0) {
                    passos.append(" + ");
                }
                passos.append('(').append(numero(a[linha][coluna])).append(") × (").append(numero(solucao[coluna])).append(')');
                calculado += a[linha][coluna] * solucao[coluna];
            }

            passos.append(" = ").append(numero(calculado))
                    .append(" | b = ").append(numero(b[linha]))
                    .append(" | diferença absoluta = ")
                    .append(numero(Math.abs(calculado - b[linha]))).append('\n');
        }

        return ResultadoCalculo.sucesso(resumo.toString().stripTrailing(), passos.toString());
    }

    static void registrarMatriz(StringBuilder passos, double[][] matriz) {
        int n = matriz.length;

        for (double[] linha : matriz) {
            for (int coluna = 0; coluna < n; coluna++) {
                passos.append(String.format(" %16s", numero(linha[coluna])));
            }
            if (linha.length > n) {
                passos.append(" | ").append(numero(linha[n]));
            }
            passos.append('\n');
        }
    }

    static ResultadoCalculo erro(String mensagem, StringBuilder passos) {
        return ResultadoCalculo.erro(mensagem + "\n\n" + passos);
    }

    static String numero(double valor) {
        if (!Double.isFinite(valor)) {
            return Double.toString(valor);
        }

        BigDecimal decimal = BigDecimal.valueOf(valor).round(new MathContext(10)).stripTrailingZeros();
        double modulo = Math.abs(valor);
        return modulo >= 1_000_000 || (modulo > 0 && modulo < 0.000001)
                ? decimal.toString()
                : decimal.toPlainString();
    }
}