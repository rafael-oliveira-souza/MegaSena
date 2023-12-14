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

@Slf4j
class MegaSenaTests {

    public static final String SORTEIO_PATH = "src/main/resources/resultados/mega_sena_ate_concurso_2666.json";

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

        List<Integer> apostaMax = ApostaUtils.criarAposta(SORTEIO_PATH, 7, FrequenciaRepeticaoEnum.MAX, dataInicio, dataFim);
        List<Integer> apostaMin = ApostaUtils.criarAposta(SORTEIO_PATH, 7, FrequenciaRepeticaoEnum.MIN, dataInicio, dataFim);
        List<Integer> apostaRandom = ApostaUtils.criarAposta(SORTEIO_PATH, 7, FrequenciaRepeticaoEnum.RANDOM, dataInicio, dataFim);
        List<Integer> apostaMid = ApostaUtils.criarAposta(SORTEIO_PATH, 7, FrequenciaRepeticaoEnum.MID, dataInicio, dataFim);
        List<Integer> multiplasAPostas = ApostaUtils.criarAposta(30, 6);
        log.info("Aposta min={}", apostaMin);
        log.info("Aposta max={}", apostaMax);
        log.info("Aposta mid={}", apostaMid);
        log.info("Aposta random={}", apostaRandom);
        log.info("Apostas multiplas={}", multiplasAPostas);

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
