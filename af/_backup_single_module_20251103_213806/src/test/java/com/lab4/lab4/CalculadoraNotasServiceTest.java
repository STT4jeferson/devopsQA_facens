package com.lab4.lab4;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lab4.lab4.service.CalculadoraNotasService;


class CalculadoraNotasServiceTest {

    private CalculadoraNotasService service;

    @BeforeEach
    void setUp() {
        service = new CalculadoraNotasService();
    }

    @Test
    void bdd1_simularNotaRecalculaMinimosEStatus() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("atividade1", 0.3);
        weights.put("atividade2", 0.3);
        weights.put("atividade3", 0.4);

        Map<String, Double> current = new HashMap<>();
    
        CalculadoraNotasService.Result r = service.simulateGrade("atividade1", 8.0, weights, current);
    
        assertTrue(r.getRequired() > 0.0, "Esperava required > 0.0 para que o teste de aceitação passe (stub atual retorna 0.0)");
    
        assertNotNull(r.getOtherMinimums(), "Mapa de mínimos não deve ser nulo");
        assertFalse(r.getOtherMinimums().isEmpty(), "Mapa de mínimos deve conter entradas");
    }

    @Test
    void bdd2_requiredGreaterThan10IsInviavel() {
    
        Map<String, Double> weights = new HashMap<>();
        weights.put("atividade1", 0.1);
        weights.put("atividade2", 0.1);
        weights.put("atividade3", 0.8);

        Map<String, Double> current = new HashMap<>();
    
        CalculadoraNotasService.Result r = service.simulateGrade("atividade1", 0.0, weights, current);

        assertTrue(r.getRequired() > 10.0, "Esperava required > 10.0 conforme o BDD (stub retorna 0.0)");
        assertEquals("inviável", r.getStatus(), "Status deve ser 'inviável' quando required > 10");
    }

    @Test
    void whenWeightZero_thenRequiredIsInfinity() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("a1", 0.0);
        weights.put("a2", 1.0);

        Map<String, Double> current = new HashMap<>();
        CalculadoraNotasService.Result r = service.simulateGrade("a1", 5.0, weights, current);

        assertTrue(Double.isInfinite(r.getOtherMinimums().get("a1")), "Expected Infinity for weight 0");
    }

    @Test
    void whenActivityNotInWeights_thenRequiredIsInfinity() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("a1", 0.5);
        weights.put("a2", 0.5);

        Map<String, Double> current = new HashMap<>();
        CalculadoraNotasService.Result r = service.simulateGrade("missing", 5.0, weights, current);

        assertTrue(Double.isInfinite(r.getRequired()), "Expected Infinity when activity not present in weights");
    }

    @Test
    void boundary_requiredEqualsTen_isAlcançavel() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("a1", 0.1);
        weights.put("a2", 0.9);

        Map<String, Double> current = new HashMap<>();
    
        current.put("a2", 6.0/0.9);

        CalculadoraNotasService.Result r = service.simulateGrade("a1", 0.0, weights, current);

        assertEquals(10.0, r.getRequired(), 1e-9, "Expected required exactly 10");
        assertEquals("alcançável", r.getStatus(), "Boundary 10 should be alcançável (not >10)");
    }

    @Test
    void negativeGrades_areHandled() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("a1", 0.5);
        weights.put("a2", 0.5);

        Map<String, Double> current = new HashMap<>();
        current.put("a2", -1.0);

        CalculadoraNotasService.Result r = service.simulateGrade("a1", 5.0, weights, current);

    
        assertNotNull(r);
        assertTrue(!Double.isNaN(r.getRequired()), "Required should be a number");
    }
}
