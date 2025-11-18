package com.lab4.lab4.service;

import com.lab4.lab4.dto.OperacaoDTO;
import com.lab4.lab4.entity.Operacao;
import com.lab4.lab4.repository.OperacaoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.HashMap;

class CalculadoraNotasServiceTest {
    @Test
    void testSimulateGradeAlcancavel() {
        CalculadoraNotasService service = new CalculadoraNotasService();
        Map<String, Double> weights = new HashMap<>();
        weights.put("A1", 0.4);
        weights.put("A2", 0.6);
        Map<String, Double> currentGrades = new HashMap<>();
        currentGrades.put("A2", 8.0);
        CalculadoraNotasService.Result result = service.simulateGrade("A1", 7.0, weights, currentGrades);
        assertEquals("alcançável", result.getStatus());
        assertTrue(result.getOtherMinimums().containsKey("A1"));
    }

    @Test
    void testSimulateGradeInviavel() {
        CalculadoraNotasService service = new CalculadoraNotasService();
        Map<String, Double> weights = new HashMap<>();
        weights.put("A1", 0.5);
        weights.put("A2", 0.5);
        Map<String, Double> currentGrades = new HashMap<>();
        currentGrades.put("A2", 2.0);
        CalculadoraNotasService.Result result = service.simulateGrade("A1", 2.0, weights, currentGrades);
        assertEquals("inviável", result.getStatus());
    }

    @Test
    void testSimulateGradeWeightsEmpty() {
        CalculadoraNotasService service = new CalculadoraNotasService();
        Map<String, Double> weights = new HashMap<>();
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            service.simulateGrade("A1", 7.0, weights, null)
        );
        assertEquals("weights must not be null or empty", exception.getMessage());
    }

    @Test
    void testResultGetters() {
        Map<String, Double> mins = new HashMap<>();
        mins.put("A1", 5.0);
        CalculadoraNotasService.Result result = new CalculadoraNotasService.Result(5.0, "alcançável", mins);
        assertEquals(5.0, result.getRequired());
        assertEquals("alcançável", result.getStatus());
        assertEquals(mins, result.getOtherMinimums());
    }
    @Mock
    private OperacaoRepository operacaoRepository;

    @InjectMocks
    private CalculadoraNotasService service;

    public CalculadoraNotasServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalcularSoma() {
        OperacaoDTO dto = OperacaoDTO.builder().tipo("soma").valor1(2.0).valor2(3.0).build();
        when(operacaoRepository.save(any(Operacao.class))).thenReturn(new Operacao());
        OperacaoDTO result = service.calcular(dto);
        assertEquals(5.0, result.getResultado());
    }

    @Test
    void testCalcularDivisaoPorZero() {
        OperacaoDTO dto = OperacaoDTO.builder().tipo("divisao").valor1(10.0).valor2(0.0).build();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.calcular(dto));
        assertEquals("Divisão por zero não é permitida", exception.getMessage());
    }

    @Test
    void testCalcularDivisaoNormal() {
        OperacaoDTO dto = OperacaoDTO.builder().tipo("divisao").valor1(10.0).valor2(2.0).build();
        when(operacaoRepository.save(any(Operacao.class))).thenReturn(new Operacao());
        OperacaoDTO result = service.calcular(dto);
        assertEquals(5.0, result.getResultado());
    }

    @Test
    void testCalcularTipoInvalido() {
        OperacaoDTO dto = OperacaoDTO.builder().tipo("potencia").valor1(2.0).valor2(3.0).build();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.calcular(dto));
        assertEquals("Tipo de operação inválido", exception.getMessage());
    }

    @Test
    void testBuscarPorId() {
        Operacao operacao = Operacao.builder().tipo("soma").valor1(1.0).valor2(2.0).resultado(3.0).build();
        when(operacaoRepository.findById(1L)).thenReturn(Optional.of(operacao));
        Optional<Operacao> result = service.buscarPorId(1L);
        assertTrue(result.isPresent());
        assertEquals(3.0, result.get().getResultado());
    }
}
