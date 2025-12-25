package com.megasena.aposta.strategy;

import com.megasena.aposta.ApostaFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApostaAleatoria implements ApostaStrategy {

    @Override
    public List<Integer> criarAposta(Integer qtdNumeros, Map<Integer, Integer> resultadosBackup) {
        List<Integer> aposta = new ArrayList<>();
        while (aposta.size() < qtdNumeros) {
            int randomInt = ApostaFactory.gerarNumeroAleatorio(1, qtdNumeros);
            if (!aposta.contains(randomInt)) {
                aposta.add(randomInt);
            }
        }

        return aposta;
    }
}
