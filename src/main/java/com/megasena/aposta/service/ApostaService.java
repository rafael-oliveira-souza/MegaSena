package com.megasena.aposta.service;

import com.google.gson.Gson;
import com.megasena.aposta.ApostaFactory;
import com.megasena.aposta.dtos.SorteioDto;
import com.megasena.aposta.enums.FrequenciaRepeticaoEnum;
import com.megasena.aposta.enums.ResultadosEnum;
import com.megasena.aposta.strategy.ApostaStrategy;
import com.megasena.aposta.utils.ApostaUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static com.megasena.aposta.utils.ApostaUtils.getGson;

@Service
public class ApostaService {

    public List<List<Integer>> gerarApostasERelatorio(ResultadosEnum resultadosEnum,
                                                      int quantidadeNumeros,
                                                      LocalDate dataInicio,
                                                      LocalDate dataFim,
                                                      int quantidadeApostas,
                                                      int quantidadeParticipantes,
                                                      double valorPremio,
                                                      FrequenciaRepeticaoEnum... frequencias) {
        List<List<Integer>> apostas = new ArrayList<>();
        List<SorteioDto> sorteioDtos = ApostaUtils.buscarResultados(resultadosEnum);

        while (apostas.size() < quantidadeApostas) {
            FrequenciaRepeticaoEnum freq = ApostaFactory.gerarFrequenciaAleatoria(frequencias);
            List<Integer> apostaGerada = gerarAposta(resultadosEnum, sorteioDtos, freq, dataInicio, dataFim, quantidadeNumeros);
            if (possuiApostaRepetida(apostas, apostaGerada)) {
                dataInicio = dataInicio.plusMonths(1);
            } else {
                apostas.add(apostaGerada);
            }
        }

        gerarRelatorio(resultadosEnum, quantidadeApostas,
                quantidadeNumeros, quantidadeParticipantes,
                valorPremio, apostas);

        return apostas;
    }

    public List<Integer> gerarAposta(
            ResultadosEnum resultadosEnum,
            List<SorteioDto> sorteios,
            FrequenciaRepeticaoEnum freq,
            LocalDate dataInicio,
            LocalDate dataFim,
            int quantidadeNumeros) {
        List<SorteioDto> sorteiosFiltrados = sorteios
                .stream()
                .filter(sorteioDto -> (Objects.nonNull(dataInicio)
                        && (sorteioDto.getData().isAfter(dataInicio)
                        || sorteioDto.getData().isEqual(dataInicio)))
                        && (Objects.nonNull(dataFim)
                        && (sorteioDto.getData().isBefore(dataFim)
                        || sorteioDto.getData().isEqual(dataFim)))
                )
                .toList();

        Map<Integer, Integer> mapSorteios = buscaMapNumerosRecorrentes(sorteiosFiltrados, resultadosEnum.getQtdNumeros());
        ApostaStrategy apostaStrategy = ApostaFactory.criarTipoAposta(freq);

        List<Integer> aposta = apostaStrategy.criarAposta(quantidadeNumeros, mapSorteios);
        if (!apostaStrategy.isApostaValida(aposta, quantidadeNumeros)) {
            aposta = gerarAposta(resultadosEnum, sorteiosFiltrados, freq, dataInicio.plusMonths(1), dataFim, quantidadeNumeros);
        }

        return aposta
                .stream()
                .sorted(Integer::compareTo)
                .toList();
    }


    private Map<Integer, Integer> buscaMapNumerosRecorrentes(List<SorteioDto> resultados, Integer qtdNumeros) {
        Map<Integer, Integer> mapRecorrentes = new HashMap<>();

        for (int i = 1; i <= qtdNumeros; i++) {
            mapRecorrentes.put(i, 0);
        }

        resultados.forEach(sort ->
                sort.getResultados().forEach(numSorteado -> {
                    int valorSorteado = mapRecorrentes.get(numSorteado);
                    valorSorteado++;
                    mapRecorrentes.put(numSorteado, valorSorteado);
                }));

        return mapRecorrentes;
    }

    private String converterAposta(List<Integer> aposta) {
        StringBuilder apostaBuilder = new StringBuilder();
        aposta.stream()
                .sorted(Integer::compareTo)
                .forEach(numero -> apostaBuilder.append(numero).append("_"));

        return apostaBuilder.toString();
    }

    private boolean possuiApostaRepetida(List<List<Integer>> apostas, List<Integer> apostaGerada) {
        if (apostas.isEmpty()) {
            return false;
        }

        String apostaConvertida = converterAposta(apostaGerada);
        return apostas
                .stream()
                .map(this::converterAposta)
                .anyMatch(apostaFeita -> apostaFeita.equals(apostaConvertida));
    }

    private List<List<Integer>> gerarRelatorio(ResultadosEnum resultado,
                                               int quantidadeApostas,
                                               int quantidadeNumeros,
                                               int quantidadeParticipantes,
                                               double valorPremio,
                                               List<List<Integer>> apostas) {
        List<List<Integer>> multiplasApostas = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.valueOf(quantidadeApostas * resultado.getValores().get(quantidadeNumeros))
                .setScale(2, RoundingMode.HALF_UP);
        StringBuilder msg = new StringBuilder()
                .append("Quantidade de Apostas: ")
                .append(quantidadeApostas)
                .append("\nQuantidade de Participantes: ")
                .append(quantidadeParticipantes)
                .append("\nValor Total das Apostas: R$ ")
                .append(valorTotal)
                .append("\nValor Total por Participantes: R$ ")
                .append(BigDecimal.valueOf(valorTotal.doubleValue() / quantidadeParticipantes).setScale(2, RoundingMode.HALF_UP).toString())
                .append("\nValor Do Prêmio: R$ ")
                .append(BigDecimal.valueOf(valorPremio).setScale(2, RoundingMode.HALF_UP).toString())
                .append("\nValor Do Prêmio por Participante: R$ ")
                .append(BigDecimal.valueOf(valorPremio / quantidadeParticipantes).setScale(2, RoundingMode.HALF_UP).toString())
                .append("\n\nApostas: [ \n ");

        apostas.forEach(aposta -> {
            multiplasApostas.add(aposta);
            Gson gson = getGson();
            msg.append(gson.toJson(aposta))
                    .append(", \n");
        });

        msg.append("];");

        ApostaUtils.gerarArquivo("src/docs/" + resultado.name().toLowerCase() + "/relatorioApostas.txt", msg.toString().replace(", \n];", "\n];"));
        return multiplasApostas;
    }
}
