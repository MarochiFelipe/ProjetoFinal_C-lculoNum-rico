package org.example.projetofinal_calcnumerico.metodos.interpolacao;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.ConversorEntrada;

public class Lagrange {

    public static ResultadoCalculo calcular(double[] x, double[] y, double xAlvo) {
        String erro = ValidadorInterpolacao.validar(x, y);
        if (erro != null) {
            return ResultadoCalculo.erro(erro);
        }

        int n = x.length - 1;
        double soma = 0.0;
        StringBuilder detalhes = new StringBuilder();
        detalhes.append(ValidadorInterpolacao.avisoExtrapolacao(x, xAlvo));

        for (int k = 0; k <= n; k++) {
            double numerador = 1.0;
            double denominador = 1.0;

            for (int j = 0; j <= n; j++) {
                if (j != k) {
                    numerador *= (xAlvo - x[j]);
                    denominador *= (x[k] - x[j]);
                }
            }

            double lk = numerador / denominador;
            double parcela = y[k] * lk;
            soma += parcela;

            detalhes.append(String.format(
                    "L_%d(%s) = %s  |  y_%d * L_%d = %s%n",
                    k,
                    ConversorEntrada.formatar(xAlvo),
                    ConversorEntrada.formatar(lk),
                    k, k,
                    ConversorEntrada.formatar(parcela)
            ));
        }

        String resumo = String.format(
                "Lagrange (grau <= %d): P_%d(%s) = %s",
                n, n, ConversorEntrada.formatar(xAlvo), ConversorEntrada.formatar(soma)
        );
        return ResultadoCalculo.sucesso(resumo, detalhes.toString());
    }
}