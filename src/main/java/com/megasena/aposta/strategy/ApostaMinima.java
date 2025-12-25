package com.megasena.aposta.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ApostaMinima implements ApostaStrategy {

    @Override
    public List<Integer> criarAposta(Integer qtdNumeros, Map<Integer, Integer> resultadosBackup) {
        List<Integer> aposta = new ArrayList<>();
        Map<Integer, Integer> resultados = criarBackup(resultadosBackup);
        while (aposta.size() < qtdNumeros && !resultados.isEmpty()) {
            Map.Entry<Integer, Integer> map = resultados
                    .entrySet()
                    .stream()
                    .min(Comparator.comparingInt(Map.Entry::getValue))
                    .orElse(null);

            resultados.remove(map.getKey());
            aposta.add(map.getKey());
        }

        return aposta;
    }
}
