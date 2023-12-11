package com.megasena.aposta.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SorteioDto {
    private LocalDate data;
    private Integer concurso;
    private List<Integer> resultados = new LinkedList<>();
}
