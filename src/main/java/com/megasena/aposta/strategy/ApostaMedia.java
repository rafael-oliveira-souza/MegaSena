package com.megasena.aposta.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApostaMedia implements ApostaStrategy {

    @Override
    public List<Integer> criarAposta(Integer qtdNumeros, Map<Integer, Integer> resultadosBackup) {
        List<Integer> aposta = new ArrayList<>();
        int media = qtdNumeros / 2;

        aposta.addAll(new ApostaMinima().criarAposta(media, resultadosBackup));
        aposta.addAll(new ApostaMaxima().criarAposta(qtdNumeros - media, resultadosBackup));

        return aposta;
    }
}
