package com.lab4.lab4.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * Entidade Operacao representa uma operação de cálculo de notas.
 * 
 * Por que usar getters, setters, construtor, toString, hashCode/equals?
 * - Getters/Setters: Encapsulam o acesso aos atributos, permitindo validações e controle.
 * - Construtor: Garante a criação correta da entidade.
 * - toString: Facilita o debug e logging.
 * - hashCode/equals: Necessários para comparar entidades e usar em coleções.
 * Lombok gera tudo automaticamente, reduzindo boilerplate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operacao {
	private Long id;
	private String tipo;
	private Double valor1;
	private Double valor2;
	private Double resultado;
}
