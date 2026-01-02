package com.megasena.aposta.service;

import com.google.gson.Gson;
import com.megasena.aposta.ApostaFactory;
import com.megasena.aposta.dtos.ApostaDto;
import com.megasena.aposta.dtos.SorteioDto;
import com.megasena.aposta.enums.FrequenciaRepeticaoEnum;
import com.megasena.aposta.enums.ResultadosEnum;
import com.megasena.aposta.strategy.ApostaStrategy;
import com.megasena.aposta.utils.ApostaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.megasena.aposta.utils.ApostaUtils.getGson;

@Slf4j
@Service
public class ApostaService {

    private Set<Integer> ignoreNumbers = new HashSet<>();
    private Map<String, Integer> tiposAposta = new HashMap<>();

    public ApostaService(Set<Integer> ignoreNumbers) {
        this.ignoreNumbers.addAll(ignoreNumbers);
        for (FrequenciaRepeticaoEnum value : FrequenciaRepeticaoEnum.values()) {
            tiposAposta.put(value.name(), 0);
        }
    }

    public List<List<Integer>> gerarApostasERelatorio(ResultadosEnum resultadosEnum,
                                                      int quantidadeNumeros,
                                                      LocalDate dataInicio,
                                                      LocalDate dataFim,
                                                      int quantidadeApostas,
                                                      int quantidadeParticipantes,
                                                      BigDecimal valorPremio,
                                                      FrequenciaRepeticaoEnum... frequencias) {

        List<List<Integer>> apostas = new ArrayList<>();
        List<SorteioDto> sorteioDtos = ApostaUtils.buscarResultados(resultadosEnum);
//        listarRepetidos(sorteioDtos.stream()
//                .map(SorteioDto::getResultados)
//                .toList());
        LocalDate dataInicioBackup = dataInicio;

        int mediaFrequencias = (int) Math.ceil((double) quantidadeApostas / frequencias.length);

        while (apostas.size() < quantidadeApostas) {
            FrequenciaRepeticaoEnum freq = ApostaFactory.gerarFrequenciaAleatoria(frequencias);
            if (tiposAposta.get(freq.name()) < mediaFrequencias) {
                try {
                    ApostaDto apostaDto = gerarAposta(
                            resultadosEnum, sorteioDtos, freq,
                            dataInicio, dataFim, quantidadeNumeros
                    );

                    List<Integer> apostaGerada = apostaDto.getAposta()
                            .stream().sorted().toList();
                    if (possuiApostaRepetida(apostas, apostaGerada)) {
                        dataInicio = dataInicio.plusWeeks(1);
                        if (dataInicio.isAfter(dataFim)) {
                            int idx = ApostaFactory.gerarNumeroAleatorio(0, apostaGerada.size() - 1);
                            ignoreNumbers.add(apostaGerada.get(idx));

                            if (ignoreNumbers.size() >= apostaGerada.size() * 0.4) {
                                ignoreNumbers.clear();
                            }
                            dataInicio = dataInicioBackup;
                        }
                    } else {
                        tiposAposta.put(apostaDto.getFrequencia().name(), tiposAposta.get(apostaDto.getFrequencia().name()) + 1);
                        apostas.add(apostaGerada);
                        log.info("Aposta {} criada({}): {}", apostaDto.getFrequencia().name(), apostas.size(), apostaGerada);
                    }

                } catch (Exception e) {
                    tiposAposta.put(freq.name(), tiposAposta.get(freq.name()) + 1);
                    log.error(e.getMessage());
                }
            }
        }

        gerarRelatorio(resultadosEnum, quantidadeApostas,
                quantidadeNumeros, quantidadeParticipantes,
                valorPremio, apostas);

        listarRepetidos(apostas);
        listarVencedores(apostas, sorteioDtos);

        return apostas;
    }

    public ApostaDto gerarAposta(ResultadosEnum resultadosEnum,
                                 List<SorteioDto> sorteioDtos,
                                 FrequenciaRepeticaoEnum freq,
                                 LocalDate dataInicio,
                                 LocalDate dataFim,
                                 int quantidadeNumeros) {
        if (dataInicio.isAfter(dataFim)) {
            ApostaStrategy apostaStrategy =
                    ApostaFactory.criarTipoAposta(resultadosEnum, FrequenciaRepeticaoEnum.RANDOM);
            return ApostaDto.builder()
                    .aposta(apostaStrategy.criarAposta(quantidadeNumeros, new HashMap<>()))
                    .frequencia(FrequenciaRepeticaoEnum.RANDOM)
                    .build();
        } else {
            List<SorteioDto> sorteiosFiltrados = getSorteiosFiltrados(sorteioDtos, dataInicio, dataFim);
            Map<Integer, Integer> mapSorteios =
                    buscaMapNumerosRecorrentes(sorteiosFiltrados, resultadosEnum.getQtdNumeros());

            ApostaStrategy apostaStrategy =
                    ApostaFactory.criarTipoAposta(resultadosEnum, freq);

            List<Integer> aposta = apostaStrategy.criarAposta(quantidadeNumeros, mapSorteios);

            if (!apostaStrategy.isApostaValida(aposta, quantidadeNumeros, resultadosEnum)) {
                return gerarAposta(
                        resultadosEnum, sorteioDtos, freq,
                        dataInicio.plusWeeks(1), dataFim, quantidadeNumeros
                );
            }
            return ApostaDto.builder()
                    .aposta(aposta)
                    .frequencia(freq)
                    .build();
        }

    }

