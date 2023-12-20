package com.megasena.aposta;

import com.megasena.aposta.dtos.NumeroRecorrenteDto;
import com.megasena.aposta.dtos.SorteioDto;
import com.megasena.aposta.enums.ResultadosEnum;
import com.megasena.aposta.utils.ApostaUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

@Slf4j
class LotoFacilTests {

    public static final int QTD_NUMEROS = 15;
    public static final int QTD_PARTICIPANTES = 5;
    public static final ResultadosEnum SORTEIO_PATH = ResultadosEnum.LOTO_FACIL;
    public static final int QTD_APOSTAS = 20;
    public static final double VALOR_TOTAL = 30;

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

        List<List<Integer>> multiplasAPostas = ApostaUtils.criarAposta(SORTEIO_PATH, QTD_APOSTAS, QTD_NUMEROS, QTD_PARTICIPANTES);
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

        Assertions.assertFalse(multiplasAPostas.isEmpty());
        Assertions.assertFalse(sorteioDtos.isEmpty());
        Assertions.assertFalse(sorteiosPorData.isEmpty());
        Assertions.assertFalse(numeroRecorrenteDtos.isEmpty());
        Assertions.assertFalse(numeroRecorrenteDtosPorData.isEmpty());
    }

}
