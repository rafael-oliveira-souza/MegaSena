package com.megasena.aposta.strategy;

import com.megasena.aposta.enums.ResultadosEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApostaMedia implements ApostaStrategy {

    private final ResultadosEnum resultadosEnum;

    public ApostaMedia(ResultadosEnum resultadosEnum) {
        this.resultadosEnum = resultadosEnum;
    }

    @Override
    public List<Integer> criarAposta(Integer qtdNumeros, Map<Integer, Integer> resultadosBackup) {
        List<Integer> aposta = new ArrayList<>();
        int media = qtdNumeros / 2;

        aposta.addAll(new ApostaMinima(resultadosEnum).criarAposta(media, resultadosBackup));
        aposta.addAll(new ApostaMaxima(resultadosEnum).criarAposta(qtdNumeros - media, resultadosBackup));

        return aposta;
    }
}
