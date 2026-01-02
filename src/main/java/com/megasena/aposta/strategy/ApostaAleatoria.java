package com.megasena.aposta.strategy;

import com.megasena.aposta.ApostaFactory;
import com.megasena.aposta.enums.ResultadosEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApostaAleatoria implements ApostaStrategy {

    private final ResultadosEnum resultadosEnum;

    public ApostaAleatoria(ResultadosEnum resultadosEnum) {
        this.resultadosEnum = resultadosEnum;
    }

    @Override
    public List<Integer> criarAposta(Integer qtdNumeros, Map<Integer, Integer> resultadosBackup) {
        List<Integer> aposta = new ArrayList<>();
        while (aposta.size() < qtdNumeros) {
            int randomInt = ApostaFactory.gerarNumeroAleatorio(
                    1, resultadosEnum.getQtdNumeros()
            );
            int limitesExtremidades = (resultadosEnum.getQtdNumeros() / 2) + 1;
            int excedeLimiteParOuImpar = excedeLimiteParOuImpar(aposta, qtdNumeros);
            int excedeLimiteExtremidades = excedeLimiteExtremidades(aposta, qtdNumeros, limitesExtremidades);
            int excedeLimiteSequenciais = excedeLimiteSequenciais(aposta, qtdNumeros);

            if ((excedeLimiteParOuImpar == 0
                    || (excedeLimiteParOuImpar == 2 && randomInt % 2 != 0)
                    || (excedeLimiteParOuImpar == 1 && randomInt % 2 == 0))
                    && (excedeLimiteSequenciais == 0)
                    && (excedeLimiteExtremidades == 0
                    || (excedeLimiteExtremidades == 2 && randomInt <= limitesExtremidades)
                    || (excedeLimiteExtremidades == 1 && randomInt >= limitesExtremidades))
            ) {
                aposta.add(randomInt);
            }
        }

        return aposta;
    }
}
