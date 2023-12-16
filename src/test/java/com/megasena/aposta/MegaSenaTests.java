package com.megasena.aposta;

import com.megasena.aposta.dtos.NumeroRecorrenteDto;
import com.megasena.aposta.dtos.SorteioDto;
import com.megasena.aposta.enums.FrequenciaRepeticaoEnum;
import com.megasena.aposta.utils.ApostaUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
class MegaSenaTests {

    public static final int QTD_NUMEROS = 6;
    public static final String SORTEIO_PATH = "src/main/resources/resultados/mega_sena_ate_concurso_2666.json";
    public static final int QTD_APOSTAS = 10;
    public static  final Map<Integer, Double> VALOR_MEGA = Map.of(
            6, 5D,
            7, 35D,
            8, 140D,
            9, 420D,
            10, 1050D,
            11, 2350D,
            12, 4620D,
            13, 8580D,
            14, 15015D,
            15, 25025D
    );
    public static final double VALOR_TOTAL = 500;

    @Test
    void contextLoads() {
        List<SorteioDto> sorteioDtos = ApostaUtils
                .buscarResultados(SORTEIO_PATH);
        LocalDate dataInicio = sorteioDtos.get(0).getData();
        LocalDate dataFim = sorteioDtos.get(sorteioDtos.size() - 1).getData();

        List<SorteioDto> sorteiosPorData = ApostaUtils
                .buscarResultadosPorData(SORTEIO_PATH, dataInicio, dataFim);

        List<NumeroRecorrenteDto> numeroRecorrenteDtos = ApostaUtils
                .buscarNumerosRecorrentes(SORTEIO_PATH);

        List<NumeroRecorrenteDto> numeroRecorrenteDtosPorData = ApostaUtils
                .buscarNumerosRecorrentesPorData(SORTEIO_PATH, dataInicio, dataFim);

        NumeroRecorrenteDto numeroMinimoRepeticoes = ApostaUtils.buscarNumeroMinimoRepeticoes(SORTEIO_PATH);
        NumeroRecorrenteDto numeroMinimoRepeticoesPorData = ApostaUtils
                .buscarNumeroMinimoRepeticoesPorData(SORTEIO_PATH, dataInicio, dataFim);
        NumeroRecorrenteDto numeroMaximoRepeticoes = ApostaUtils.buscarNumeroMaximoRepeticoes(SORTEIO_PATH);
        NumeroRecorrenteDto numeroMaximoRepeticoesPorData = ApostaUtils
                .buscarNumeroMaximoRepeticoesPorData(SORTEIO_PATH, dataInicio, dataFim);
        NumeroRecorrenteDto numeroRecorrenteDto = ApostaUtils.buscarRecorrencia(SORTEIO_PATH, 15);

        List<Integer> apostaMax = ApostaUtils.criarAposta(SORTEIO_PATH, QTD_NUMEROS, FrequenciaRepeticaoEnum.MAX, dataInicio, dataFim);
        List<Integer> apostaMin = ApostaUtils.criarAposta(SORTEIO_PATH, QTD_NUMEROS, FrequenciaRepeticaoEnum.MIN, dataInicio, dataFim);
        List<Integer> apostaRandom = ApostaUtils.criarAposta(SORTEIO_PATH, QTD_NUMEROS, FrequenciaRepeticaoEnum.RANDOM, dataInicio, dataFim);
        List<Integer> apostaMid = ApostaUtils.criarAposta(SORTEIO_PATH, QTD_NUMEROS, FrequenciaRepeticaoEnum.MID, dataInicio, dataFim);
        List<List<Integer>> multiplasAPostas = ApostaUtils.criarAposta(QTD_APOSTAS, QTD_NUMEROS);
//        log.info("Aposta min={}", apostaMin);
//        log.info("Aposta max={}", apostaMax);
//        log.info("Aposta mid={}", apostaMid);
//        log.info("Aposta random={}", apostaRandom);
        log.info("Apostas multiplas={}", multiplasAPostas);
        log.info("Apostas Invalidas={}", multiplasAPostas.stream().filter(aposta -> aposta.size() != QTD_NUMEROS).count());
        log.info("Valor Total={}", multiplasAPostas.size() * VALOR_MEGA.get(QTD_NUMEROS));
        log.info("\n\n##########################################################################################################################################\n");

        VALOR_MEGA.forEach((qtdNumeros, valor) -> {
            double valorTotal = VALOR_TOTAL;
            int qtdJogos = 0;

            while (valorTotal - valor >= 0D) {
                valorTotal -= valor;
                qtdJogos++;
            }
            log.info("Valor={}, Quantidade de apostas={}, Quantidade de Numeros={}", VALOR_TOTAL, qtdJogos, qtdNumeros);
        });

        log.info("\n\n##########################################################################################################################################\n");
        ApostaUtils.gerarRelatorio(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31));


//        for(int i = 1996; i<=2023; i++){
//            log.info("Ano={}, Repeticoes={}", i, ApostaUtils.calcularRepeticoesAno(i));
//        }

        Assertions.assertFalse(multiplasAPostas.isEmpty());
        Assertions.assertFalse(apostaMax.isEmpty());
        Assertions.assertFalse(apostaMin.isEmpty());
        Assertions.assertFalse(apostaMid.isEmpty());
        Assertions.assertFalse(apostaRandom.isEmpty());
        Assertions.assertFalse(sorteioDtos.isEmpty());
        Assertions.assertFalse(sorteiosPorData.isEmpty());
        Assertions.assertFalse(numeroRecorrenteDtos.isEmpty());
        Assertions.assertFalse(numeroRecorrenteDtosPorData.isEmpty());
        Assertions.assertTrue(numeroMinimoRepeticoesPorData.getRepeticoes() > 0);
        Assertions.assertTrue(numeroMaximoRepeticoesPorData.getRepeticoes() > 0);
        Assertions.assertTrue(numeroMinimoRepeticoes.getRepeticoes() > 0);
        Assertions.assertTrue(numeroMaximoRepeticoes.getRepeticoes() > 0);
        Assertions.assertTrue(numeroRecorrenteDto.getRepeticoes() > 0);
        Assertions.assertNotEquals(sorteioDtos.size(), sorteiosPorData.size());
    }

}
