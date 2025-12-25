package com.megasena.aposta.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ApostaMaxima implements ApostaStrategy {
    private boolean retry;

    public ApostaMaxima(boolean retry) {
        this.retry = retry;
    }

    @Override
    public List<Integer> criarAposta(Integer qtdNumeros, Map<Integer, Integer> resultadosBackup) {
        List<Integer> aposta = new ArrayList<>();
        Map<Integer, Integer> resultados = criarBackup(resultadosBackup);
        while (aposta.size() < qtdNumeros && !resultados.isEmpty()) {
            Map.Entry<Integer, Integer> map = resultados
                    .entrySet()
                    .stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .orElse(null);

            if (this.retry) {
                resultados.remove(map.getKey());
                this.retry = false;
            } else {
                resultados.remove(map.getKey());
                aposta.add(map.getKey());
            }

        }

        return aposta;
    }

    @Override
    public boolean getRetry() {
        return this.retry;
    }
}
