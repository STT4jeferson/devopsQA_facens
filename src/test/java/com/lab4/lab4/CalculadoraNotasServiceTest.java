package com.lab4.lab4;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários que seguem os BDDs fornecidos.
 * Os testes usam a implementação stub de CalculadoraNotasService e devem falhar.
 */
class CalculadoraNotasServiceTest {

    private CalculadoraNotasService service;

    @BeforeEach
    void setUp() {
        service = new CalculadoraNotasService();
    }

    @Test
    void bdd1_simularNotaRecalculaMinimosEStatus() {
        // Arrange
        Map<String, Double> weights = new HashMap<>();
        weights.put("atividade1", 0.3);
        weights.put("atividade2", 0.3);
        weights.put("atividade3", 0.4);

        Map<String, Double> current = new HashMap<>();
        // Action: simula nota 8.0 na atividade1
        CalculadoraNotasService.Result r = service.simulateGrade("atividade1", 8.0, weights, current);

        // Assert: segundo o BDD, a calculadora deve mostrar a nota mínima necessária para média final 7
        // e indicar status alcançável ou inviável. Como estamos com stub, esperamos que o test falhe
        // (por exemplo, required deve ser > 0). Aqui colocamos uma asserção que será falsa com o stub.
        assertTrue(r.getRequired() > 0.0, "Esperava required > 0.0 para que o teste de aceitação passe (stub atual retorna 0.0)");

        // As notas mínimas das demais atividades devem ser recalculadas (não nulas)
        assertNotNull(r.getOtherMinimums(), "Mapa de mínimos não deve ser nulo");
        assertFalse(r.getOtherMinimums().isEmpty(), "Mapa de mínimos deve conter entradas");
    }

    @Test
    void bdd2_requiredGreaterThan10IsInviavel() {
        // Arrange: construir um cenário onde o required deveria ser > 10
        Map<String, Double> weights = new HashMap<>();
        weights.put("atividade1", 0.1);
        weights.put("atividade2", 0.1);
        weights.put("atividade3", 0.8);

        Map<String, Double> current = new HashMap<>();
        // Action: simula nota 0.0 na atividade1 — espera-se que required > 10 (mas stub não fará isso)
        CalculadoraNotasService.Result r = service.simulateGrade("atividade1", 0.0, weights, current);

        // Assert: quando required > 10, status deve ser "inviável"
        assertTrue(r.getRequired() > 10.0, "Esperava required > 10.0 conforme o BDD (stub retorna 0.0)");
        assertEquals("inviável", r.getStatus(), "Status deve ser 'inviável' quando required > 10");
    }
}
