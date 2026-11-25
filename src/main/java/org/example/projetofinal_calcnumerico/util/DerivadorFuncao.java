package org.example.projetofinal_calcnumerico.util;

import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;
import net.objecthunter.exp4j.shuntingyard.ShuntingYard;
import net.objecthunter.exp4j.tokenizer.FunctionToken;
import net.objecthunter.exp4j.tokenizer.NumberToken;
import net.objecthunter.exp4j.tokenizer.OperatorToken;
import net.objecthunter.exp4j.tokenizer.Token;
import net.objecthunter.exp4j.tokenizer.VariableToken;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EmptyStackException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class DerivadorFuncao {
    private static final Expressao ZERO = Expressao.numero(0);
    private static final Expressao UM = Expressao.numero(1);
    private static final Expressao DOIS = Expressao.numero(2);

    private DerivadorFuncao() {}

    public static String derivar(String textoFuncao) {
        String texto = AvaliadorFuncao.normalizar(textoFuncao);
        Token[] tokens;
        try {
            tokens = ShuntingYard.convertToRPN(texto, AvaliadorFuncao.FUNCOES, Map.of(), Set.of("x", "pi", "π", "e", "φ"), true);
        } catch (IllegalArgumentException | EmptyStackException erro) {
            throw expressaoInvalida();
        }

        Deque<Termo> termos = new ArrayDeque<>();
        try {
            for (Token token : tokens) {
                switch (token) {
                    case NumberToken numero ->
                            termos.push(new Termo(Expressao.numero(numero.getValue()), ZERO));
                    case VariableToken variavel -> {
                        String nome = variavel.getName();
                        termos.push(new Termo(Expressao.atomo(nome), nome.equals("x") ? UM : ZERO));
                    }
                    case OperatorToken operador ->
                            termos.push(aplicarOperador(operador.getOperator(), termos));
                    case FunctionToken funcao -> {
                        Function operacao = funcao.getFunction();
                        Termo ultimo = termos.pop();
                        if (operacao.getName().equals("pow")) {
                            termos.push(potencia(termos.pop(), ultimo));
                        } else {
                            termos.push(aplicarFuncao(operacao, ultimo));
                        }
                    }
                    default -> throw expressaoInvalida();
                }
            }
        } catch (NoSuchElementException erro) {
            throw expressaoInvalida();
        }

        if (termos.size() != 1) {
            throw expressaoInvalida();
        }
        return termos.pop().derivada().texto();
    }

    private static Termo aplicarOperador(Operator operador, Deque<Termo> termos) {
        String simbolo = operador.getSymbol();
        Termo direita = termos.pop();
        if (operador.getNumOperands() == 1) {
            return simbolo.equals("-")
                    ? new Termo(negar(direita.valor()), negar(direita.derivada()))
                    : direita;
        }

        Termo esquerda = termos.pop();
        Expressao u = esquerda.valor();
        Expressao v = direita.valor();
        Expressao du = esquerda.derivada();
        Expressao dv = direita.derivada();

        return switch (simbolo) {
            case "+" -> new Termo(operar("+", u, v), operar("+", du, dv));
            case "-" -> new Termo(operar("-", u, v), operar("-", du, dv));
            case "*" -> new Termo(operar("*", u, v), operar("+", operar("*", du, v), operar("*", u, dv)));
            case "/" -> {
                Expressao derivada = dv.eh(0)
                        ? operar("/", du, v)
                        : operar("/", operar("-", operar("*", du, v), operar("*", u, dv)),
                        operar("^", v, DOIS));
                yield new Termo(operar("/", u, v), derivada);
            }
            case "^" -> potencia(esquerda, direita);
            case "%" -> {
                if (!du.eh(0) || !dv.eh(0)) {
                    throw new IllegalArgumentException("O operador % com termos que dependem de x não é aceito no Newton-Raphson.");
                }
                yield new Termo(operar("%", u, v), ZERO);
            }
            default -> throw expressaoInvalida();
        };
    }

    private static Termo potencia(Termo base, Termo expoente) {
        Expressao u = base.valor();
        Expressao v = expoente.valor();
        Expressao du = base.derivada();
        Expressao dv = expoente.derivada();
        Expressao valor = operar("^", u, v);

        if (dv.eh(0)) {
            Expressao fator = operar("*", v, operar("^", u, operar("-", v, UM)));
            return new Termo(valor, operar("*", fator, du));
        }

        Expressao fator = operar("+", operar("*", dv, chamada("ln", u)), operar("*", v, operar("/", du, u)));
        return new Termo(valor, operar("*", valor, fator));
    }

    private static Termo aplicarFuncao(Function operacao, Termo argumento) {
        String nome = operacao.getName();
        Expressao u = argumento.valor();
        Expressao du = argumento.derivada();
        Expressao funcao = chamada(nome, u);

        if (du.eh(0)) {
            if (u.numero() != null) {
                double valor = operacao.apply(u.numero());
                if (Double.isFinite(valor)) {
                    return new Termo(Expressao.numero(valor), ZERO);
                }
            }
            return new Termo(funcao, ZERO);
        }

        Expressao derivadaExterna = switch (nome) {
            case "sin", "sen" -> chamada("cos", u);
            case "cos" -> negar(chamada("sin", u));
            case "tan", "tg" -> operar("/", UM, operar("^", chamada("cos", u), DOIS));
            case "cot" -> negar(operar("/", UM, operar("^", chamada("sin", u), DOIS)));
            case "asin" -> operar("/", UM, chamada("sqrt", operar("-", UM, operar("^", u, DOIS))));
            case "acos" -> negar(operar("/", UM, chamada("sqrt", operar("-", UM, operar("^", u, DOIS)))));
            case "atan" -> operar("/", UM, operar("+", UM, operar("^", u, DOIS)));
            case "sinh" -> chamada("cosh", u);
            case "cosh" -> chamada("sinh", u);
            case "tanh" -> operar("/", UM, operar("^", chamada("cosh", u), DOIS));
            case "exp", "expm1" -> chamada("exp", u);
            case "ln" -> operar("/", UM, u);
            case "log", "log10" -> operar("/", UM, operar("*", u, chamada("ln", Expressao.numero(10))));
            case "log2" -> operar("/", UM, operar("*", u, chamada("ln", DOIS)));
            case "log1p" -> operar("/", UM, operar("+", UM, u));
            case "sqrt" -> operar("/", UM, operar("*", DOIS, funcao));
            case "cbrt" -> operar("/", UM, operar("*", Expressao.numero(3), operar("^", funcao, DOIS)));
            case "abs" -> operar("/", u, funcao);
            default -> throw new IllegalArgumentException(
                    "A função " + nome + "(...) com argumento que depende de x não é aceita no Newton-Raphson."
            );
        };

        return new Termo(funcao, operar("*", derivadaExterna, du));
    }

    private static Expressao chamada(String nome, Expressao argumento) {
        return Expressao.atomo(nome + "(" + argumento.texto() + ")");
    }

    private static Expressao negar(Expressao valor) {
        return valor.numero() == null
                ? Expressao.composta("-" + valor.agrupada())
                : Expressao.numero(-valor.numero());
    }

    private static Expressao operar(String operador, Expressao a, Expressao b) {
        if (a.numero() != null && b.numero() != null) {
            double x = a.numero();
            double y = b.numero();
            double resultado = switch (operador) {
                case "+" -> x + y;
                case "-" -> x - y;
                case "*" -> x * y;
                case "/" -> x / y;
                case "%" -> x % y;
                default -> Math.pow(x, y);
            };
            if (Double.isFinite(resultado)) {
                return Expressao.numero(resultado);
            }
        }

        return switch (operador) {
            case "+" -> a.eh(0) ? b : b.eh(0) ? a : combinar(operador, a, b);
            case "-" -> b.eh(0) ? a : a.eh(0) ? negar(b) : combinar(operador, a, b);
            case "*" -> a.eh(0) || b.eh(0) ? ZERO : a.eh(1) ? b : b.eh(1) ? a : combinar(operador, a, b);
            case "/" -> b.eh(1) ? a : a.eh(0) && !b.eh(0) ? ZERO : combinar(operador, a, b);
            case "^" -> b.eh(0) ? UM : b.eh(1) ? a : combinar(operador, a, b);
            default -> combinar(operador, a, b);
        };
    }

    private static Expressao combinar(String operador, Expressao a, Expressao b) {
        return Expressao.composta(a.agrupada() + operador + b.agrupada());
    }

    private static IllegalArgumentException expressaoInvalida() {
        return new IllegalArgumentException("Confira a expressão de f(x) para gerar a derivada.");
    }

    private record Termo(Expressao valor, Expressao derivada) {}

    private record Expressao(String texto, boolean composta, Double numero) {
        static Expressao atomo(String texto) {
            return new Expressao(texto, false, null);
        }

        static Expressao composta(String texto) {
            return new Expressao(texto, true, null);
        }

        static Expressao numero(double valor) {
            String texto = Double.toString(valor == 0 ? 0 : valor);
            if (texto.endsWith(".0")) {
                texto = texto.substring(0, texto.length() - 2);
            }
            return new Expressao(texto, valor < 0, valor);
        }

        boolean eh(double valor) {
            return numero != null && numero == valor;
        }

        String agrupada() {
            return composta ? "(" + texto + ")" : texto;
        }
    }
}