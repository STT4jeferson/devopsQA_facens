package com.lab4.lab4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacaoDTO {
    private String tipo;
    private Double valor1;
    private Double valor2;
    private Double resultado;
}
