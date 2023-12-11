package com.megasena.aposta;

import com.megasena.aposta.enums.FrequenciaRepeticaoEnum;
import com.megasena.aposta.utils.ApostaUtils;
import com.megasena.aposta.dtos.NumeroRecorrenteDto;
import com.megasena.aposta.dtos.SorteioDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class MegaSenaTests {

    public static final String SORTEIO_PATH = "src/main/resources/resultados/mega_sena_ate_concurso_2666.json";

    @Test
    void contextLoads() {
        List<SorteioDto> sorteioDtos = ApostaUtils
                .buscarResultados(SORTEIO_PATH);

        List<SorteioDto> sorteiosPorData = ApostaUtils
                .buscarResultadosPorData(SORTEIO_PATH,
                        LocalDate.parse("2021-01-01"),
                        LocalDate.parse("2021-11-03"));

        List<NumeroRecorrenteDto> numeroRecorrenteDtos = ApostaUtils
                .buscarNumerosRecorrentes(SORTEIO_PATH);
        NumeroRecorrenteDto numeroMinimoRepeticoes = ApostaUtils.buscarNumeroMinimoRepeticoes(SORTEIO_PATH);
        NumeroRecorrenteDto numeroMaximoRepeticoes = ApostaUtils.buscarNumeroMaximoRepeticoes(SORTEIO_PATH);
        NumeroRecorrenteDto numeroRecorrenteDto = ApostaUtils.buscarRecorrencia(SORTEIO_PATH, 15);
        List<Integer> apostaMax = ApostaUtils.criarAposta(SORTEIO_PATH, 7, FrequenciaRepeticaoEnum.MAX);
        List<Integer> apostaMin = ApostaUtils.criarAposta(SORTEIO_PATH, 7, FrequenciaRepeticaoEnum.MIN);
        List<Integer> apostaRandom = ApostaUtils.criarAposta(SORTEIO_PATH, 7, FrequenciaRepeticaoEnum.RANDOM);
        List<Integer> apostaMid = ApostaUtils.criarAposta(SORTEIO_PATH, 7, FrequenciaRepeticaoEnum.MID);

        Assertions.assertFalse(apostaMax.isEmpty());
        Assertions.assertFalse(apostaMin.isEmpty());
        Assertions.assertFalse(apostaMid.isEmpty());
        Assertions.assertFalse(apostaRandom.isEmpty());
        Assertions.assertFalse(sorteioDtos.isEmpty());
        Assertions.assertFalse(sorteiosPorData.isEmpty());
        Assertions.assertFalse(numeroRecorrenteDtos.isEmpty());
        Assertions.assertTrue(numeroMinimoRepeticoes.getRepeticoes() > 0);
        Assertions.assertTrue(numeroMaximoRepeticoes.getRepeticoes() > 0);
        Assertions.assertTrue(numeroRecorrenteDto.getRepeticoes() > 0);
        Assertions.assertNotEquals(sorteioDtos.size(), sorteiosPorData.size());
    }

}
