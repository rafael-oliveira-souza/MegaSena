package com.megasena.aposta.enums;

public enum FrequenciaRepeticaoEnum {
    MAX,
    MIN,
    MID,
    RANDOM;

    public static FrequenciaRepeticaoEnum getByOrdinal(int num) {
        for (FrequenciaRepeticaoEnum freq : FrequenciaRepeticaoEnum.values()) {
            if (freq.ordinal() == num) {
                return freq;
            }
        }

        throw new IllegalArgumentException("Invalid ordinal " + num);
    }
}
