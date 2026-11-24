package org.example.projetofinal_calcnumerico.metodos.integracao;

class ValidadorIntegracao {

    private static final double TOLERANCIA_ESPACAMENTO = 1e-6;

    static String validarIntervaloESubintervalos(double a, double b, int m, boolean exigePar) {
        if (a >= b) {
            return "O limite inferior 'a' deve ser menor que o limite superior 'b'.";
        }
        if (m < 1) {
            return "O número de subintervalos 'm' deve ser pelo menos 1.";
        }
        if (exigePar && (m % 2 != 0)) {
            return "Para a Regra 1/3 de Simpson Repetida, o número de subintervalos 'm' deve ser par.";
        }
        return null;
    }

    static String validarTabelaDiscreta(double[] x, double[] y) {
        if (x == null || y == null || x.length != y.length) {
            return "A quantidade de valores em x e f(x) deve ser igual.";
        }
        if (x.length < 2) {
            return "Informe pelo menos 2 pontos para integrar.";
        }

        int m = x.length - 1;
        for (int i = 0; i <= m - 1; i++) {
            if (x[i + 1] <= x[i]) {
                return "Os valores de x devem estar em ordem estritamente crescente (x_0 < x_1 < ... < x_m).";
            }
        }
        return null;
    }

    static boolean ehIgualmenteEspacado(double[] x) {
        int m = x.length - 1;
        double hPadrao = x[1] - x[0];

        for (int i = 1; i <= m - 1; i++) {
            double hi = x[i + 1] - x[i];
            if (Math.abs(hi - hPadrao) > TOLERANCIA_ESPACAMENTO) {
                return false;
            }
        }
        return true;
    }
}