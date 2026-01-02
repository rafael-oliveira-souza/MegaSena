package com.megasena.aposta;

import com.megasena.aposta.enums.FrequenciaRepeticaoEnum;
import com.megasena.aposta.enums.ResultadosEnum;
import com.megasena.aposta.strategy.*;

import java.util.Arrays;

public class ApostaFactory {
    public static ApostaStrategy criarTipoAposta(ResultadosEnum resultadosEnum, FrequenciaRepeticaoEnum repeticaoEnum) {
        if (FrequenciaRepeticaoEnum.MAX.equals(repeticaoEnum)) {
            return new ApostaMaxima(resultadosEnum);
        } else if (FrequenciaRepeticaoEnum.MIN.equals(repeticaoEnum)) {
            return new ApostaMinima(resultadosEnum);
        } else if (FrequenciaRepeticaoEnum.MID.equals(repeticaoEnum)) {
            return new ApostaMedia(resultadosEnum);
        } else {
            return new ApostaAleatoria(resultadosEnum);
        }
    }

    public static FrequenciaRepeticaoEnum gerarFrequenciaAleatoria(FrequenciaRepeticaoEnum... frequencias) {
        if (frequencias == null || frequencias.length == 0) {
            frequencias = FrequenciaRepeticaoEnum.values();
        }

        Integer min = Arrays.stream(frequencias)
                .map(Enum::ordinal)
                .min(Integer::compare)
                .orElse(FrequenciaRepeticaoEnum.MAX.ordinal());

        Integer max = Arrays.stream(frequencias)
                .map(Enum::ordinal)
                .max(Integer::compare)
                .orElse(FrequenciaRepeticaoEnum.RANDOM.ordinal());

        Integer numeroAleatorio = gerarNumeroAleatorio(min, max);
        return FrequenciaRepeticaoEnum.getByOrdinal(numeroAleatorio);
    }

    public static Integer gerarNumeroAleatorio(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }
}
