package org.example.projetofinal_calcnumerico.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.projetofinal_calcnumerico.metodos.integracao.IntegracaoDiscreta;
import org.example.projetofinal_calcnumerico.metodos.integracao.SimpsonRepetido;
import org.example.projetofinal_calcnumerico.metodos.integracao.TrapezioRepetido;
import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.AvaliadorFuncao;
import org.example.projetofinal_calcnumerico.util.ConversorEntrada;

public class IntegracaoController {

    @FXML
    private TextField txtFuncao;

    @FXML
    private TextField txtA;

    @FXML
    private TextField txtB;

    @FXML
    private TextField txtM;

    @FXML
    private TextField txtXDiscreto;

    @FXML
    private TextField txtYDiscreto;

    @FXML
    private Label lblStatus;

    @FXML
    private TextArea txtResultado;

    @FXML
    private void onCalcularTrapezioRepetido() {
        executarMetodoFuncao("TRAPEZIO");
    }

    @FXML
    private void onCalcularSimpsonRepetido() {
        executarMetodoFuncao("SIMPSON");
    }

    @FXML
    private void onCalcularTrapezioDiscreto() {
        executarMetodoDiscreto("TRAPEZIO");
    }

    @FXML
    private void onCalcularSimpsonDiscreto() {
        executarMetodoDiscreto("SIMPSON");
    }

    @FXML
    private void onLimpar() {
        txtFuncao.clear();
        txtA.clear();
        txtB.clear();
        txtM.clear();
        txtXDiscreto.clear();
        txtYDiscreto.clear();
        lblStatus.setText("");
        txtResultado.clear();
    }

    private void executarMetodoFuncao(String metodo) {
        try {
            AvaliadorFuncao f = new AvaliadorFuncao(txtFuncao.getText());
            double a = ConversorEntrada.lerDouble(txtA.getText(), "Limite inferior (a)");
            double b = ConversorEntrada.lerDouble(txtB.getText(), "Limite superior (b)");
            int m = ConversorEntrada.lerIntPositivo(txtM.getText(), "Subintervalos (m)");

            ResultadoCalculo resultado;
            if (metodo.equals("TRAPEZIO")) {
                resultado = TrapezioRepetido.calcular(f, a, b, m);
            } else {
                resultado = SimpsonRepetido.calcular(f, a, b, m);
            }

            exibirResultado(resultado);
        } catch (IllegalArgumentException e) {
            exibirResultado(ResultadoCalculo.erro(e.getMessage()));
        }
    }

    private void executarMetodoDiscreto(String metodo) {
        try {
            double[] x = ConversorEntrada.lerVetor(txtXDiscreto.getText(), "Valores de x");
            double[] y = ConversorEntrada.lerVetor(txtYDiscreto.getText(), "Valores de f(x)");

            ResultadoCalculo resultado;
            if (metodo.equals("TRAPEZIO")) {
                resultado = IntegracaoDiscreta.calcularTrapezio(x, y);
            } else {
                resultado = IntegracaoDiscreta.calcularSimpson(x, y);
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