package com.megasena.aposta;

import com.megasena.aposta.dtos.SorteioDto;
import com.megasena.aposta.enums.ResultadosEnum;
import com.megasena.aposta.service.ApostaService;
import com.megasena.aposta.service.SorteioService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class MegaSenaUpTests {

    public static final int QTD_NUMEROS = 6;
    public static final int QTD_PARTICIPANTES = 1;
    public static final ResultadosEnum SORTEIO_PATH = ResultadosEnum.MEGA_SENA;
    public static final int QTD_APOSTAS = 20;
    public static final double VALOR_TOTAL = 500;
    public static final double VALOR_PREMIO = 1000000000;

    List<List<Integer>> multiplasApostas = new ArrayList<>();

    @Test
    void contextLoads() {

        ApostaService apostaService = new ApostaService();
        List<SorteioDto> sorteioDtos = new SorteioService().carregarSorteios(SORTEIO_PATH);
        for (int i = 0; i < QTD_APOSTAS; i++) {
            List<Integer> apostas = apostaService.gerarAposta(sorteioDtos, QTD_NUMEROS);
            multiplasApostas.add(apostas);
        }
        log.info("Valor Total={}", multiplasApostas.size() * SORTEIO_PATH.getValores().get(QTD_NUMEROS));
        log.info("\n\n##########################################################################################################################################\n");
        apostaService.gerarRelatorio(
                SORTEIO_PATH, QTD_APOSTAS, QTD_NUMEROS,
                QTD_PARTICIPANTES, VALOR_PREMIO,
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2024, 12, 31));
    }

}
