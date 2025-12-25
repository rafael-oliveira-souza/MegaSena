package com.megasena.aposta;

import com.megasena.aposta.enums.FrequenciaRepeticaoEnum;
import com.megasena.aposta.enums.ResultadosEnum;
import com.megasena.aposta.service.ApostaService;
import com.megasena.aposta.utils.ApostaUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class CriaApostaTests {

    public static final int QTD_NUMEROS = 15;
    public static final int QTD_PARTICIPANTES = 2;
    public static final ResultadosEnum SORTEIO_PATH = ResultadosEnum.LOTO_FACIL;
    public static final int QTD_APOSTAS = 20;
    public static final double VALOR_TOTAL = 500;
    public static final double VALOR_PREMIO = 1000000000;

    List<List<Integer>> multiplasApostas = new ArrayList<>();

    @Test
    void contextLoads() {

        ApostaService apostaService = new ApostaService();
        log.info("Valor Total={}", multiplasApostas.size() * SORTEIO_PATH.getValores().get(QTD_NUMEROS));
        log.info("\n\n##########################################################################################################################################\n");
        multiplasApostas.addAll(apostaService.gerarApostasERelatorio(
                SORTEIO_PATH,
                QTD_NUMEROS,
                LocalDate.of(2001, 1, 1),
                LocalDate.of(2025, 12, 31),
                QTD_APOSTAS,
                QTD_PARTICIPANTES,
                VALOR_PREMIO,
                FrequenciaRepeticaoEnum.MAX,
                FrequenciaRepeticaoEnum.MID,
                FrequenciaRepeticaoEnum.MIN));
    }

}
