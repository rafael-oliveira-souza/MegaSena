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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ApostaUtils {

    public static List<Integer> criarAposta(String sorteioPath,
                                            int qtdNumeros,
                                            FrequenciaRepeticaoEnum repeticaoEnum) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentes(sorteioPath);
        List<Integer> aposta = new LinkedList<>();

        if (FrequenciaRepeticaoEnum.MAX.equals(repeticaoEnum)) {
            for (int i = 0; i < qtdNumeros; i++) {
                Map.Entry<Integer, Integer> map = resultados
                        .entrySet()
                        .stream()
                        .max((o1, o2) -> Integer.compare(o1.getValue(), o2.getValue()))
                        .orElse(null);

                aposta.add(map.getKey());
                resultados.remove(map.getKey());
            }
        } else if (FrequenciaRepeticaoEnum.MIN.equals(repeticaoEnum)) {
            for (int i = 0; i < qtdNumeros; i++) {
                Map.Entry<Integer, Integer> map = resultados
                        .entrySet()
                        .stream()
                        .min((o1, o2) -> Integer.compare(o1.getValue(), o2.getValue()))
                        .orElse(null);

                aposta.add(map.getKey());
                resultados.remove(map.getKey());
            }
        } else if (FrequenciaRepeticaoEnum.RANDOM.equals(repeticaoEnum)) {
            for (int i = 0; i < qtdNumeros; i++) {
                int randomInt = gerarNumeroAleatorio(1, 60);
                if (!aposta.contains(randomInt)) {
                    resultados.remove(randomInt);
                    aposta.add(randomInt);
                }
            }
        } else {
            int mid = qtdNumeros / 2;
            aposta.addAll(ApostaUtils.criarAposta(sorteioPath, mid, FrequenciaRepeticaoEnum.MIN));
            aposta.addAll(ApostaUtils.criarAposta(sorteioPath, qtdNumeros - mid, FrequenciaRepeticaoEnum.MAX));
        }

        return aposta;
    }

    public static NumeroRecorrenteDto buscarNumeroMinimoRepeticoes(String sorteioPath) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentes(sorteioPath);

        return resultados
                .entrySet()
                .stream()
                .min((o1, o2) -> Integer.compare(o1.getValue(), o2.getValue()))
                .map(map -> new NumeroRecorrenteDto(map.getKey(), map.getValue()))
                .orElse(null);
    }

    public static NumeroRecorrenteDto buscarNumeroMaximoRepeticoes(String sorteioPath) {
        Map<Integer, Integer> resultados = buscaMapNumerosRecorrentes(sorteioPath);

        return resultados
                .entrySet()
                .stream()
                .max((o1, o2) -> Integer.compare(o1.getValue(), o2.getValue()))
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
            String resultados = Files.readString(new File(sorteioPath).toPath());
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                    .create();

            return gson.fromJson(resultados, new TypeToken<List<SorteioDto>>() {
            }.getType());
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar sorteios");
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
}
