package org.example.projetofinal_calcnumerico.metodos.raizes;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.AvaliadorFuncao;

public class Newton {

    public static ResultadoCalculo calcular(AvaliadorFuncao funcao, AvaliadorFuncao derivada, double x0, double tolerancia, int maxIteracoes) {
        return calcular(funcao, derivada, x0, tolerancia, maxIteracoes, CasasDecimais.AUTOMATICO);
    }

    public static ResultadoCalculo calcular(AvaliadorFuncao funcao, AvaliadorFuncao derivada, double x0, double tolerancia, int maxIteracoes, CasasDecimais casasDecimais) {
        if (!Double.isFinite(x0)) {
            return ResultadoCalculo.erro("Informe uma aproximação inicial finita.");
        }
        if (!Double.isFinite(tolerancia) || tolerancia <= 0 || maxIteracoes <= 0) {
            return ResultadoCalculo.erro("A tolerância e o máximo de iterações devem ser positivos.");
        }

        SaidaRaizes saida = new SaidaRaizes(casasDecimais, "x atual", "f(x atual)", "f'(x atual)", "x novo", "f(x novo)", "Variação");
        double x = x0;
        double fx = funcao.avaliar(x);
        if (Math.abs(fx) <= tolerancia) {
            return saida.sucesso(x, 0, "Tolerância da função atingida na aproximação inicial.", saida.verificarResiduo(x, fx, tolerancia));
        }

        int iteracoes = 0;
        double variacao = 0.0;
        while (iteracoes < maxIteracoes) {
            double dfx = derivada.avaliar(x);
            if (dfx == 0.0) {
                return saida.erro("Derivada igual a zero.", "f'(" + saida.numero(x) + ") = 0. A atualização exige dividir por f'(x).");
            }

            double xNovo = x - fx / dfx;
            if (!Double.isFinite(xNovo)) {
                return saida.erro("A próxima aproximação ficou fora do alcance do double.", "x novo = " + x + " - (" + fx + " / " + dfx + ") = " + xNovo);
            }

            double fNovo = funcao.avaliar(xNovo);
            variacao = Math.abs(xNovo - x);
            iteracoes++;
            saida.registrar(iteracoes, x, fx, dfx, xNovo, fNovo, variacao);

            if (Math.abs(fNovo) <= tolerancia) {
                return saida.sucesso(xNovo, iteracoes, "Tolerância da função atingida.", saida.verificarResiduo(xNovo, fNovo, tolerancia));
            }
            if (xNovo == x) {
                return saida.erro("A precisão do double impediu o avanço de Newton.", "x novo = x atual = " + x + ", com |f(x novo)| acima da tolerância.");
            }
            if (variacao <= tolerancia) {
                return saida.sucesso(xNovo, iteracoes, "Tolerância entre aproximações atingida.", saida.verificarVariacao(x, xNovo, tolerancia));
            }

            x = xNovo;
            fx = fNovo;
        }

        return saida.erro("Limite de iterações atingido antes da tolerância.",
                "Iterações realizadas = " + iteracoes + " (máximo: " + maxIteracoes + ")."
                        + "\n|f(x)| = " + saida.numero(Math.abs(fx))
                        + " > " + saida.numero(tolerancia)
                        + "\nVariação = " + saida.numero(variacao)
                        + " > " + saida.numero(tolerancia));
    }
}
