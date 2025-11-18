package com.lab4.lab4.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab4.lab4.dto.OperacaoDTO;
import com.lab4.lab4.service.CalculadoraNotasService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest
class OperacaoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalculadoraNotasService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCalcular() throws Exception {
        OperacaoDTO dto = OperacaoDTO.builder().tipo("soma").valor1(2.0).valor2(3.0).build();
        OperacaoDTO result = OperacaoDTO.builder().tipo("soma").valor1(2.0).valor2(3.0).resultado(5.0).build();
        when(service.calcular(any(OperacaoDTO.class))).thenReturn(result);
        mockMvc.perform(post("/api/operacoes/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value(5.0));
    }
}
