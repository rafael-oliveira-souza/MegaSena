package com.megasena.aposta.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megasena.aposta.dtos.SorteioDto;
import com.megasena.aposta.enums.ResultadosEnum;
import com.megasena.aposta.utils.ExcelToJsonUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.megasena.aposta.utils.ApostaUtils.getGson;
import static com.megasena.aposta.utils.ApostaUtils.lerArquivo;

@Service
public class SorteioService {

    public List<SorteioDto> carregarSorteios(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, SorteioDto.class));
    }


    public List<SorteioDto> carregarSorteios(ResultadosEnum resultado) {
        try {
            return ExcelToJsonUtils.lerExcelEGerarJson(resultado.getPath());
        } catch (Exception e) {
            throw new RuntimeException("Falha ao ler arquivo de resultados " + e.getMessage());
        }
    }

}
