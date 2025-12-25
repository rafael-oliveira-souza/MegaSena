package com.megasena.aposta.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface ApostaStrategy {

    List<Integer> criarAposta(Integer qtdNumeros, Map<Integer, Integer> resultadosBackup);

    boolean getRetry();

    default Map<Integer, Integer> criarBackup(Map<Integer, Integer> resultadosBackup) {
        HashMap<Integer, Integer> integerIntegerHashMap = new HashMap<>();
        if (Objects.nonNull(resultadosBackup)) {
            integerIntegerHashMap.putAll(resultadosBackup);
        }
        return integerIntegerHashMap;
    }

    default boolean isApostaValida(List<Integer> aposta, Integer qtdNumeros) {
        if (aposta.size() == qtdNumeros) {
            return true;
        }

        HashMap<Integer, Integer> map = new HashMap<>();
        aposta.forEach(num -> map.put(num, num));
        if (map.keySet().size() == qtdNumeros) {
            return true;
        }

        return false;
    }
}
