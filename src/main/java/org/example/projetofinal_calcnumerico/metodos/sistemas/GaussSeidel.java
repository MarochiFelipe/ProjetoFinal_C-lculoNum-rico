package org.example.projetofinal_calcnumerico.metodos.sistemas;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;

import java.util.Arrays;

import static org.example.projetofinal_calcnumerico.metodos.sistemas.SaidaSistemas.erro;
import static org.example.projetofinal_calcnumerico.metodos.sistemas.SaidaSistemas.numero;

public class GaussSeidel {

    public static ResultadoCalculo calcular(double[][] a, double[] b, double[] xInicial,
                                            double tolerancia, int maxIteracoes) {
        if (a == null || b == null || a.length == 0 || a.length != b.length) {
            return ResultadoCalculo.erro("Informe uma matriz A e um vetor b de dimensões compatíveis.");
        }
        if (xInicial == null || xInicial.length != a.length) {
            return ResultadoCalculo.erro("O vetor inicial x₀ deve ter " + a.length + " elementos.");
        }
        if (!Double.isFinite(tolerancia) || tolerancia <= 0 || maxIteracoes <= 0) {
            return ResultadoCalculo.erro("A tolerância e o máximo de iterações devem ser positivos.");
        }

        int n = a.length;
        for (int linha = 0; linha < n; linha++) {
            if (a[linha] == null || a[linha].length != n
                    || !Double.isFinite(b[linha]) || !Double.isFinite(xInicial[linha])) {
                return ResultadoCalculo.erro("Informe uma matriz quadrada e vetores com valores finitos.");
            }
            for (double coeficiente : a[linha]) {
                if (!Double.isFinite(coeficiente)) {
                    return ResultadoCalculo.erro("A matriz A deve conter apenas valores finitos.");
                }
            }
            if (a[linha][linha] == 0.0) {
                return ResultadoCalculo.erro("Diagonal zero na equação " + (linha + 1) + ". Gauss-Seidel exige coeficientes não nulos na diagonal principal.");
            }
        }

        double maiorBeta = sassenfeld(a);
        double[] x = xInicial.clone();
        StringBuilder passos = new StringBuilder("GAUSS-SEIDEL\n");

        passos.append("Vetor inicial = ").append(Arrays.toString(xInicial))
                .append("\nSassenfeld • beta máximo = ").append(numero(maiorBeta))
                .append(maiorBeta < 1.0 ? " < 1: convergência garantida."
                        : " >= 1: o critério não garante a convergência.")
                .append("\n\nITERAÇÕES\n");
        passos.append(String.format("%-7s", "Iter."));

        for (int coluna = 0; coluna < n; coluna++) {
            passos.append(String.format(" %16s", "x" + (coluna + 1)));
        }
        passos.append(String.format(" %16s%n", "Erro"));

        double maiorDiferenca = 0.0;
        double maiorModulo = 0.0;
        double erroParada = 0.0;

        for (int i = 0; i < maxIteracoes; i++) {
            int iteracao = i + 1;
            maiorDiferenca = 0.0;
            maiorModulo = 0.0;

            for (int linha = 0; linha < n; linha++) {
                double anterior = x[linha];
                double termo = b[linha];

                for (int coluna = 0; coluna < n; coluna++) {
                    if (coluna != linha) {
                        termo -= a[linha][coluna] * x[coluna];
                    }
                }

                double novo = termo / a[linha][linha];
                if (!Double.isFinite(novo)) {
                    return erro("A iteração " + iteracao + " produziu um valor não finito para x" + (linha + 1) + ".", passos);
                }
                x[linha] = novo;
                maiorDiferenca = Math.max(maiorDiferenca, Math.abs(novo - anterior));
                maiorModulo = Math.max(maiorModulo, Math.abs(novo));
            }

            erroParada = maiorModulo == 0.0 ? maiorDiferenca : maiorDiferenca / maiorModulo;
            passos.append(String.format("%-7d", iteracao));

            for (double valor : x) {
                passos.append(String.format(" %16s", numero(valor)));
            }
            passos.append(String.format(" %16s%n", numero(erroParada)));

            if (erroParada < tolerancia) {
                passos.append("\nMOTIVO DA PARADA\n")
                        .append(maiorModulo == 0.0 ? "Tolerância absoluta" : "Tolerância relativa")
                        .append(" atingida na iteração ").append(iteracao).append(".\n");
                registrarVerificacao(passos, maiorDiferenca, maiorModulo, erroParada, tolerancia, " < ");
                return SaidaSistemas.sucesso(a, b, x, passos);
            }
        }

        passos.append("\nMOTIVO DA PARADA\nMáximo de iterações atingido antes da tolerância.\n").append("Iterações realizadas = ").append(maxIteracoes).append('\n');
        registrarVerificacao(passos, maiorDiferenca, maiorModulo, erroParada, tolerancia, " >= ");
        return erro("Limite de iterações atingido antes da tolerância.", passos);
    }

    private static void registrarVerificacao(StringBuilder passos, double maiorDiferenca, double maiorModulo, double erroParada, double tolerancia, String comparacao) {
        passos.append("\nVERIFICAÇÃO DA PARADA\n");

        if (maiorModulo == 0.0) {
            passos.append("Vetor atual nulo. Erro absoluto = ").append(numero(maiorDiferenca));
        } else {
            passos.append("Erro relativo = maior diferença / maior |x atual|\n")
                    .append(numero(maiorDiferenca)).append(" / ").append(numero(maiorModulo))
                    .append(" = ").append(numero(erroParada));
        }
        passos.append('\n').append(numero(erroParada)).append(comparacao).append(numero(tolerancia)).append('\n');
    }

    private static double sassenfeld(double[][] a) {
        double[] beta = new double[a.length];
        double maiorBeta = 0.0;

        for (int linha = 0; linha < a.length; linha++) {
            for (int coluna = 0; coluna < a.length; coluna++) {
                if (coluna != linha && a[linha][coluna] != 0.0) {
                    double peso = coluna < linha ? beta[coluna] : 1.0;
                    if (peso > 0.0) {
                        beta[linha] += Math.abs(a[linha][coluna] / a[linha][linha]) * peso;
                    }
                }
            }
            maiorBeta = Math.max(maiorBeta, beta[linha]);
        }
        return maiorBeta;
    }
}
