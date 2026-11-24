package org.example.projetofinal_calcnumerico.metodos.interpolacao;

class ValidadorInterpolacao {

    static String validar(double[] x, double[] y) {
        if (x == null || y == null || x.length != y.length) {
            return "A quantidade de valores em x e f(x) deve ser igual.";
        }
        if (x.length < 2) {
            return "Informe pelo menos 2 pontos para interpolar.";
        }

        int n = x.length - 1;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                if (x[i] == x[j]) {
                    return "Os valores de x devem ser distintos (x_" + i + " = x_" + j + " = " + x[i] + ").";
                }
            }
        }
        return null;
    }

    static String avisoExtrapolacao(double[] x, double xAlvo) {
        double min = x[0];
        double max = x[0];
        for (double valor : x) {
            if (valor < min) min = valor;
            if (valor > max) max = valor;
        }
        if (xAlvo < min || xAlvo > max) {
            return "Aviso: x = " + xAlvo + " está fora do intervalo [" + min + ", " + max + "] (extrapolação).\n\n";
        }
        return "";
    }
}