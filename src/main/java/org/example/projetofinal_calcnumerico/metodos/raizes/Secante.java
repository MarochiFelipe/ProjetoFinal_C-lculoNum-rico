package org.example.projetofinal_calcnumerico.metodos.raizes;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.AvaliadorFuncao;

public class Secante {

    public static ResultadoCalculo calcular(AvaliadorFuncao funcao, double x0, double x1, double tolerancia, int maxIteracoes) {
        return calcular(funcao, x0, x1, tolerancia, maxIteracoes, CasasDecimais.AUTOMATICO);
    }

    public static ResultadoCalculo calcular(AvaliadorFuncao funcao, double x0, double x1, double tolerancia, int maxIteracoes, CasasDecimais casasDecimais) {
        if (!Double.isFinite(x0) || !Double.isFinite(x1) || x0 == x1) {
            return ResultadoCalculo.erro("Informe duas aproximações iniciais finitas e distintas.");
        }
        if (!Double.isFinite(tolerancia) || tolerancia <= 0 || maxIteracoes <= 0) {
            return ResultadoCalculo.erro("A tolerância e o máximo de iterações devem ser positivos.");
        }

        SaidaRaizes saida = new SaidaRaizes(casasDecimais, "x0", "x1", "x2", "f(x2)", "Variação");
        double f0 = funcao.avaliar(x0);
        if (Math.abs(f0) <= tolerancia) {
            return saida.sucesso(x0, 0, "Tolerância da função atingida na aproximação inicial.", saida.verificarResiduo(x0, f0, tolerancia));
        }

        double f1 = funcao.avaliar(x1);
        if (Math.abs(f1) <= tolerancia) {
            return saida.sucesso(x1, 0, "Tolerância da função atingida na aproximação inicial.", saida.verificarResiduo(x1, f1, tolerancia));
        }
        if (Math.abs(x1 - x0) <= tolerancia) {
            return saida.sucesso(x1, 0, "Tolerância entre as aproximações iniciais atingida.", saida.verificarVariacao(x0, x1, tolerancia));
        }

        for (int i = 0; i < maxIteracoes; i++) {
            int iteracao = i + 1;
            double denominador = f1 - f0;

            if (denominador == 0.0) {
                return saida.erro("Denominador zero. A função tem o mesmo valor nos dois pontos.", "f(x1) - f(x0) = " + saida.numero(f1) + " - (" + saida.numero(f0) + ") = 0");
            }

            double fracao = Double.isInfinite(denominador) ? (f1 / 2.0) / (f1 / 2.0 - f0 / 2.0) : f1 / denominador;
            double x2 = x1 - fracao * (x1 - x0);
            if (!Double.isFinite(x2)) {
                return saida.erro("A próxima aproximação ficou fora do alcance do double.", "O cálculo da próxima aproximação produziu um valor não finito.");
            }

            double f2 = funcao.avaliar(x2);
            double variacao = Math.abs(x2 - x1);
            saida.registrar(iteracao, x0, x1, x2, f2, variacao);

            if (Math.abs(f2) <= tolerancia) {
                return saida.sucesso(x2, iteracao, "Tolerância da função atingida.", saida.verificarResiduo(x2, f2, tolerancia));
            }
            if (x2 == x1) {
                return saida.erro("A precisão do double impediu o avanço da Secante.", "x2 = x1 = " + x1 + ", com |f(x2)| acima da tolerância.");
            }
            if (variacao <= tolerancia) {
                return saida.sucesso(x2, iteracao, "Tolerância entre aproximações atingida.", saida.verificarVariacao(x1, x2, tolerancia));
            }

            x0 = x1;
            f0 = f1;
            x1 = x2;
            f1 = f2;
        }

        return saida.erro("Limite de iterações atingido antes da tolerância.", "Iterações realizadas = " + maxIteracoes + " (máximo: " + maxIteracoes + ").");
    }
}
