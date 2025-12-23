package com.megasena.aposta.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum ResultadosEnum {
    MEGA_SENA("src/main/resources/resultados/mega_resultados.xlsx", 60, Map.of(
            6, 6D,
            7, 42D,
            8, 168D,
            9, 504D,
            10, 1260D,
            11, 2772D,
            12, 5544D,
            13, 10296D,
            14, 18018D,
            15, 30030D
    )),
    QUINA("src/main/resources/resultados/quina_resultados.xlsx", 80, Map.of(
            5, 2.50,
            6, 5D,
            7, 35D,
            8, 140D,
            9, 420D,
            10, 1050D,
            11, 2350D,
            12, 4620D,
            13, 8580D,
            14, 15015D
    )),
    LOTO_FACIL("src/main/resources/resultados/lotofacil_resultados.xlsx", 25, Map.of(
            15, 3D,
            16, 48D,
            17,	408D,
            18,	2448D
    ));

    private String path;
    private Integer qtdNumeros;
    private Map<Integer, Double> valores;
}
