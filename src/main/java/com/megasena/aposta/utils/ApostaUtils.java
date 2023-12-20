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
import com.megasena.aposta.enums.ResultadosEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class ApostaUtils {
    public static final LocalDate DATA_INICIO = LocalDate.of(1996, 11, 3);
    public static final LocalDate DATA_ATUAL = LocalDate.now();
    public static final BigDecimal VLR_PREMIO = new BigDecimal(550000000D);
    public static final int MAX_REPETICOES_ANO = 20;
    public static final int MIN_REPETICOES_ANO = 5;

    public static List<Integer> validarSeApostaPossuiRestricoes(ResultadosEnum resultadosEnum, List<Integer> aposta, Integer numeroRecorrente){
        aposta = removerNumeroMaxNumerosEmSequencia(aposta, resultadosEnum, numeroRecorrente);
        aposta =  removerNumeroMaxRepeticoesAno(aposta, numeroRecorrente);

        return aposta;
    }

    private static List<Integer> removerNumeroMaxRepeticoesAno( List<Integer> aposta, Integer numeroRecorrente){
        AtomicReference<Integer> contador = new AtomicReference<>(0);
        List<Integer> apostaAux = new ArrayList<>();
        int maxRepet = 2;

        if(aposta.size() >= maxRepet) {
            List<Integer> apostaOrdenada = aposta.stream()
                    .sorted(Integer::compareTo)
                    .toList();
            for (int i = 1 ; i < apostaOrdenada.size(); i++) {
                Integer anterior = apostaOrdenada.get(i-1);
                Integer atual = apostaOrdenada.get(i);

                if (anterior.equals(atual-1) || anterior.equals(atual+1)){
                    contador.getAndSet(contador.get() + 1);
                } else if (contador.get() < maxRepet) {
                    contador.set(0);
                }
            }
        }

        for(int i = aposta.size()-1; i >= 0; i--){
            if (contador.get() >= maxRepet){
                contador.getAndSet(contador.get() - 1);
            }else{
                apostaAux.add(aposta.get(i));
            }
        }

       return apostaAux;
    }
    private static List<Integer> removerNumeroMaxNumerosEmSequencia(List<Integer> aposta, ResultadosEnum resultadosEnum, Integer numeroRecorrente){
        NumeroRecorrenteDto numeroMaximoRepeticoes = ApostaUtils.buscarRecorrenciaPorData(
                resultadosEnum, numeroRecorrente, DATA_ATUAL.withMonth(1).withDayOfMonth(1), DATA_ATUAL);

        if (numeroMaximoRepeticoes.getRepeticoes() >= MAX_REPETICOES_ANO){
            aposta.remove(numeroRecorrente);
        }

        return new ArrayList<>(aposta);
    }

    private static NumeroRecorrenteDto recuperarNumeroMinRepeticoesAno(ResultadosEnum resultadosEnum){
        NumeroRecorrenteDto numeroMinimoRepeticoesPorData = ApostaUtils.buscarNumeroMinimoRepeticoesPorData(
                resultadosEnum, DATA_ATUAL.withMonth(1).withDayOfMonth(1), DATA_ATUAL);

        if (numeroMinimoRepeticoesPorData.getRepeticoes() <= MIN_REPETICOES_ANO){
            return numeroMinimoRepeticoesPorData;
        }

        return null;
    }

    public static List<List<Integer>> criarAposta(ResultadosEnum resultado, Integer qtdApostas, Integer qtdNumeros, Integer qtdParticipantes) {
        List<List<Integer>> apostas = new LinkedList<>();
        List<SorteioDto> sorteioDtos = ApostaUtils
                .buscarResultados(resultado);

        LocalDate dataInicio = sorteioDtos.get(0).getData();
        LocalDate dataFim = sorteioDtos.get(sorteioDtos.size() - 1).getData();
        Map<String, Integer> numeroApostas = new HashMap<>();
        for (FrequenciaRepeticaoEnum tipoAposta: FrequenciaRepeticaoEnum.values()) {
            numeroApostas.put(tipoAposta.name(), 0);
        }

        while (apostas.size() < qtdApostas) {
            Integer numeroAleatorio = gerarNumeroAleatorio(1, 4);
            if (numeroAleatorio.equals(1)) {
                List<Integer> aposta = criarAposta(resultado, qtdNumeros, FrequenciaRepeticaoEnum.MAX, dataInicio, dataFim);
                adicionarAposta(apostas, aposta, qtdNumeros);
                Integer mapNum = numeroApostas.get(FrequenciaRepeticaoEnum.MAX.name()) + 1;
                numeroApostas.put(FrequenciaRepeticaoEnum.MAX.name(), mapNum);
            } else if (numeroAleatorio.equals(2)) {
                List<Integer> aposta = criarAposta(resultado, qtdNumeros, FrequenciaRepeticaoEnum.MIN, dataInicio, dataFim);
                adicionarAposta(apostas, aposta, qtdNumeros);
                Integer mapNum = numeroApostas.get(FrequenciaRepeticaoEnum.MIN.name()) + 1;
                numeroApostas.put(FrequenciaRepeticaoEnum.MIN.name(), mapNum);
            } else if (numeroAleatorio.equals(3)) {
                List<Integer> aposta = criarAposta(resultado, qtdNumeros, FrequenciaRepeticaoEnum.MID, dataInicio, dataFim);
                adicionarAposta(apostas, aposta, qtdNumeros);
                Integer mapNum = numeroApostas.get(FrequenciaRepeticaoEnum.MID.name()) + 1;
                numeroApostas.put(FrequenciaRepeticaoEnum.MID.name(), mapNum);
            } else {
                List<Integer> aposta = criarAposta(resultado, qtdNumeros, FrequenciaRepeticaoEnum.RANDOM, dataInicio, dataFim);
                adicionarAposta(apostas, aposta, qtdNumeros);
                Integer mapNum = numeroApostas.get(FrequenciaRepeticaoEnum.RANDOM.name()) + 1;
                numeroApostas.put(FrequenciaRepeticaoEnum.RANDOM.name(), mapNum);
            }
            dataInicio = dataInicio.plusYears(1);
        }

        numeroApostas.forEach((s, num) -> {
            log.info("Tipo de Aposta={}, Quantidade={}", s, num);
        });

        Gson gson = getGson();
        String criarApostaTemplateJS = lerArquivo("src/js/criarApostaTemplate.js");
        String template = criarApostaTemplateJS
                .replace(":APOSTAS_GERADAS", gson.toJson(apostas))
                .replaceAll("\"\\[", "[")
                .replaceAll("]\"", "]");
        gerarArquivo("src/js/" + resultado.name().toLowerCase() +"/criarAposta.js", template);

        String relatorioApostaTemplateJS = lerArquivo("src/docs/relatorioApostasTemplate.txt");
        String valorPremioPart = converterParaMoeda(VLR_PREMIO.divide(new BigDecimal(qtdParticipantes)));
        String valorApostas = converterParaMoeda(BigDecimal.valueOf(apostas.size() * resultado.getValores().get(qtdNumeros)));
        String valorPremio = converterParaMoeda(VLR_PREMIO);

        String templateRelatorio = relatorioApostaTemplateJS
                .replace(":APOSTAS_GERADAS", gson.toJson(apostas))
                .replace(":QTD_PARTICIPANTES", qtdParticipantes.toString())
                .replace(":VLR_PARTICIPANTE", valorPremioPart)
                .replace(":VLR_PREMIO", valorPremio)
                .replace(":VALOR_TOTAL",valorApostas )
                .replace(":QTD_JOGOS", qtdApostas.toString())
                .replace("\"\\[", "[")
                .replace("]\"", "]")
                .replace("],", "], \n")
                .replace("[[", "[ \n[")
                .replace("]]", "] \n]");
        gerarArquivo("src/docs/" + resultado.name().toLowerCase() + "/relatorioApostas.txt", templateRelatorio);

        return apostas;
    }

    public static List<Integer> criarAposta(ResultadosEnum resultado,
                                            int qtdNumeros,
                                            FrequenciaRepeticaoEnum repeticaoEnum,
                                            LocalDate dataInicio,
                                            LocalDate dataFim) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentesPorData(resultado, dataInicio, dataFim);

        List<Integer> aposta = new LinkedList<>();
        NumeroRecorrenteDto numeroRecorrenteMinReptAno = recuperarNumeroMinRepeticoesAno(resultado);
        if(Objects.nonNull(numeroRecorrenteMinReptAno)){
            aposta.add(numeroRecorrenteMinReptAno.getNumero());
            qtdNumeros = (qtdNumeros - aposta.size());
        }

        if (FrequenciaRepeticaoEnum.MAX.equals(repeticaoEnum)) {
            aposta.addAll(criarApostaMaxima(resultado, qtdNumeros, resultados));
        } else if (FrequenciaRepeticaoEnum.MIN.equals(repeticaoEnum)) {
            aposta.addAll(criarApostaMinima(resultado, qtdNumeros, resultados));
        } else if (FrequenciaRepeticaoEnum.RANDOM.equals(repeticaoEnum)) {
            aposta.addAll(criarApostaRandom(resultado, qtdNumeros, resultados));
        } else {
            aposta.addAll(criarApostaMedia(resultado, qtdNumeros, resultados));
        }

        if(!validarQuantidadeNumerosAposta(aposta, qtdNumeros)){
            return ApostaUtils.criarAposta(resultado, qtdNumeros, repeticaoEnum, dataInicio, dataFim);
        }

        return aposta;
    }

    private static List<Integer> criarApostaMinima(ResultadosEnum resultado, int qtdNumeros, Map<Integer, Integer> resultados) {
        List<Integer> aposta = new ArrayList<>();
        while (aposta.size() < qtdNumeros && !resultados.isEmpty()) {
            Map.Entry<Integer, Integer> map = resultados
                    .entrySet()
                    .stream()
                    .min(Comparator.comparingInt(Map.Entry::getValue))
                    .orElse(null);

            if (Objects.nonNull(map)) {
                aposta.add(map.getKey());
                resultados.remove(map.getKey());
                validarSeApostaPossuiRestricoes(resultado, aposta, map.getKey());
            }
        }

        return aposta;
    }

    private static List<Integer> criarApostaMaxima(ResultadosEnum resultado, int qtdNumeros, Map<Integer, Integer> resultados) {
        List<Integer> aposta = new ArrayList<>();
        while (aposta.size() < qtdNumeros && !resultados.isEmpty()) {
            Map.Entry<Integer, Integer> map = resultados
                    .entrySet()
                    .stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .orElse(null);

            if (Objects.nonNull(map)) {
                aposta.add(map.getKey());
                resultados.remove(map.getKey());
                validarSeApostaPossuiRestricoes(resultado, aposta, map.getKey());
            }
        }

        return aposta;
    }

    private static List<Integer> criarApostaMedia(ResultadosEnum resultado,  final int qtdNumeros, final Map<Integer, Integer> resultados) {
        int mid = (qtdNumeros) / 2;
        List<Integer> aposta = new ArrayList<>();
        aposta.addAll(ApostaUtils.criarApostaMinima(resultado, mid, resultados));
        aposta.addAll(ApostaUtils.criarApostaMaxima(resultado, qtdNumeros-aposta.size(), resultados));

        return aposta;
    }

    private static List<Integer> criarApostaRandom(ResultadosEnum resultado, int qtdNumeros, Map<Integer, Integer> resultados) {
        List<Integer> aposta = new ArrayList<>();
        while (aposta.size() < qtdNumeros) {
            int randomInt = gerarNumeroAleatorio(1, resultado.getQtdNumeros());
            if (!aposta.contains(randomInt)) {
                aposta.add(randomInt);
                resultados.remove(randomInt);
                validarSeApostaPossuiRestricoes(resultado, aposta, randomInt);
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
        aposta.stream()
                .sorted(Integer::compareTo)
                .forEach(numero -> apostaBuilder.append(numero).append("_"));

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

    public static NumeroRecorrenteDto buscarNumeroMinimoRepeticoesPorData(ResultadosEnum resultado, LocalDate dataInicio, LocalDate dataFim) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentesPorData(resultado, dataInicio, dataFim);

        return resultados
                .entrySet()
                .stream()
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .orElse(null);
    }

    public static NumeroRecorrenteDto buscarNumeroMinimoRepeticoes(ResultadosEnum resultado) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentes(resultado);

        return resultados
                .entrySet()
                .stream()
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .orElse(null);
    }

    public static NumeroRecorrenteDto buscarNumeroMaximoRepeticoesPorData(ResultadosEnum resultado,
                                                                          LocalDate dataInicio, LocalDate dataFim) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentesPorData(resultado, dataInicio, dataFim);

        return resultados
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .orElse(null);
    }

    public static NumeroRecorrenteDto buscarNumeroMaximoRepeticoes(ResultadosEnum resultado) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentes(resultado);

        return resultados
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .orElse(null);
    }

    public static NumeroRecorrenteDto buscarRecorrencia(ResultadosEnum resultado, Integer numero) {
        List<NumeroRecorrenteDto> resultados = buscarNumerosRecorrentes(resultado);

        return resultados
                .stream()
                .filter(numeroRecorrenteDto -> numeroRecorrenteDto.getNumero().equals(numero))
                .findFirst().orElse(null);
    }

    public static NumeroRecorrenteDto buscarRecorrenciaPorData(ResultadosEnum resultado, Integer numero,
                                                               LocalDate dataInicio, LocalDate dataFim) {
        List<NumeroRecorrenteDto> resultados = buscarNumerosRecorrentesPorData(resultado, dataInicio, dataFim);

        return resultados
                .stream()
                .filter(numeroRecorrenteDto -> numeroRecorrenteDto.getNumero().equals(numero))
                .findFirst().orElse(null);
    }

    private static Map<Integer, Integer> buscaMapNumerosRecorrentesPorData(ResultadosEnum resultado, LocalDate dataInicio, LocalDate dataFim) {
        List<SorteioDto> resultados = buscarResultadosPorData(resultado, dataInicio, dataFim);
        Map<Integer, Integer> mapRecorrentes = new HashMap<>();

        for (int i = 1; i <= resultado.getQtdNumeros(); i++) {
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

    private static Map<Integer, Integer> buscaMapNumerosRecorrentes(ResultadosEnum resultado) {
        List<SorteioDto> resultados = buscarResultados(resultado);
        Map<Integer, Integer> mapRecorrentes = new HashMap<>();

        for (int i = 1; i <= resultado.getQtdNumeros(); i++) {
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

    public static List<NumeroRecorrenteDto> buscarNumerosRecorrentesPorData(ResultadosEnum resultado, LocalDate dataInicio, LocalDate dataFim) {
        return buscaMapNumerosRecorrentesPorData(resultado, dataInicio, dataFim)
                .entrySet()
                .stream()
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .toList();
    }

    public static List<NumeroRecorrenteDto> buscarNumerosRecorrentes(ResultadosEnum resultado) {
        return buscaMapNumerosRecorrentes(resultado)
                .entrySet()
                .stream()
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .toList();
    }

    public static List<SorteioDto> buscarResultadosPorData(ResultadosEnum resultado,
                                                           LocalDate dataInicio,
                                                           LocalDate dataFim) {
        List<SorteioDto> resultados = buscarResultados(resultado);

        return resultados
                .stream()
                .filter(sorteioDto -> sorteioDto.getData().isAfter(dataInicio) && sorteioDto.getData().isBefore(dataFim))
                .toList();
    }

    public static List<SorteioDto> buscarResultados(ResultadosEnum resultado) {
        try {
            return lerArquivoResultados(resultado)
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

    private static List<SorteioDto> lerArquivoResultados(ResultadosEnum resultado) {
        try {
            String resultados = lerArquivo(resultado.getPath());
            Gson gson = getGson();
            return gson.fromJson(resultados, new TypeToken<List<SorteioDto>>() {
            }.getType());
        } catch (Exception e) {
            throw new RuntimeException("Falha ao ler arquivo de resultados " + e.getMessage());
        }
    }

    private static Gson getGson() {
        return new GsonBuilder()
//                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                .create();
    }

    private Integer gerarNumeroAleatorio(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static void gerarRelatorio(ResultadosEnum resultado, LocalDate primeiroAno, LocalDate ultimoAno){
        ultimoAno = ultimoAno.withDayOfMonth(1);
        primeiroAno = primeiroAno.withDayOfMonth(1);
        StringBuilder linha1 = new StringBuilder();
        linha1.append("Ano");
        StringBuilder linha2 = new StringBuilder();
        boolean primeiraIteracao = true;
        while(ultimoAno.getYear() >= primeiroAno.getYear()) {
            LocalDate proximoAno = primeiroAno.plusYears(1);
            List<NumeroRecorrenteDto> numeroRecorrenteUltimoAno = ApostaUtils
                    .buscarNumerosRecorrentesPorData(resultado, primeiroAno, proximoAno);

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

        gerarArquivo("src/docs/" + resultado.name().toLowerCase() + "/relatorio.csv", linha1.append("\n").append(linha2).toString());
    }

    private static String lerArquivo(String nomeArquivo){
        try{
            return Files.readString(new File(nomeArquivo).toPath());
        }catch (Exception e){
            log.error("Falha ao ler arquivo: {}", nomeArquivo);
            return null;
        }
    }

    private static void gerarArquivo(String nomeArquivo, String texto){
        try{
            File file = new File(nomeArquivo);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                FileOutputStream writer = new FileOutputStream(nomeArquivo);
                writer.write(texto.getBytes());
                writer.close();
            }

            log.info("Arquivo criado: {}", nomeArquivo);
        }catch (Exception e){
            log.error("Falha ao criar arquivo: {}", nomeArquivo);
        }
    }

    public static Integer calcularRepeticoesAno(ResultadosEnum resultado, Integer primeiroAno){
        LocalDate primeiroAnoDate = LocalDate.of(primeiroAno, 1, 1);
        LocalDate ultimoAnoDate = LocalDate.of(primeiroAno+1, 1, 1);
        List<NumeroRecorrenteDto> numeroRecorrenteUltimoAno = ApostaUtils
                .buscarNumerosRecorrentesPorData(resultado, primeiroAnoDate, ultimoAnoDate);

        AtomicInteger repeticoes = new AtomicInteger(0);
        numeroRecorrenteUltimoAno.forEach(numero -> {
            repeticoes.addAndGet(numero.getRepeticoes());
        });
//        log.info("Ano={}, Repeticoes={}", primeiroAnoDate.getYear(), repeticoes.get());

        return repeticoes.get();
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
                String date  = jsonReader.nextString();
                try{
                    return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }catch (Exception e){
                    return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
            }
            return null;
        }
    }

    private static String converterParaMoeda(BigDecimal valor){
        String valorConvertido =  valor.toString();
        return  "R$ " + valorConvertido;
    }
}
