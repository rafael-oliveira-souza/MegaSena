package com.megasena.aposta.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.megasena.aposta.dtos.NumeroRecorrenteDto;
import com.megasena.aposta.dtos.SorteioDto;
import com.megasena.aposta.enums.FrequenciaRepeticaoEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@UtilityClass
public class ApostaUtils {
    public static final String SORTEIO_PATH = "src/main/resources/resultados/mega_sena_ate_concurso_2666.json";

    public static List<List<Integer>> criarAposta(int qtdApostas, int qtdNumeros) {
        List<List<Integer>> apostas = new LinkedList<>();
        List<SorteioDto> sorteioDtos = ApostaUtils
                .buscarResultados(SORTEIO_PATH);

        LocalDate dataInicio = sorteioDtos.get(0).getData();
        LocalDate dataFim = sorteioDtos.get(sorteioDtos.size() - 1).getData();

        while (apostas.size() < qtdApostas) {
            Integer numeroAleatorio = gerarNumeroAleatorio(1, 4);
            if (numeroAleatorio.equals(1)) {
                List<Integer> aposta = criarAposta(SORTEIO_PATH, qtdNumeros, FrequenciaRepeticaoEnum.MAX, dataInicio, dataFim);
                adicionarAposta(apostas, aposta, qtdNumeros);
            } else if (numeroAleatorio.equals(2)) {
                List<Integer> aposta = criarAposta(SORTEIO_PATH, qtdNumeros, FrequenciaRepeticaoEnum.MIN, dataInicio, dataFim);
                adicionarAposta(apostas, aposta, qtdNumeros);
            } else if (numeroAleatorio.equals(3)) {
                List<Integer> aposta = criarAposta(SORTEIO_PATH, qtdNumeros, FrequenciaRepeticaoEnum.MID, dataInicio, dataFim);
                adicionarAposta(apostas, aposta, qtdNumeros);
            } else {
                List<Integer> aposta = criarAposta(SORTEIO_PATH, qtdNumeros, FrequenciaRepeticaoEnum.RANDOM, dataInicio, dataFim);
                adicionarAposta(apostas, aposta, qtdNumeros);
            }
            dataInicio = dataInicio.plusYears(1);
        }

        return apostas;
    }

    public static List<Integer> criarAposta(String sorteioPath,
                                            int qtdNumeros,
                                            FrequenciaRepeticaoEnum repeticaoEnum,
                                            LocalDate dataInicio,
                                            LocalDate dataFim) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentesPorData(sorteioPath, dataInicio, dataFim);

        List<Integer> aposta = new LinkedList<>();
        if (FrequenciaRepeticaoEnum.MAX.equals(repeticaoEnum)) {
            aposta.addAll(criarApostaMaxima(qtdNumeros, resultados));
        } else if (FrequenciaRepeticaoEnum.MIN.equals(repeticaoEnum)) {
            aposta.addAll(criarApostaMinima(qtdNumeros, resultados));
        } else if (FrequenciaRepeticaoEnum.RANDOM.equals(repeticaoEnum)) {
            aposta.addAll(criarApostaRandom(qtdNumeros, resultados));
        } else {
            aposta.addAll(criarApostaMedia(qtdNumeros, resultados));
        }

        if(!validarQuantidadeNumerosAposta(aposta, qtdNumeros)){
            return ApostaUtils.criarAposta(sorteioPath, qtdNumeros, repeticaoEnum, dataInicio, dataFim);
        }

