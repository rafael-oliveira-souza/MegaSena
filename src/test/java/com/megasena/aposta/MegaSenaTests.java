package com.megasena.aposta;

import com.megasena.aposta.dtos.NumeroRecorrenteDto;
import com.megasena.aposta.dtos.SorteioDto;
import com.megasena.aposta.enums.FrequenciaRepeticaoEnum;
import com.megasena.aposta.enums.ResultadosEnum;
import com.megasena.aposta.utils.ApostaUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
class MegaSenaTests {

    public static final int QTD_NUMEROS = 6;
    public static final int QTD_PARTICIPANTES = 5;
    public static final ResultadosEnum SORTEIO_PATH = ResultadosEnum.MEGA_SENA;
    public static final int QTD_APOSTAS = 20;
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

        List<Integer> apostaMax = ApostaUtils.criarAposta(SORTEIO_PATH, 7, FrequenciaRepeticaoEnum.MAX,
                LocalDate.of(2023, 03, 01),
                LocalDate.of(2024, 11, 01));

        List<Integer> apostaMin = ApostaUtils.criarAposta(SORTEIO_PATH, 7, FrequenciaRepeticaoEnum.MIN,
                LocalDate.of(2023, 03, 01),
                LocalDate.of(2024, 11, 01));


        List<Integer> apostaRandom = ApostaUtils.criarAposta(SORTEIO_PATH, 6, FrequenciaRepeticaoEnum.RANDOM,
                LocalDate.of(2023, 03, 01),
                LocalDate.of(2024, 11, 01));

        List<Integer> apostaMid = ApostaUtils.criarAposta(SORTEIO_PATH, 6, FrequenciaRepeticaoEnum.MID,
                LocalDate.of(2023, 03, 01),
                LocalDate.of(2024, 11, 01));

        List<Integer> apostaMin2 = ApostaUtils.criarAposta(SORTEIO_PATH, 6, FrequenciaRepeticaoEnum.MIN,
                LocalDate.of(2023, 02, 01),
                LocalDate.of(2024, 03, 01));
        List<List<Integer>> multiplasAPostas = ApostaUtils.criarAposta(SORTEIO_PATH, QTD_APOSTAS, QTD_NUMEROS, QTD_PARTICIPANTES);
        log.info("Aposta min={}", apostaMin);
        log.info("Aposta min2={}", apostaMin2);
        log.info("Aposta max={}", apostaMax);
        log.info("Aposta mid={}", apostaMid);
        log.info("Aposta random={}", apostaRandom);
        log.info("Apostas multiplas={}", multiplasAPostas);
        log.info("Apostas Invalidas={}", multiplasAPostas.stream().filter(aposta -> aposta.size() != QTD_NUMEROS).count());
        log.info("Valor Total={}", multiplasAPostas.size() * SORTEIO_PATH.getValores().get(QTD_NUMEROS));
        log.info("\n\n##########################################################################################################################################\n");

        SORTEIO_PATH.getValores().forEach((qtdNumeros, valor) -> {
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
                SORTEIO_PATH,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2024, 12, 31));


//        for(int i = 1996; i<=2023; i++){
//            log.info("Ano={}, Repeticoes={}", i, ApostaUtils.calcularRepeticoesAno(i));
//        }

        List<Integer> aposta = ApostaUtils.validarSeApostaPossuiRestricoes(SORTEIO_PATH, List.of(1, 2, 3), numeroRecorrenteDto.getNumero());
        Assertions.assertEquals(2, aposta.size());
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