    private List<SorteioDto> getSorteiosFiltrados(List<SorteioDto> sorteios, LocalDate dataInicio, LocalDate dataFim) {
        List<SorteioDto> sorteiosFiltrados = new ArrayList<>();

        for (SorteioDto dto : sorteios) {
            boolean depoisInicio = dataInicio != null &&
                    (dto.getData().isAfter(dataInicio) || dto.getData().isEqual(dataInicio));

            boolean antesFim = dataFim != null &&
                    (dto.getData().isBefore(dataFim) || dto.getData().isEqual(dataFim));

            if (depoisInicio && antesFim) {
                sorteiosFiltrados.add(dto);
            }
        }
        return sorteiosFiltrados;
    }

    private Map<Integer, Integer> buscaMapNumerosRecorrentes(List<SorteioDto> resultados, Integer qtdNumeros) {
        Map<Integer, Integer> map = new HashMap<>();

        // Inicializa todos os números possíveis
        for (int i = 1; i <= qtdNumeros; i++) {
            if (!ignoreNumbers.contains(i)) {
                map.put(i, 0);
            }
        }

        // Conta as ocorrências
        for (SorteioDto dto : resultados) {
            List<Integer> numeros = dto.getResultados();
            if (numeros == null) continue;

            for (Integer num : numeros) {
                if (!ignoreNumbers.contains(num)) {
                    map.compute(num, (k, v) -> v == null ? 1 : v + 1);
                }
            }
        }

        return map;
    }


    private String converterAposta(List<Integer> aposta) {
        List<Integer> copia = new ArrayList<>(aposta);
        Collections.sort(copia);

        StringBuilder sb = new StringBuilder();
        for (Integer n : copia) {
            sb.append(n).append("_");
        }
        return sb.toString();
    }

    private boolean possuiApostaRepetida(List<List<Integer>> apostas, List<Integer> apostaGerada) {
        String atual = converterAposta(apostaGerada);

        for (List<Integer> aposta : apostas) {
            if (converterAposta(aposta).equals(atual)) {
                return true;
            }
        }
        return false;
    }

    private List<List<Integer>> gerarRelatorio(ResultadosEnum resultado,
                                               Integer quantidadeApostas,
                                               Integer quantidadeNumeros,
                                               Integer quantidadeParticipantes,
                                               BigDecimal valorPremio,
                                               List<List<Integer>> apostas) {

        BigDecimal valorTotal = BigDecimal.valueOf(
                quantidadeApostas * resultado.getValores().get(quantidadeNumeros)
        ).setScale(2, RoundingMode.HALF_UP);

        StringBuilder msg = new StringBuilder()
                .append("Criação das Apostas: ")
                .append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append("\nQuantidade de Apostas: ").append(quantidadeApostas)
                .append("\nQuantidade de Participantes: ").append(quantidadeParticipantes)
                .append("\nValor Total das Apostas: R$ ").append(valorTotal)
                .append("\nValor Total por Participante: R$ ")
                .append(valorTotal.divide(BigDecimal.valueOf(quantidadeParticipantes), 2, RoundingMode.HALF_UP))
                .append("\nValor do Prêmio: R$ ").append(valorPremio)
                .append("\nValor do Prêmio por Participante: R$ ")
                .append(valorPremio.divide(BigDecimal.valueOf(quantidadeParticipantes), 2, RoundingMode.HALF_UP))
                .append("\nTipos de Apostas: ").append(tiposAposta)
                .append("\n\nApostas:\n");

        Gson gson = getGson();
        for (List<Integer> aposta : apostas) {
            msg.append(gson.toJson(aposta)).append("\n");
        }
        String criarApostaTemplateJS = lerArquivo("src/js/criarApostaTemplate.js");
        String template = (StringUtil.isNotBlank(criarApostaTemplateJS) ? criarApostaTemplateJS : "")
                .replace(":APOSTAS_GERADAS", gson.toJson(apostas))
                .replaceAll("\"\\[", "[")
                .replaceAll("]\"", "]");
        gerarArquivo("src/js/" + resultado.name().toLowerCase() + "/criarAposta.js", template);

        gerarArquivo("src/docs/" + resultado.name().toLowerCase() + "/relatorioApostas.txt", msg.toString());
        return apostas;
    }

    private String lerArquivo(String nomeArquivo) {
        try {
            return Files.readString(new File(nomeArquivo).toPath());
        } catch (Exception e) {
            log.error("Falha ao ler arquivo {}", nomeArquivo);
            return null;
        }
    }

    private void gerarArquivo(String nomeArquivo, String texto) {
        try (FileOutputStream fos = new FileOutputStream(nomeArquivo)) {
            fos.write(texto.getBytes());
        } catch (Exception e) {
            log.error("Falha ao criar arquivo {}", nomeArquivo);
        }
    }

    private void listarRepetidos(List<List<Integer>> apostas) {
        Set<String> unicos = new HashSet<>();
        Set<String> repetidos = new HashSet<>();

        for (List<Integer> aposta : apostas) {
            String apostaConvertida = converterAposta(aposta);
            if (unicos.contains(apostaConvertida)) {
                repetidos.add(apostaConvertida);
            }
            unicos.add(apostaConvertida);
        }
//        log.info("Ultimos resultados: {}", apostas.subList(apostas.size()-20, apostas.size()));
        log.info("Repetidos({}): {}", repetidos.size(), repetidos);
    }

    private void listarVencedores(List<List<Integer>> apostas, List<SorteioDto> sorteioDtos) {
        Set<List<Integer>> resultados = new HashSet<>();

        for (SorteioDto dto : sorteioDtos) {
            resultados.add(dto.getResultados());
        }

        Set<List<Integer>> vencedores = new HashSet<>();
        for (List<Integer> aposta : apostas) {
            if (resultados.contains(aposta)) {
                vencedores.add(aposta);
            }
        }

        log.info("Sorteados({}): {}", vencedores.size(), vencedores);
    }
}
