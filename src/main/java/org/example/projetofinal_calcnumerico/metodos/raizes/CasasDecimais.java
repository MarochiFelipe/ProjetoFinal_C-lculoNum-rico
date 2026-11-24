package org.example.projetofinal_calcnumerico.metodos.raizes;

public enum CasasDecimais {
    AUTOMATICO("Automático", 0),
    DUAS("2 casas", 2),
    QUATRO("4 casas", 4),
    SEIS("6 casas", 6),
    OITO("8 casas", 8);

    private final String rotulo;
    private final int quantidade;

    CasasDecimais(String rotulo, int quantidade) {
        this.rotulo = rotulo;
        this.quantidade = quantidade;
    }

    int quantidade() {
        return quantidade;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
