package org.example.projetofinal_calcnumerico.metodos.integracao;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.ConversorEntrada;

public class IntegracaoDiscreta {

    public static ResultadoCalculo calcularTrapezio(double[] x, double[] y) {
        String erro = ValidadorIntegracao.validarTabelaDiscreta(x, y);
        if (erro != null) {
            return ResultadoCalculo.erro(erro);
        }

        int m = x.length - 1;
        double integral = 0.0;
        StringBuilder detalhes = new StringBuilder();
        detalhes.append("Número de subintervalos m = ").append(m).append("\n\n");

        for (int i = 0; i <= m - 1; i++) {
            double hi = x[i + 1] - x[i];
            double areaSubintervalo = (hi / 2.0) * (y[i] + y[i + 1]);
            integral += areaSubintervalo;

            detalhes.append(String.format(
                    "Subintervalo [%s, %s] | h = %s | Área = %s%n",
                    ConversorEntrada.formatar(x[i]),
                    ConversorEntrada.formatar(x[i + 1]),
                    ConversorEntrada.formatar(hi),
                    ConversorEntrada.formatar(areaSubintervalo)
            ));
        }

        String resumo = String.format(
                "Caso Discreto - Trapézios (%d subintervalos): I ≈ %s",
                m, ConversorEntrada.formatar(integral)
        );
        return ResultadoCalculo.sucesso(resumo, detalhes.toString());
    }

    public static ResultadoCalculo calcularSimpson(double[] x, double[] y) {
        String erro = ValidadorIntegracao.validarTabelaDiscreta(x, y);
        if (erro != null) {
            return ResultadoCalculo.erro(erro);
        }

        int m = x.length - 1;
        if (m % 2 != 0) {
            return ResultadoCalculo.erro(
                    "1/3 de Simpson exige um número par de subintervalos (número ímpar de pontos)."
            );
        }
        if (!ValidadorIntegracao.ehIgualmenteEspacado(x)) {
            return ResultadoCalculo.erro(
                    "1/3 de Simpson exige pontos de x igualmente espaçados (passo h constante)."
            );
        }

        double h = x[1] - x[0];
        double somaImpares = 0.0;
        double somaPares = 0.0;

        for (int i = 1; i <= m - 1; i++) {
            if (i % 2 != 0) {
                somaImpares += y[i];
            } else {
                somaPares += y[i];
            }
        }

        double integral = (h / 3.0) * (y[0] + y[m] + 4.0 * somaImpares + 2.0 * somaPares);

        StringBuilder detalhes = new StringBuilder();
        detalhes.append("Subintervalos m = ").append(m)
                .append("  |  Passo constante h = ").append(ConversorEntrada.formatar(h)).append("\n");
        detalhes.append("Extremos (y_0 + y_m) = ").append(ConversorEntrada.formatar(y[0] + y[m])).append("\n");
        detalhes.append("Soma dos índices ímpares (x4) = ").append(ConversorEntrada.formatar(somaImpares)).append("\n");
        detalhes.append("Soma dos índices pares (x2)   = ").append(ConversorEntrada.formatar(somaPares)).append("\n");

        String resumo = String.format(
                "Caso Discreto - 1/3 de Simpson (%d subintervalos): I ≈ %s",
                m, ConversorEntrada.formatar(integral)
        );
        return ResultadoCalculo.sucesso(resumo, detalhes.toString());
    }
}