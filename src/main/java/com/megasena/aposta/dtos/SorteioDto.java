package com.megasena.aposta.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SorteioDto {
    @EqualsAndHashCode.Include
    private Integer concurso;
    private LocalDate data;
    private List<Integer> resultados = new LinkedList<>();
}
