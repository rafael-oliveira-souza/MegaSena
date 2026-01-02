package com.megasena.aposta.dtos;

import com.megasena.aposta.enums.FrequenciaRepeticaoEnum;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApostaDto {
    private FrequenciaRepeticaoEnum frequencia;
    private List<Integer> aposta = new LinkedList<>();
}
