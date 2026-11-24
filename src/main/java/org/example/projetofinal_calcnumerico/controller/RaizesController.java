package org.example.projetofinal_calcnumerico.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.projetofinal_calcnumerico.metodos.raizes.Bisseccao;
import org.example.projetofinal_calcnumerico.metodos.raizes.CasasDecimais;
import org.example.projetofinal_calcnumerico.metodos.raizes.Newton;
import org.example.projetofinal_calcnumerico.metodos.raizes.RegulaFalsi;
import org.example.projetofinal_calcnumerico.metodos.raizes.Secante;
import org.example.projetofinal_calcnumerico.model.ResultadoCalculo;
import org.example.projetofinal_calcnumerico.util.AvaliadorFuncao;
import org.example.projetofinal_calcnumerico.util.ConversorEntrada;

public class RaizesController {
    @FXML private TextField campoFuncao;
    @FXML private TextField campoDerivada;
    @FXML private TextField campoA;
    @FXML private TextField campoB;
    @FXML private TextField campoTolerancia;
    @FXML private TextField campoToleranciaFuncao;
    @FXML private TextField campoMaxIteracoes;
    @FXML private TextArea areaResultado;

    private static final String BISSECCAO = "Bissecção";
    private static final String FALSA_POSICAO = "Falsa Posição";
    private static final String SECANTE = "Secante";
    private static final String NEWTON = "Newton-Raphson";

    @FXML private ComboBox<String> seletorMetodo;
    @FXML private ComboBox<CasasDecimais> seletorCasasDecimais;
    @FXML private Label rotuloA;
    @FXML private Label rotuloB;
    @FXML private Label rotuloTolerancia;
    @FXML private VBox grupoB;
    @FXML private VBox grupoDerivada;
    @FXML private VBox grupoToleranciaFuncao;
    @FXML private Button botaoCalcular;

    @FXML
    private void initialize() {
        grupoB.managedProperty().bind(grupoB.visibleProperty());
        grupoDerivada.managedProperty().bind(grupoDerivada.visibleProperty());
        grupoToleranciaFuncao.managedProperty().bind(grupoToleranciaFuncao.visibleProperty());

        seletorMetodo.getItems().setAll(BISSECCAO, FALSA_POSICAO, SECANTE, NEWTON);
        seletorMetodo.valueProperty().addListener((observavel, anterior, novo) -> atualizarCampos(novo));
        seletorMetodo.getSelectionModel().selectFirst();

        seletorCasasDecimais.getItems().setAll(CasasDecimais.values());
        seletorCasasDecimais.getSelectionModel().select(CasasDecimais.AUTOMATICO);
    }

    private void atualizarCampos(String metodo) {
        boolean newton = NEWTON.equals(metodo);
        boolean secante = SECANTE.equals(metodo);
        boolean falsaPosicao = FALSA_POSICAO.equals(metodo);
        boolean usaIntervalo = !newton && !secante;

        rotuloA.setText(usaIntervalo ? "Limite inferior (a)" : "Aproximação inicial (x₀)");
        rotuloB.setText(secante ? "Segunda aproximação (x₁)" : "Limite superior (b)");
        rotuloTolerancia.setText(usaIntervalo ? "Tolerância do intervalo" : "Tolerância");

        grupoB.setVisible(!newton);
        grupoDerivada.setVisible(newton);
        grupoToleranciaFuncao.setVisible(falsaPosicao);

        boolean disponivel = BISSECCAO.equals(metodo) || falsaPosicao || secante || newton;
        botaoCalcular.setDisable(!disponivel);
        mostrarResultado(disponivel ? "" : "Método em implementação.");
    }

    private void mostrarResultado(ResultadoCalculo resultado) {
        String texto = resultado.getResultadoFinal();
        if (!resultado.isSucesso()) {
            texto = "Erro: " + texto;
        } else if (resultado.getDetalhes() != null && !resultado.getDetalhes().isBlank()) {
            texto += "\n\n" + resultado.getDetalhes();
        }
        mostrarResultado(texto);
    }

    private void mostrarResultado(String texto) {
        areaResultado.setText(texto);
        areaResultado.positionCaret(0);
    }

    @FXML
    private void calcular() {
        try {
            String metodo = seletorMetodo.getValue();
            CasasDecimais casasDecimais = seletorCasasDecimais.getValue();
            boolean newton = NEWTON.equals(metodo);
            boolean secante = SECANTE.equals(metodo);
            double a = ConversorEntrada.lerDouble(campoA.getText(), newton || secante ? "Aproximação inicial (x₀)" : "Limite inferior");
            double tolerancia = ConversorEntrada.lerDoublePositivo(campoTolerancia.getText(), "Tolerância");
            int maxIteracoes = ConversorEntrada.lerIntPositivo(campoMaxIteracoes.getText(), "Máximo de iterações");

            AvaliadorFuncao funcao = new AvaliadorFuncao(campoFuncao.getText());
            ResultadoCalculo resultado;

            if (newton) {
                AvaliadorFuncao derivada = new AvaliadorFuncao(campoDerivada.getText());
                resultado = Newton.calcular(funcao, derivada, a, tolerancia, maxIteracoes, casasDecimais);
            } else {
                double b = ConversorEntrada.lerDouble(campoB.getText(), secante ? "Segunda aproximação (x₁)" : "Limite superior");

                if (FALSA_POSICAO.equals(metodo)) {
                    double toleranciaFuncao = ConversorEntrada.lerDoublePositivo(campoToleranciaFuncao.getText(), "Tolerância para |f(x)|");
                    resultado = RegulaFalsi.calcular(funcao, a, b, tolerancia, toleranciaFuncao, maxIteracoes, casasDecimais);
                } else if (secante) {
                    resultado = Secante.calcular(funcao, a, b, tolerancia, maxIteracoes, casasDecimais);
                } else {
                    resultado = Bisseccao.calcular(funcao, a, b, tolerancia, maxIteracoes, casasDecimais);
                }
            }

            mostrarResultado(resultado);
        } catch (IllegalArgumentException erro) {
            mostrarResultado(ResultadoCalculo.erro(erro.getMessage()));
        }
    }
}
