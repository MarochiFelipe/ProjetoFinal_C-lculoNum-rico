package org.example.projetofinal_calcnumerico.metodos.sistemas;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;

import java.util.Arrays;

import static org.example.projetofinal_calcnumerico.metodos.sistemas.SaidaSistemas.erro;
import static org.example.projetofinal_calcnumerico.metodos.sistemas.SaidaSistemas.numero;
import static org.example.projetofinal_calcnumerico.metodos.sistemas.SaidaSistemas.registrarMatriz;

public class FatoracaoLU {

    public static ResultadoCalculo calcular(double[][] a, double[] b) {
        if (a == null || b == null || a.length == 0 || a.length != b.length) {
            return ResultadoCalculo.erro("Informe uma matriz A e um vetor b de dimensões compatíveis.");
        }

        int n = a.length;
        double[][] l = new double[n][n];
        double[][] u = new double[n][n];
        double[] escalas = new double[n];
        int[] ordem = new int[n];

        for (int linha = 0; linha < n; linha++) {
            if (a[linha] == null || a[linha].length != n || !Double.isFinite(b[linha])) {
                return ResultadoCalculo.erro("Informe uma matriz quadrada e um vetor b com valores finitos.");
            }

            l[linha][linha] = 1.0;
            ordem[linha] = linha;
            for (int coluna = 0; coluna < n; coluna++) {
                double coeficiente = a[linha][coluna];

                if (!Double.isFinite(coeficiente)) {
                    return ResultadoCalculo.erro("A matriz A deve conter apenas valores finitos.");
                }

                u[linha][coluna] = coeficiente;
                escalas[linha] = Math.max(escalas[linha], Math.abs(coeficiente));
            }
        }

        StringBuilder passos = new StringBuilder("FATORAÇÃO LU COM PIVOTEAMENTO PARCIAL [PA = LU]\n");

        for (int coluna = 0; coluna < n; coluna++) {
            int linhaPivo = coluna;

            for (int linha = coluna + 1; linha < n; linha++) {
                if (Math.abs(u[linha][coluna]) > Math.abs(u[linhaPivo][coluna])) {
                    linhaPivo = linha;
                }
            }

            double pivo = u[linhaPivo][coluna];
            passos.append("\nETAPA ").append(coluna + 1).append(" • Pivô = ").append(numero(pivo)).append('\n');
            if (pivo == 0.0) {
                return erro("Pivô zero. A matriz A é singular e o sistema não tem solução única.", passos);
            }
            if (Math.abs(pivo) / escalas[linhaPivo] <= n * Math.ulp(1.0)) {
                return erro("Pivô pequeno em relação aos coeficientes da linha. " + "A precisão do double não permite resolver o sistema com segurança.", passos);
            }

            if (linhaPivo != coluna) {
                trocarLinhas(l, u, ordem, escalas, coluna, linhaPivo);
                passos.append("Troca L").append(coluna + 1).append(" com L").append(linhaPivo + 1).append('\n');
            }

            for (int linha = coluna + 1; linha < n; linha++) {
                double multiplicador = u[linha][coluna] / pivo;
                l[linha][coluna] = multiplicador;
                passos.append("Multiplicador m(").append(linha + 1).append(", ").append(coluna + 1).append(") = ").append(numero(multiplicador)).append('\n');

                for (int j = coluna + 1; j < n; j++) {
                    u[linha][j] -= multiplicador * u[coluna][j];
                    if (!Double.isFinite(u[linha][j])) {
                        return erro("Um valor da fatoração ultrapassou o alcance do double.", passos);
                    }
                }
                u[linha][coluna] = 0.0;
            }
        }

        passos.append("\nMATRIZ L\n");
        registrarMatriz(passos, l);
        passos.append("\nMATRIZ U\n");
        registrarMatriz(passos, u);

        double[] pb = new double[n];
        int[] ordemParaTela = new int[n];
        for (int linha = 0; linha < n; linha++) {
            pb[linha] = b[ordem[linha]];
            ordemParaTela[linha] = ordem[linha] + 1;
        }

        passos.append("\nOrdem das linhas em PA = ").append(Arrays.toString(ordemParaTela)).append("\nVetor Pb = ").append(Arrays.toString(pb)).append("\n\nSUBSTITUIÇÃO PROGRESSIVA [Ly = Pb]\n");

        double[] y = new double[n];

        for (int linha = 0; linha < n; linha++) {
            double soma = 0.0;

            for (int coluna = 0; coluna < linha; coluna++) {
                soma += l[linha][coluna] * y[coluna];
            }
            y[linha] = pb[linha] - soma;
            if (!Double.isFinite(y[linha])) {
                return erro("A substituição progressiva produziu um valor não finito.", passos);
            }
            passos.append("y").append(linha + 1).append(" = ").append(numero(pb[linha])).append(" - (").append(numero(soma)).append(") = ").append(numero(y[linha])).append('\n');
        }

        passos.append("\nSUBSTITUIÇÃO RETROATIVA [Ux = y]\n");
        double[] solucao = new double[n];

        for (int linha = n - 1; linha >= 0; linha--) {
            double soma = 0.0;

            for (int coluna = linha + 1; coluna < n; coluna++) {
                soma += u[linha][coluna] * solucao[coluna];
            }

            solucao[linha] = (y[linha] - soma) / u[linha][linha];
            if (!Double.isFinite(solucao[linha])) {
                return erro("A substituição retroativa produziu uma incógnita não finita.", passos);
            }
            passos.append("x").append(linha + 1).append(" = (").append(numero(y[linha]))
                    .append(" - (").append(numero(soma)).append(")) / (")
                    .append(numero(u[linha][linha])).append(") = ")
                    .append(numero(solucao[linha])).append('\n');
        }

        return SaidaSistemas.sucesso(a, b, solucao, passos);
    }

    private static void trocarLinhas(double[][] l, double[][] u, int[] ordem, double[] escalas, int linhaAtual, int linhaPivo) {
        double[] linhaTemporaria = u[linhaAtual];
        u[linhaAtual] = u[linhaPivo];
        u[linhaPivo] = linhaTemporaria;

        int ordemTemporaria = ordem[linhaAtual];
        ordem[linhaAtual] = ordem[linhaPivo];
        ordem[linhaPivo] = ordemTemporaria;

        double escalaTemporaria = escalas[linhaAtual];
        escalas[linhaAtual] = escalas[linhaPivo];
        escalas[linhaPivo] = escalaTemporaria;

        for (int coluna = 0; coluna < linhaAtual; coluna++) {
            double multiplicadorTemporario = l[linhaAtual][coluna];
            l[linhaAtual][coluna] = l[linhaPivo][coluna];
            l[linhaPivo][coluna] = multiplicadorTemporario;
        }
    }
}
