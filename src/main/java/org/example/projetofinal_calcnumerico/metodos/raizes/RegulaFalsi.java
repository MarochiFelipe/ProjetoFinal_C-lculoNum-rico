package org.example.projetofinal_calcnumerico.metodos.raizes;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.AvaliadorFuncao;

public class RegulaFalsi {

    public static ResultadoCalculo calcular(AvaliadorFuncao funcao, double a, double b, double toleranciaIntervalo, double toleranciaFuncao, int maxIteracoes) {
        return calcular(funcao, a, b, toleranciaIntervalo, toleranciaFuncao, maxIteracoes, CasasDecimais.AUTOMATICO);
    }

    public static ResultadoCalculo calcular(AvaliadorFuncao funcao, double a, double b, double toleranciaIntervalo, double toleranciaFuncao, int maxIteracoes, CasasDecimais casasDecimais) {

        if (!Double.isFinite(a) || !Double.isFinite(b) || a >= b) {
            return ResultadoCalculo.erro("Informe limites finitos, com o limite inferior menor que o superior.");
        }
        if (!Double.isFinite(toleranciaIntervalo) || toleranciaIntervalo <= 0 || !Double.isFinite(toleranciaFuncao) || toleranciaFuncao <= 0 || maxIteracoes <= 0) {
            return ResultadoCalculo.erro("As tolerâncias e o máximo de iterações devem ser positivos.");
        }

        double fa = funcao.avaliar(a);
        double fb = funcao.avaliar(b);
        SaidaRaizes saida = new SaidaRaizes(casasDecimais, "a", "b", "x", "f(x)");

        if (fa == 0.0 || fb == 0.0) {
            double raiz = fa == 0.0 ? a : b;
            return saida.sucesso(raiz, 0, "Raiz encontrada em um dos limites.", saida.raizExata(raiz));
        }
        if (Math.signum(fa) == Math.signum(fb)) {
            return ResultadoCalculo.erro("Escolha limites em que f(a) e f(b) tenham sinais opostos.");
        }
        if (Math.abs(fa) <= toleranciaFuncao) {
            return saida.sucesso(a, 0, "Tolerância da função atingida no limite inferior.", saida.verificarResiduo(a, fa, toleranciaFuncao));
        }
        if (Math.abs(fb) <= toleranciaFuncao) {
            return saida.sucesso(b, 0, "Tolerância da função atingida no limite superior.", saida.verificarResiduo(b, fb, toleranciaFuncao));
        }

        int iteracoes = 0;
        while (b - a > toleranciaIntervalo && iteracoes < maxIteracoes) {
            double x = aproximar(a, b, fa, fb);
            if (!(a < x && x < b)) {
                return saida.erro("A precisão do double impediu reduzir o intervalo.", "A aproximação " + x + " ficou fora do interior do intervalo.");
            }

            double fx = funcao.avaliar(x);
            iteracoes++;
            saida.registrar(iteracoes, a, b, x, fx);

            if (Math.abs(fx) <= toleranciaFuncao) {
                return saida.sucesso(x, iteracoes, "Tolerância da função atingida.", saida.verificarResiduo(x, fx, toleranciaFuncao));
            }

            if (Math.signum(fa) == Math.signum(fx)) {
                a = x;
                fa = fx;
            } else {
                b = x;
                fb = fx;
            }
        }

        if (b - a > toleranciaIntervalo) {
            return saida.erro("Limite de iterações atingido antes das tolerâncias.", "Iterações realizadas = " + iteracoes + " (máximo: " + maxIteracoes + ").");
        }

        return saida.sucesso(a / 2.0 + b / 2.0, iteracoes, "Tolerância do intervalo atingida.", saida.verificarIntervalo(a, b, toleranciaIntervalo));
    }

    private static double aproximar(double a, double b, double fa, double fb) {
        double escala = Math.max(Math.abs(fa), Math.abs(fb));
        double pesoA = Math.abs(fb) / escala;
        double pesoB = Math.abs(fa) / escala;
        double soma = pesoA + pesoB;
        return a * (pesoA / soma) + b * (pesoB / soma);
    }
}