        return aposta;
    }

    private static List<Integer> criarApostaMinima(int qtdNumeros, Map<Integer, Integer> resultados) {
        List<Integer> aposta = new ArrayList<>();
        for (int i = 0; i < qtdNumeros; i++) {
            Map.Entry<Integer, Integer> map = resultados
                    .entrySet()
                    .stream()
                    .min(Comparator.comparingInt(Map.Entry::getValue))
                    .orElse(null);

            if (Objects.nonNull(map)) {
                aposta.add(map.getKey());
                resultados.remove(map.getKey());
            }
        }

        return aposta;
    }

    private static List<Integer> criarApostaMaxima(int qtdNumeros, Map<Integer, Integer> resultados) {
        List<Integer> aposta = new ArrayList<>();
        for (int i = 0; i < qtdNumeros; i++) {
            Map.Entry<Integer, Integer> map = resultados
                    .entrySet()
                    .stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .orElse(null);

            if (Objects.nonNull(map)) {
                aposta.add(map.getKey());
                resultados.remove(map.getKey());
            }
        }

        return aposta;
    }

    private static List<Integer> criarApostaMedia(final int qtdNumeros, final Map<Integer, Integer> resultados) {
        int mid = qtdNumeros / 2;
        List<Integer> aposta = new ArrayList<>();
        aposta.addAll(ApostaUtils.criarApostaMinima(mid, resultados));
        aposta.addAll(ApostaUtils.criarApostaMaxima(qtdNumeros-mid, resultados));

        return aposta;
    }

    private static List<Integer> criarApostaRandom(int qtdNumeros, Map<Integer, Integer> resultados) {
        List<Integer> aposta = new ArrayList<>();
        for (int i = 0; i < qtdNumeros; i++) {
            int randomInt = gerarNumeroAleatorio(1, 60);
            if (!aposta.contains(randomInt)) {
                resultados.remove(randomInt);
                aposta.add(randomInt);
            }
        }

        return aposta;
    }

    private static void adicionarAposta(List<List<Integer>> apostas, List<Integer> aposta, Integer qtdNumeros){
        if(!validarSeApostaValida(apostas, aposta, qtdNumeros)){
            apostas.add(aposta);
        }
    }

    private static String converterAposta(List<Integer> aposta){
        StringBuilder apostaBuilder = new StringBuilder();
        aposta.forEach(numero -> apostaBuilder.append(numero.toString()).append("_"));

        return apostaBuilder.toString();
    }

    private static boolean validarSeApostaValida(List<List<Integer>> apostas, List<Integer> aposta, Integer qtdNumeros){
        if (!validarQuantidadeNumerosAposta(aposta, qtdNumeros)){
            return false;
        }

        String apostaConvertida = converterAposta(aposta);
        return apostas
                .stream()
                .map(ApostaUtils::converterAposta)
                .anyMatch(apostaFeita -> apostaFeita.equals(apostaConvertida));
    }

    private static boolean validarQuantidadeNumerosAposta(List<Integer> aposta, Integer qtdNumeros) {
        if(aposta.size() != qtdNumeros){
            return false;
        }
        return true;
    }

    public static NumeroRecorrenteDto buscarNumeroMinimoRepeticoesPorData(String sorteioPath, LocalDate dataInicio, LocalDate dataFim) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentesPorData(sorteioPath, dataInicio, dataFim);

        return resultados
                .entrySet()
                .stream()
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .orElse(null);
    }

    public static NumeroRecorrenteDto buscarNumeroMinimoRepeticoes(String sorteioPath) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentes(sorteioPath);

        return resultados
                .entrySet()
                .stream()
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .orElse(null);
    }

    public static NumeroRecorrenteDto buscarNumeroMaximoRepeticoesPorData(String sorteioPath, LocalDate dataInicio, LocalDate dataFim) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentesPorData(sorteioPath, dataInicio, dataFim);

        return resultados
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .orElse(null);
    }

    public static NumeroRecorrenteDto buscarNumeroMaximoRepeticoes(String sorteioPath) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentes(sorteioPath);

        return resultados
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .orElse(null);
    }

    public static NumeroRecorrenteDto buscarRecorrencia(String sorteioPath, Integer numero) {
        List<NumeroRecorrenteDto> resultados = buscarNumerosRecorrentes(sorteioPath);

        return resultados
                .stream()
                .filter(numeroRecorrenteDto -> numeroRecorrenteDto.getNumero().equals(numero))
                .findFirst().orElse(null);
    }

    private static Map<Integer, Integer> buscaMapNumerosRecorrentesPorData(String sorteioPath, LocalDate dataInicio, LocalDate dataFim) {
        List<SorteioDto> resultados = buscarResultadosPorData(sorteioPath, dataInicio, dataFim);
        Map<Integer, Integer> mapRecorrentes = new HashMap<>();

        for (int i = 1; i <= 60; i++) {
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

    private static Map<Integer, Integer> buscaMapNumerosRecorrentes(String sorteioPath) {
        List<SorteioDto> resultados = buscarResultados(sorteioPath);
        Map<Integer, Integer> mapRecorrentes = new HashMap<>();

        for (int i = 1; i <= 60; i++) {
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

    public static List<NumeroRecorrenteDto> buscarNumerosRecorrentesPorData(String sorteioPath, LocalDate dataInicio, LocalDate dataFim) {
        return buscaMapNumerosRecorrentesPorData(sorteioPath, dataInicio, dataFim)
                .entrySet()
                .stream()
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .toList();
    }

    public static List<NumeroRecorrenteDto> buscarNumerosRecorrentes(String sorteioPath) {
        return buscaMapNumerosRecorrentes(sorteioPath)
                .entrySet()
                .stream()
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .toList();
    }

    public static List<SorteioDto> buscarResultadosPorData(String sorteioPath,
                                                           LocalDate dataInicio,
                                                           LocalDate dataFim) {
        List<SorteioDto> resultados = buscarResultados(sorteioPath);

        return resultados
                .stream()
                .filter(sorteioDto -> sorteioDto.getData().isAfter(dataInicio) && sorteioDto.getData().isBefore(dataFim))
                .toList();
    }

    public static List<SorteioDto> buscarResultados(String sorteioPath) {
        try {
            return lerArquivoResultados(sorteioPath)
                    .stream()
                    .sorted((o1, o2) -> {
                        if (o1.getData().isBefore(o2.getData())) {
                            return -1;
                        } else if (o1.getData().isAfter(o2.getData())) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }).toList();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar sorteios " + e.getMessage());
        }
    }

    private static List<SorteioDto> lerArquivoResultados(String sorteioPath) {
        try {
            String resultados = Files.readString(new File(sorteioPath).toPath());
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                    .create();

            return gson.fromJson(resultados, new TypeToken<List<SorteioDto>>() {
            }.getType());
        } catch (Exception e) {
            throw new RuntimeException("Falha ao ler arquivo de resultados " + e.getMessage());
        }
    }

    private Integer gerarNumeroAleatorio(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    private static class LocalDateGsonSerializer extends TypeAdapter<LocalDate> {

        @Override
        public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
            jsonWriter.value(localDate.toString());
        }

        @Override
        public LocalDate read(JsonReader jsonReader) throws IOException {
            JsonToken jsonToken = jsonReader.peek();
            if (jsonToken.equals(JsonToken.STRING)) {
                return LocalDate.parse(jsonReader.nextString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            return null;
        }
    }

    public static void gerarRelatorio(LocalDate primeiroAno, LocalDate ultimoAno){
        ultimoAno = ultimoAno.withDayOfMonth(1);
        primeiroAno = primeiroAno.withDayOfMonth(1);
        StringBuilder linha1 = new StringBuilder();
        linha1.append("Ano");
        StringBuilder linha2 = new StringBuilder();
        boolean primeiraIteracao = true;
        while(ultimoAno.getYear() >= primeiroAno.getYear()) {
            LocalDate proximoAno = primeiroAno.plusYears(1);
            List<NumeroRecorrenteDto> numeroRecorrenteUltimoAno = ApostaUtils
                    .buscarNumerosRecorrentesPorData(SORTEIO_PATH, primeiroAno, proximoAno);

            linha2.append("").append(primeiroAno.getYear()).append("");
            boolean finalPrimeiraIteracao = primeiraIteracao;
            numeroRecorrenteUltimoAno.forEach(numero -> {
                if(finalPrimeiraIteracao) {
                    linha1.append(",").append(numero.getNumero());
                }
                linha2.append(",") .append(numero.getRepeticoes().toString());
            });
            linha2.append("\n");
            primeiroAno = proximoAno;
            primeiraIteracao = false;
        }

        try{
            OutputStream os = new FileOutputStream("src/main/resources/docs/relatorio.csv"); // nome do arquivo que será escrito
            Writer wr = new OutputStreamWriter(os); // criação de um escritor
            BufferedWriter br = new BufferedWriter(wr); // adiciono a um escritor de buffer

            br.write(linha1.append("\n").append(linha2).toString());
            br.close();

        }catch (Exception e){
//            return linha1.append("\n").append(linha2).toString();
        }
    }


    public static Integer calcularRepeticoesAno(Integer primeiroAno){
        LocalDate primeiroAnoDate = LocalDate.of(primeiroAno, 1, 1);
        LocalDate ultimoAnoDate = LocalDate.of(primeiroAno+1, 1, 1);
        List<NumeroRecorrenteDto> numeroRecorrenteUltimoAno = ApostaUtils
                .buscarNumerosRecorrentesPorData(SORTEIO_PATH, primeiroAnoDate, ultimoAnoDate);

        AtomicInteger repeticoes = new AtomicInteger(0);
        numeroRecorrenteUltimoAno.forEach(numero -> {
            repeticoes.addAndGet(numero.getRepeticoes());
        });
//        log.info("Ano={}, Repeticoes={}", primeiroAnoDate.getYear(), repeticoes.get());

        return repeticoes.get();
    }
}
