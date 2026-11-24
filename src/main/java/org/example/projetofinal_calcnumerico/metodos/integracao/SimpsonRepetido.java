package org.example.projetofinal_calcnumerico.metodos.integracao;

import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.AvaliadorFuncao;
import org.example.projetofinal_calcnumerico.util.ConversorEntrada;

public class SimpsonRepetido {

    public static ResultadoCalculo calcular(AvaliadorFuncao f, double a, double b, int m) {
        String erro = ValidadorIntegracao.validarIntervaloESubintervalos(a, b, m, true);
        if (erro != null) {
            return ResultadoCalculo.erro(erro);
        }

        double h = (b - a) / m;
        double fa = f.avaliar(a);
        double fb = f.avaliar(b);
        double somaImpares = 0.0;
        double somaPares = 0.0;

        StringBuilder detalhes = new StringBuilder();
        detalhes.append("Subintervalos m = ").append(m)
                .append("  |  Passo h = ").append(ConversorEntrada.formatar(h)).append("\n\n");
        detalhes.append(String.format("i = %-3d | x = %-12s | f(x) = %-12s | peso = 1%n",
                0, ConversorEntrada.formatar(a), ConversorEntrada.formatar(fa)));

        for (int i = 1; i <= m - 1; i++) {
            double xi = a + i * h;
            double fxi = f.avaliar(xi);
            int peso;

            if (i % 2 != 0) {
                somaImpares += fxi;
                peso = 4;
            } else {
                somaPares += fxi;
                peso = 2;
            }

            detalhes.append(String.format("i = %-3d | x = %-12s | f(x) = %-12s | peso = %d%n",
                    i, ConversorEntrada.formatar(xi), ConversorEntrada.formatar(fxi), peso));
        }

        detalhes.append(String.format("i = %-3d | x = %-12s | f(x) = %-12s | peso = 1%n",
                m, ConversorEntrada.formatar(b), ConversorEntrada.formatar(fb)));

        double integral = (h / 3.0) * (fa + fb + 4.0 * somaImpares + 2.0 * somaPares);

        String resumo = String.format(
                "1/3 de Simpson Repetida (m = %d): I ≈ %s",
                m, ConversorEntrada.formatar(integral)
        );
        return ResultadoCalculo.sucesso(resumo, detalhes.toString());
    }
}