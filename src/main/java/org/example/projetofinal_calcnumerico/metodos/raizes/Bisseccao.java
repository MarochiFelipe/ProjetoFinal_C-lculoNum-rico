package org.example.projetofinal_calcnumerico.metodos.raizes;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.AvaliadorFuncao;

public class Bisseccao {

    public static ResultadoCalculo calcular(AvaliadorFuncao funcao, double a, double b, double tolerancia, int maxIteracoes) {
        return calcular(funcao, a, b, tolerancia, maxIteracoes, CasasDecimais.AUTOMATICO);
    }

    public static ResultadoCalculo calcular(AvaliadorFuncao funcao, double a, double b, double tolerancia, int maxIteracoes, CasasDecimais casasDecimais) {
        if (!Double.isFinite(a) || !Double.isFinite(b) || a >= b) {
            return ResultadoCalculo.erro("Informe limites finitos, com o limite inferior menor que o superior.");
        }
        if (!Double.isFinite(tolerancia) || tolerancia <= 0 || maxIteracoes <= 0) {
            return ResultadoCalculo.erro("A tolerância e o máximo de iterações devem ser positivos.");
        }

        double fa = funcao.avaliar(a);
        double fb = funcao.avaliar(b);
        SaidaRaizes saida = new SaidaRaizes(casasDecimais, "a", "b", "m", "f(m)");

        if (fa == 0.0) {
            return saida.sucesso(a, 0, "Raiz encontrada no limite inferior.", saida.raizExata(a));
        }
        if (fb == 0.0) {
            return saida.sucesso(b, 0, "Raiz encontrada no limite superior.", saida.raizExata(b));
        }
        if (mesmoSinal(fa, fb)) {
            return ResultadoCalculo.erro("Escolha limites em que f(a) e f(b) tenham sinais opostos.");
        }

        int iteracoes = 0;
        while (b - a > tolerancia && iteracoes < maxIteracoes) {
            double meio = pontoMedio(a, b);
            if (meio <= a || meio >= b) {
                return saida.erro("A precisão do double impediu reduzir o intervalo.", "O ponto médio " + meio + " coincidiu com um dos limites.");
            }

            double fm = funcao.avaliar(meio);
            iteracoes++;
            saida.registrar(iteracoes, a, b, meio, fm);

            if (fm == 0.0) {
                return saida.sucesso(meio, iteracoes, "Raiz encontrada no ponto médio.", saida.raizExata(meio));
            }

            if (mesmoSinal(fa, fm)) {
                a = meio;
                fa = fm;
            } else {
                b = meio;
            }
        }

        if (b - a > tolerancia) {
            return saida.erro("Limite de iterações atingido antes da tolerância.", "Iterações realizadas = " + iteracoes + " (máximo: " + maxIteracoes + ").");
        }

        return saida.sucesso(pontoMedio(a, b), iteracoes, "Tolerância do intervalo atingida.",
                saida.verificarIntervalo(a, b, tolerancia));
    }

    private static boolean mesmoSinal(double primeiro, double segundo) {
        return (primeiro < 0) == (segundo < 0);
    }

    private static double pontoMedio(double a, double b) {
        return a / 2.0 + b / 2.0;
    }
}
