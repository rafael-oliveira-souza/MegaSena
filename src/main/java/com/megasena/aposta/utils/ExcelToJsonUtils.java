package com.megasena.aposta.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megasena.aposta.dtos.SorteioDto;
import com.megasena.aposta.enums.ResultadosEnum;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.megasena.aposta.utils.ApostaUtils.getGson;

public class ExcelToJsonUtils {

    public static List<SorteioDto> lerExcelEGerarJson(ResultadosEnum resultadosEnum) throws Exception {
        try (FileInputStream fis = new FileInputStream(resultadosEnum.getPath());
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            int headerRowIndex = -1;
            Map<String, Integer> colunas = new HashMap<>();
            int qtdNumeros = resultadosEnum.getValores()
                    .keySet()
                    .stream()
                    .sorted()
                    .findFirst()
                    .orElse(0);

            // 1️⃣ Localiza a linha do cabeçalho
            for (Row row : sheet) {
                colunas.clear();

                for (Cell cell : row) {
                    String valor = cell.getStringCellValue().trim().toLowerCase();
                    colunas.put(valor, cell.getColumnIndex());
                }

                HashSet<String> colunasExcel = new HashSet<>();
                colunasExcel.add("concurso");
                colunasExcel.add("data");

                for (int i = 1; i <= qtdNumeros; i++) {
                    colunasExcel.add("bola " + i);
                }

                if (colunas.keySet().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet())
                        .containsAll(colunasExcel)) {
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
                int concurso = row.getCell(colunas.get("concurso")).getCellType() == CellType.NUMERIC ?
                        (int) row.getCell(colunas.get("concurso")).getNumericCellValue() :
                        Integer.parseInt(row.getCell(colunas.get("concurso")).getStringCellValue());

                Cell dataCell = row.getCell(colunas.get("data"));
                String data = dataCell.getCellType() == CellType.NUMERIC
                        ? sdf.format(dataCell.getDateCellValue())
                        : dataCell.getStringCellValue();

                json.put("concurso", concurso);
                json.put("data", data);

                ArrayNode resultados = mapper.createArrayNode();
                for (int b = 1; b <= qtdNumeros; b++) {
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
