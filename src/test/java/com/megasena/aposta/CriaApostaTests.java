package com.megasena.aposta;

import com.megasena.aposta.enums.FrequenciaRepeticaoEnum;
import com.megasena.aposta.enums.ResultadosEnum;
import com.megasena.aposta.service.ApostaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
class CriaApostaTests {

    public static final int QTD_NUMEROS = 15;
    public static final int DIFERENCIAR_QTD_NUMEROS_ULTIMO_RESULTADO = 5;
    public static final int QTD_PARTICIPANTES = 1;
    public static final ResultadosEnum SORTEIO_PATH = ResultadosEnum.LOTO_FACIL;
    public static final int QTD_APOSTAS = 10;
    public static final BigDecimal VALOR_TOTAL = BigDecimal.valueOf(500);
    public static final BigDecimal VALOR_PREMIO = BigDecimal.valueOf(1000000);

    List<List<Integer>> multiplasApostas = new ArrayList<>();
    Set<Integer> ignoreNumbers = Set.of();

    @Test
    void contextLoads() {

        ApostaService apostaService = new ApostaService(ignoreNumbers);
        log.info("\n\n######################################### INICIANDO ######################################################################\n");
        multiplasApostas.addAll(apostaService.gerarApostasERelatorio(
                SORTEIO_PATH,
                QTD_NUMEROS,
                DIFERENCIAR_QTD_NUMEROS_ULTIMO_RESULTADO,
                LocalDate.of(2001, 1, 1),
                LocalDate.of(2025, 12, 31),
                QTD_APOSTAS,
                QTD_PARTICIPANTES,
                VALOR_PREMIO,
                FrequenciaRepeticaoEnum.MAX,
                FrequenciaRepeticaoEnum.MID,
                FrequenciaRepeticaoEnum.MIN));

        log.info("Valor Total={}", multiplasApostas.size() * SORTEIO_PATH.getValores().get(QTD_NUMEROS));
        log.info("\n\n##########################################################################################################################################\n");
    }
}
