package org.example.projetofinal_calcnumerico.metodos.interpolacao;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.ConversorEntrada;

import java.util.Locale;

public class Newton {

    public static ResultadoCalculo calcular(double[] x, double[] y, double xAlvo) {
        String erro = ValidadorInterpolacao.validar(x, y);
        if (erro != null) {
            return ResultadoCalculo.erro(erro);
        }

        int n = x.length - 1;
        double[][] d = new double[n + 1][n + 1];

        // 1. Ordem 0: f[x_i] = y_i
        for (int i = 0; i <= n; i++) {
            d[i][0] = y[i];
        }

        // 2. Diferenças divididas de Ordem 1 até Ordem n
        for (int j = 1; j <= n; j++) {
            for (int i = 0; i <= n - j; i++) {
                d[i][j] = (d[i + 1][j - 1] - d[i][j - 1]) / (x[i + j] - x[i]);
            }
        }

        // 3. Montar a tabela de diferenças divididas para exibição
        StringBuilder detalhes = new StringBuilder();
        detalhes.append(ValidadorInterpolacao.avisoExtrapolacao(x, xAlvo));
        detalhes.append("Tabela de Diferenças Divididas:\n");

        // Cabeçalho
        detalhes.append(String.format(Locale.US, "%-12s", "x_i"));
        for (int j = 0; j <= n; j++) {
            detalhes.append(String.format(Locale.US, " | %-12s", "Ordem " + j));
        }
        detalhes.append("\n");

        // Linhas da tabela (formato triangular: j vai de 0 até n - i)
        for (int i = 0; i <= n; i++) {
            detalhes.append(String.format(Locale.US, "%-12s", ConversorEntrada.formatar(x[i])));
            for (int j = 0; j <= n - i; j++) {
                detalhes.append(String.format(Locale.US, " | %-12s", ConversorEntrada.formatar(d[i][j])));
            }
            detalhes.append("\n");
        }

        // 4. Avaliar P_n(xAlvo) usando a primeira linha da tabela (d[0][k])
        double resultado = d[0][0];
        double produto = 1.0;

        detalhes.append("\nCoeficientes utilizados (primeira linha da tabela):\n");
        detalhes.append("d_0 = ").append(ConversorEntrada.formatar(d[0][0])).append("\n");

        for (int k = 1; k <= n; k++) {
            produto *= (xAlvo - x[k - 1]);
            resultado += d[0][k] * produto;
            detalhes.append("d_").append(k).append(" = ")
                    .append(ConversorEntrada.formatar(d[0][k])).append("\n");
        }

        String resumo = String.format(
                "Newton (grau <= %d): P_%d(%s) = %s",
                n, n, ConversorEntrada.formatar(xAlvo), ConversorEntrada.formatar(resultado)
        );
        return ResultadoCalculo.sucesso(resumo, detalhes.toString());
    }
}