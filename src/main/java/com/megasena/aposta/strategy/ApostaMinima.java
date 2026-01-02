package com.megasena.aposta.strategy;

import com.megasena.aposta.enums.ResultadosEnum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ApostaMinima implements ApostaStrategy {

    private final ResultadosEnum resultadosEnum;

    public ApostaMinima(ResultadosEnum resultadosEnum) {
        this.resultadosEnum = resultadosEnum;
    }

    @Override
    public List<Integer> criarAposta(Integer qtdNumeros, Map<Integer, Integer> resultadosBackup) {
        List<Integer> aposta = new ArrayList<>();
        Map<Integer, Integer> resultados = criarBackup(resultadosBackup);
        int limitesExtremidades = (resultadosEnum.getQtdNumeros() / 2) + 1;
        while (aposta.size() < qtdNumeros && !resultados.isEmpty()) {
            int excedeLimiteParOuImpar = excedeLimiteParOuImpar(aposta, qtdNumeros);
            int excedeLimiteExtremidades = excedeLimiteExtremidades(aposta, qtdNumeros, limitesExtremidades);
            int excedeLimiteSequenciais = excedeLimiteSequenciais(aposta, qtdNumeros);

            Map.Entry<Integer, Integer> map = resultados
                    .entrySet()
                    .stream()
                    .filter(r -> (excedeLimiteParOuImpar == 0
                            || (excedeLimiteParOuImpar == 2 && r.getKey() % 2 != 0)
                            || (excedeLimiteParOuImpar == 1 && r.getKey() % 2 == 0))
                            && (excedeLimiteSequenciais == 0)
                            && (excedeLimiteExtremidades == 0
                            || (excedeLimiteExtremidades == 2 && r.getKey() <= limitesExtremidades)
                            || (excedeLimiteExtremidades == 1 && r.getKey() >= limitesExtremidades))
                    )
                    .min(Comparator.comparingInt(Map.Entry::getValue))
                    .orElse(null);

            if (map != null) {
                resultados.remove(map.getKey());
                aposta.add(map.getKey());
            } else {
                return new ArrayList<>();
            }
        }

        return aposta;
    }
}
