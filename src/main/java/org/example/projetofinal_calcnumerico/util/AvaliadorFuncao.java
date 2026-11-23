package org.example.projetofinal_calcnumerico.util;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public class AvaliadorFuncao {

    private static final Function LN = new Function("ln", 1) {
        @Override
        public double apply(double... args) {
            return Math.log(args[0]);
        }
    };

    private static final Function LOG10 = new Function("log", 1) {
        @Override
        public double apply(double... args) {
            return Math.log10(args[0]);
        }
    };

    private static final Function SEN = new Function("sen", 1) {
        @Override
        public double apply(double... args) {
            return Math.sin(args[0]);
        }
    };

    private static final Function TG = new Function("tg", 1) {
        @Override
        public double apply(double... args) {
            return Math.tan(args[0]);
        }
    };

    private final Expression expressao;

    public AvaliadorFuncao(String textoFuncao) {
        if (textoFuncao == null || textoFuncao.trim().isEmpty()) {
            throw new IllegalArgumentException("A expressão da função não pode ficar vazia.");
        }
        String tratada = textoFuncao.trim().replace(",", ".");
        try {
            this.expressao = new ExpressionBuilder(tratada)
                    .variable("x")
                    .functions(LN, LOG10, SEN, TG)
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Expressão inválida: '" + textoFuncao + "'. " +
                    "Use x como variável (ex.: x^3 - 9*x + 3, exp(-x^2) - cos(x), ln(x), log(x)).");
        }
    }

    public double avaliar(double x) {
        try {
            double resultado = expressao.setVariable("x", x).evaluate();
            if (Double.isNaN(resultado) || Double.isInfinite(resultado)) {
                throw new IllegalArgumentException(
                        "A função resultou em valor indefinido (fora do domínio ou divisão por zero) para x = " + x
                );
            }
            return resultado;
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Divisão por zero ao avaliar a função em x = " + x);
        }
    }
}