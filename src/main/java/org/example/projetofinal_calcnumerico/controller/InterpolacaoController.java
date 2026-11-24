package org.example.projetofinal_calcnumerico.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.projetofinal_calcnumerico.metodos.interpolacao.Lagrange;
import org.example.projetofinal_calcnumerico.metodos.interpolacao.Newton;
import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.ConversorEntrada;

public class InterpolacaoController {

    @FXML
    private TextField txtX;

    @FXML
    private TextField txtY;

    @FXML
    private TextField txtXAlvo;

    @FXML
    private Label lblStatus;

    @FXML
    private TextArea txtResultado;

    @FXML
    private void onCalcularLagrange() {
        executarMetodo("LAGRANGE");
    }

    @FXML
    private void onCalcularNewton() {
        executarMetodo("NEWTON");
    }

    @FXML
    private void onLimpar() {
        txtX.clear();
        txtY.clear();
        txtXAlvo.clear();
        lblStatus.setText("");
        txtResultado.clear();
    }

    private void executarMetodo(String metodo) {
        try {
            double[] x = ConversorEntrada.lerVetor(txtX.getText(), "Valores de x");
            double[] y = ConversorEntrada.lerVetor(txtY.getText(), "Valores de f(x)");
            double xAlvo = ConversorEntrada.lerDouble(txtXAlvo.getText(), "Valor de x a interpolar");

            ResultadoCalculo resultado;
            if (metodo.equals("LAGRANGE")) {
                resultado = Lagrange.calcular(x, y, xAlvo);
            } else {
                resultado = Newton.calcular(x, y, xAlvo);
            }

            exibirResultado(resultado);
        } catch (IllegalArgumentException e) {
            exibirResultado(ResultadoCalculo.erro(e.getMessage()));
        }
    }

    private void exibirResultado(ResultadoCalculo resultado) {
        if (resultado.isSucesso()) {
            lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #1b5e20;");
            lblStatus.setText(resultado.getResultadoFinal());
        } else {
            lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #b71c1c;");
            lblStatus.setText("Erro: " + resultado.getResultadoFinal());
        }
        txtResultado.setText(resultado.formatarParaTela());
    }
}