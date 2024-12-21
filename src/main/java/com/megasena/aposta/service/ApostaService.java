package com.megasena.aposta.service;

import com.google.gson.Gson;
import com.megasena.aposta.dtos.SorteioDto;
import com.megasena.aposta.enums.ResultadosEnum;
import com.megasena.aposta.utils.ApostaUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.megasena.aposta.utils.ApostaUtils.getGson;

@Service
public class ApostaService {

    private Random random = new Random();

    public List<Integer> gerarAposta(List<SorteioDto> sorteios, int quantidadeNumeros) {
        List<Integer> todosNumeros = new ArrayList<>();

        // Coletar todos os números sorteados
        for (SorteioDto sorteio : sorteios) {
            todosNumeros.addAll(sorteio.getResultados());
        }

        // Gerar a aposta com a quantidade de números desejada
        List<Integer> aposta = new ArrayList<>();
        while (aposta.size() < quantidadeNumeros) {
            int numero = todosNumeros.get(random.nextInt(todosNumeros.size()));
            if (!aposta.contains(numero)) {
                aposta.add(numero);
            }
        }

        return aposta;
    }

    public List<List<Integer>> gerarRelatorio(ResultadosEnum resultado,
                                              int quantidadeApostas,
                                              int quantidadeNumeros,
                                              int quantidadeParticipantes,
                                              double valorTotal,
                                              double valorPremio,
                                              LocalDate primeiroAno,
                                              LocalDate ultimoAno) {
        List<List<Integer>> multiplasApostas = new ArrayList<>();
        List<SorteioDto> sorteioDtos = new SorteioService()
                .carregarSorteios(resultado)
                .stream().filter(sorteioDto -> sorteioDto.getData().isAfter(primeiroAno) && sorteioDto.getData().isBefore(ultimoAno))
                .collect(Collectors.toList());

        StringBuilder msg = new StringBuilder()
                .append("Quantidade de Apostas: ")
                .append(quantidadeApostas)
                .append("\nQuantidade de Participantes: ")
                .append(quantidadeParticipantes)
                .append("\nValor Total das Apostas: R$ ")
                .append(valorTotal)
                .append("\nValor Total por Participantes: R$ ")
                .append(BigDecimal.valueOf(valorTotal / quantidadeParticipantes).setScale(2, RoundingMode.HALF_UP).toString())
                .append("\nValor Do Prêmio: R$ ")
                .append(valorPremio)
                .append("\nValor Do Prêmio por Participante: R$ ")
                .append(BigDecimal.valueOf(valorPremio / quantidadeParticipantes).setScale(2, RoundingMode.HALF_UP).toString())
                .append("\n\nApostas: [ \n ");

        for (int i = 0; i < quantidadeApostas; i++) {
            List<Integer> aposta = new ApostaService().gerarAposta(sorteioDtos, quantidadeNumeros);
            multiplasApostas.add(aposta);
            Gson gson = getGson();
            msg.append(gson.toJson(aposta))
                    .append(", \n");
        }
        msg.append("];");

        ApostaUtils.gerarArquivo("src/docs/" + resultado.name().toLowerCase() + "/relatorioApostas.txt", msg.toString().replace(", \n];", "\n];"));
        return multiplasApostas;
    }
}
