package org.example.projetofinal_calcnumerico.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.example.projetofinal_calcnumerico.metodos.sistemas.EliminacaoGauss;
import org.example.projetofinal_calcnumerico.metodos.sistemas.FatoracaoLU;
import org.example.projetofinal_calcnumerico.metodos.sistemas.GaussSeidel;
import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.ConversorEntrada;

public class SistemasController {

    private static final String GAUSS = "Eliminação de Gauss";
    private static final String LU = "Fatoração LU";
    private static final String GAUSS_SEIDEL = "Gauss-Seidel";

    @FXML private ComboBox<String> seletorMetodoSistemas;
    @FXML private HBox grupoGaussSeidel;
    @FXML private TextField campoVetorInicial;
    @FXML private TextField campoToleranciaSeidel;
    @FXML private TextField campoMaxIteracoesSeidel;

    @FXML private TextArea campoMatrizA;
    @FXML private TextArea campoVetorB;
    @FXML private TextArea areaResultado;

    @FXML
    private void initialize() {
        seletorMetodoSistemas.getItems().setAll(GAUSS, LU, GAUSS_SEIDEL);
        seletorMetodoSistemas.getSelectionModel().select(GAUSS);
        grupoGaussSeidel.visibleProperty().bind(seletorMetodoSistemas.valueProperty().isEqualTo(GAUSS_SEIDEL));
        grupoGaussSeidel.managedProperty().bind(grupoGaussSeidel.visibleProperty());
        seletorMetodoSistemas.valueProperty().addListener((observavel, anterior, novo) -> areaResultado.clear());
    }

    @FXML
    private void calcular() {
        try {
            double[][] matriz = ConversorEntrada.lerMatrizQuadrada(campoMatrizA.getText(), "Matriz A");
            double[] vetor = ConversorEntrada.lerVetor(campoVetorB.getText(), "Vetor b");

            if (vetor.length != matriz.length) {
                throw new IllegalArgumentException("O vetor b deve ter " + matriz.length
                        + " elementos, um para cada linha da matriz A. Recebidos: " + vetor.length + ".");
            }

            ResultadoCalculo resultado = switch (seletorMetodoSistemas.getValue()) {
                case LU -> FatoracaoLU.calcular(matriz, vetor);
                case GAUSS_SEIDEL -> calcularGaussSeidel(matriz, vetor);
                default -> EliminacaoGauss.calcular(matriz, vetor);
            };
            areaResultado.setText(resultado.formatarParaTela());
        } catch (IllegalArgumentException erro) {
            areaResultado.setText("Erro: " + erro.getMessage());
        }
        areaResultado.positionCaret(0);
    }

    private ResultadoCalculo calcularGaussSeidel(double[][] matriz, double[] vetor) {
        double[] xInicial = ConversorEntrada.lerVetor(campoVetorInicial.getText(), "Vetor inicial x₀");
        double tolerancia = ConversorEntrada.lerDoublePositivo(campoToleranciaSeidel.getText(), "Tolerância de Gauss-Seidel");
        int maxIteracoes = ConversorEntrada.lerIntPositivo(campoMaxIteracoesSeidel.getText(), "Máximo de iterações");
        return GaussSeidel.calcular(matriz, vetor, xInicial, tolerancia, maxIteracoes);
    }
}
