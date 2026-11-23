package org.example.projetofinal_calcnumerico.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConversorEntrada {

    public static double lerDouble(String texto, String nomeCampo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo '" + nomeCampo + "' não pode ficar vazio.");
        }
        try {
            double valor = Double.parseDouble(texto.trim().replace(",", "."));
            if (Double.isNaN(valor) || Double.isInfinite(valor)) {
                throw new IllegalArgumentException("Valor inválido no campo '" + nomeCampo + "'.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo '" + nomeCampo + "' deve conter um número válido.");
        }
    }

    public static double lerDoublePositivo(String texto, String nomeCampo) {
        double valor = lerDouble(texto, nomeCampo);
        if (valor <= 0) {
            throw new IllegalArgumentException("O campo '" + nomeCampo + "' deve ser maior que zero.");
        }
        return valor;
    }

    public static int lerIntPositivo(String texto, String nomeCampo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo '" + nomeCampo + "' não pode ficar vazio.");
        }
        try {
            int valor = Integer.parseInt(texto.trim());
            if (valor <= 0) {
                throw new IllegalArgumentException("O campo '" + nomeCampo + "' deve ser um inteiro maior que zero.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo '" + nomeCampo + "' deve conter um número inteiro válido.");
        }
    }

    /**
     * Lê um vetor de números separados por espaço, ponto e vírgula ou quebra de linha.
     * Ex.: "1 2.5 3" ou "1; 2,5; 3"
     */
    public static double[] lerVetor(String texto, String nomeCampo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo '" + nomeCampo + "' não pode ficar vazio.");
        }
        String[] partes = texto.trim().split("[;\\s]+");
        double[] vetor = new double[partes.length];
        for (int i = 0; i < partes.length; i++) {
            vetor[i] = lerDouble(partes[i], nomeCampo + " (posição " + (i + 1) + ")");
        }
        return vetor;
    }

    /**
     * Lê uma matriz quadrada n x n: uma linha por quebra de linha,
     * elementos separados por espaço ou ponto e vírgula.
     */
    public static double[][] lerMatrizQuadrada(String texto, String nomeCampo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo '" + nomeCampo + "' não pode ficar vazio.");
        }
        String[] linhasBrutas = texto.trim().split("\\R+");
        List<double[]> linhas = new ArrayList<>();

        for (String linha : linhasBrutas) {
            if (!linha.trim().isEmpty()) {
                linhas.add(lerVetor(linha, nomeCampo));
            }
        }

        int n = linhas.size();
        if (n == 0) {
            throw new IllegalArgumentException("A matriz '" + nomeCampo + "' não pode ficar vazia.");
        }

        double[][] matriz = new double[n][n];
        for (int i = 0; i < n; i++) {
            if (linhas.get(i).length != n) {
                throw new IllegalArgumentException(
                        "A matriz '" + nomeCampo + "' deve ser quadrada (" + n + "x" + n + "). " +
                                "A linha " + (i + 1) + " possui " + linhas.get(i).length + " elemento(s)."
                );
            }
            matriz[i] = linhas.get(i);
        }
        return matriz;
    }

    public static String formatar(double valor) {
        return String.format(Locale.US, "%.6f", valor);
    }
}