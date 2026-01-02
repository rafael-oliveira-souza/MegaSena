package com.megasena.aposta.strategy;

import com.megasena.aposta.enums.ResultadosEnum;

import java.util.*;

public interface ApostaStrategy {

    List<Integer> criarAposta(Integer qtdNumeros, Map<Integer, Integer> resultadosBackup);

    default Map<Integer, Integer> criarBackup(Map<Integer, Integer> resultadosBackup) {
        HashMap<Integer, Integer> integerIntegerHashMap = new HashMap<>();
        if (Objects.nonNull(resultadosBackup)) {
            integerIntegerHashMap.putAll(resultadosBackup);
        }
        return integerIntegerHashMap;
    }

    default boolean isPar(int num) {
        return num % 2 == 0;
    }

    default int excedeLimiteExtremidades(List<Integer> numeros, int qtdNumeros, int limiteExtremidades) {
        int sup = 0;
        int inf = 0;
        int limite = (qtdNumeros / 2) + 1;
        for (Integer num : numeros) {
            if (num == null) continue;

            if (num > limiteExtremidades) {
                sup++;
                if (sup >= limite) {
                    return 2;
                }
            } else {
                inf++;
                if (inf >= limite) {
                    return 1;
                }
            }
        }

        return 0;
    }

    default int excedeLimiteParOuImpar(List<Integer> numeros, int qtdNumeros) {
        int pares = 0;
        int impares = 0;
        int limite = (qtdNumeros / 2) + 1;
        for (Integer num : numeros) {
            if (num == null) continue;

            if (isPar(num)) {
                pares++;
                if (pares >= limite) {
                    return 2;
                }
            } else {
                impares++;
                if (impares >= limite) {
                    return 1;
                }
            }
        }

        return 0;
    }

    default boolean isApostaValida(List<Integer> aposta, Integer qtdNumeros, ResultadosEnum resultadosEnum) {
        if (aposta.size() != qtdNumeros) {
            return false;
        }

        HashMap<Integer, Integer> map = new HashMap<>();
        aposta.forEach(num -> map.put(num, num));
        if (map.keySet().size() != qtdNumeros) {
            return false;
        }

        return true;
    }

    default int excedeLimiteSequenciais(List<Integer> numeros, int limite) {
        if (numeros == null || numeros.size() < limite) {
            return 0;
        }

        Set<Integer> set = new HashSet<>(numeros);
        for (Integer num : set) {
            if (!set.contains(num - 1)) { // início da sequência
                int contador = 1;
                int atual = num;

                while (set.contains(atual + 1)) {
                    contador++;
                    atual++;

                    if (contador >= limite) {
                        return atual;
                    }
                }
            }
        }
        return 0;
    }
}
