package org.example.projetofinal_calcnumerico.metodos.sistemas;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;

import static org.example.projetofinal_calcnumerico.metodos.sistemas.SaidaSistemas.erro;
import static org.example.projetofinal_calcnumerico.metodos.sistemas.SaidaSistemas.numero;
import static org.example.projetofinal_calcnumerico.metodos.sistemas.SaidaSistemas.registrarMatriz;

public class EliminacaoGauss {

    public static ResultadoCalculo calcular(double[][] a, double[] b) {
        if (a == null || b == null || a.length == 0 || a.length != b.length) {
            return ResultadoCalculo.erro("Informe uma matriz A e um vetor b de dimensões compatíveis.");
        }

        int n = a.length;
        double[][] aumentada = new double[n][n + 1];
        double[] escalas = new double[n];

        for (int linha = 0; linha < n; linha++) {
            if (a[linha] == null || a[linha].length != n || !Double.isFinite(b[linha])) {
                return ResultadoCalculo.erro("Informe uma matriz quadrada e um vetor b com valores finitos.");
            }

            for (int coluna = 0; coluna < n; coluna++) {
                double coeficiente = a[linha][coluna];

                if (!Double.isFinite(coeficiente)) {
                    return ResultadoCalculo.erro("A matriz A deve conter apenas valores finitos.");
                }

                aumentada[linha][coluna] = coeficiente;
                escalas[linha] = Math.max(escalas[linha], Math.abs(coeficiente));
            }
            aumentada[linha][n] = b[linha];
        }

        StringBuilder passos = new StringBuilder("MATRIZ AUMENTADA INICIAL [A | b]\n");
        registrarMatriz(passos, aumentada);

        for (int coluna = 0; coluna < n; coluna++) {
            int linhaPivo = coluna;
            for (int linha = coluna + 1; linha < n; linha++) {
                if (Math.abs(aumentada[linha][coluna]) > Math.abs(aumentada[linhaPivo][coluna])) {
                    linhaPivo = linha;
                }
            }

            double pivo = aumentada[linhaPivo][coluna];
            passos.append("\nETAPA ").append(coluna + 1).append(" • Pivô = ").append(numero(pivo)).append('\n');

            if (pivo == 0.0) {
                return erro("Pivô zero. A matriz A é singular e o sistema não tem solução única.", passos);
            }
            if (Math.abs(pivo) / escalas[linhaPivo] <= n * Math.ulp(1.0)) {
                return erro("Pivô pequeno em relação aos coeficientes da linha. " + "A precisão do double não permite resolver o sistema com segurança.", passos);
            }

            if (linhaPivo != coluna) {
                double[] linhaTemporaria = aumentada[coluna];
                aumentada[coluna] = aumentada[linhaPivo];
                aumentada[linhaPivo] = linhaTemporaria;

                double escalaTemporaria = escalas[coluna];
                escalas[coluna] = escalas[linhaPivo];
                escalas[linhaPivo] = escalaTemporaria;

                passos.append("Troca L").append(coluna + 1).append(" com L").append(linhaPivo + 1).append('\n');
            }

            for (int linha = coluna + 1; linha < n; linha++) {
                double fator = aumentada[linha][coluna] / pivo;
                passos.append("L").append(linha + 1).append(" ← L").append(linha + 1)
                        .append(" - (").append(numero(fator)).append(") × L")
                        .append(coluna + 1).append('\n');

                for (int j = coluna + 1; j <= n; j++) {
                    aumentada[linha][j] -= fator * aumentada[coluna][j];
                    if (!Double.isFinite(aumentada[linha][j])) {
                        return erro("Um valor do escalonamento ultrapassou a precisão do double.", passos);
                    }
                }
                aumentada[linha][coluna] = 0.0;
            }

            if (coluna < n - 1) {
                registrarMatriz(passos, aumentada);
            }
        }

        passos.append("\nSUBSTITUIÇÃO RETROATIVA\n");
        double[] solucao = new double[n];

        for (int linha = n - 1; linha >= 0; linha--) {
            double soma = 0.0;

            for (int coluna = linha + 1; coluna < n; coluna++) {
                soma += aumentada[linha][coluna] * solucao[coluna];
            }

            double termo = aumentada[linha][n];
            double pivo = aumentada[linha][linha];
            solucao[linha] = (termo - soma) / pivo;

            if (!Double.isFinite(solucao[linha])) {
                return erro("A substituição retroativa produziu uma incógnita não finita.", passos);
            }

            passos.append("x").append(linha + 1).append(" = (").append(numero(termo))
                    .append(" - (").append(numero(soma)).append(")) / (")
                    .append(numero(pivo)).append(") = ").append(numero(solucao[linha])).append('\n');
        }

        return SaidaSistemas.sucesso(a, b, solucao, passos);
    }
}
