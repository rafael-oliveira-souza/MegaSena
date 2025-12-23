package com.megasena.aposta.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megasena.aposta.dtos.SorteioDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.megasena.aposta.utils.ApostaUtils.getGson;

public class ExcelToJsonUtils {

    public static List<SorteioDto> lerExcelEGerarJson(String pathFile) throws Exception {
        try (FileInputStream fis = new FileInputStream(pathFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            int headerRowIndex = -1;
            Map<String, Integer> colunas = new HashMap<>();

            // 1️⃣ Localiza a linha do cabeçalho
            for (Row row : sheet) {
                colunas.clear();

                for (Cell cell : row) {
                    String valor = cell.getStringCellValue().trim().toLowerCase();
                    colunas.put(valor, cell.getColumnIndex());
                }

                if (colunas.keySet().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet())
                        .containsAll(Arrays.asList(
                                "concurso", "data",
                                "bola 1", "bola 2", "bola 3",
                                "bola 4", "bola 5", "bola 6"
                        ))) {
                    headerRowIndex = row.getRowNum();
                    break;
                }
            }

            if (headerRowIndex == -1) {
                throw new RuntimeException("Cabeçalho não encontrado no Excel");
            }

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayJson = mapper.createArrayNode();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // 2️⃣ Lê todas as linhas abaixo do cabeçalho
            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                ObjectNode json = mapper.createObjectNode();

                int concurso = (int) row.getCell(colunas.get("concurso")).getNumericCellValue();

                Cell dataCell = row.getCell(colunas.get("data"));
                String data = dataCell.getCellType() == CellType.NUMERIC
                        ? sdf.format(dataCell.getDateCellValue())
                        : dataCell.getStringCellValue();

                json.put("concurso", concurso);
                json.put("data", data);

                ArrayNode resultados = mapper.createArrayNode();
                for (int b = 1; b <= 6; b++) {
                    resultados.add((int) row.getCell(colunas.get("bola " + b)).getNumericCellValue());
                }

                json.set("resultados", resultados);
                arrayJson.add(json);
            }

            String resultados = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(arrayJson);
            Gson gson = getGson();
            return gson.fromJson(resultados, new TypeToken<List<SorteioDto>>() {
            }.getType());
        }
    }
}
