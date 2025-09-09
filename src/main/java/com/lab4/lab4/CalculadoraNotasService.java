package com.lab4.lab4;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço responsável por calcular as notas mínimas necessárias para alcançar média 7.
 * IMPLEMENTAÇÃO: stub (intencionalmente incompleta) — usado para criar testes que falhem.
 */
public class CalculadoraNotasService {

    public static class Result {
        private final double required;
        private final String status;
        private final Map<String, Double> otherMinimums;

        public Result(double required, String status, Map<String, Double> otherMinimums) {
            this.required = required;
            this.status = status;
            this.otherMinimums = otherMinimums;
        }

        public double getRequired() {
            return required;
        }

        public String getStatus() {
            return status;
        }

        public Map<String, Double> getOtherMinimums() {
            return otherMinimums;
        }
    }

    /**
     * Simula a atribuição de uma nota em uma atividade e recalcula as notas mínimas
     * necessárias nas demais atividades para alcançar média final 7.
     *
     * OBS: Esta é uma implementação stub que retorna valores fixos (intencionalmente
     * incorretos) para que os testes de especificação falhem até que a lógica seja
     * implementada corretamente.
     *
     * @param activityName nome da atividade simulada
     * @param grade nota simulada para a atividade
     * @param weights mapa atividade -> peso (soma deve ser 1.0)
     * @param currentGrades mapa atividade -> nota atual (pode estar vazio para atividade não corrigida)
     * @return Result contendo um required, status e mapa com mínimo das demais atividades
     */
    public Result simulateGrade(String activityName,
                                double grade,
                                Map<String, Double> weights,
                                Map<String, Double> currentGrades) {
        // Stub: retorna valores fixos e simples para compilar — não é a lógica real.
        Map<String, Double> mins = new HashMap<>();
        for (String k : weights.keySet()) {
            mins.put(k, 0.0);
        }

        // Sempre retorna "alcançável" e required 0.0 — isso tornará os testes de aceitação
        // e unidade falhos até implementarmos a lógica correta.
        return new Result(0.0, "alcançável", mins);
    }

}
