package com.megasena.aposta.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultadosEnum {
    MEGA_SENA("src/main/resources/resultados/mega_sena_ate_concurso_2666.json", 60),
    LOTO_FACIL("src/main/resources/resultados/lotofacil_resultados.json", 25);

    private String path;
    private Integer qtdNumeros;
}
