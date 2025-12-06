package com.lab4.calculator.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab4.calculator.messaging.AsyncTaskMessage;
import com.lab4.calculator.messaging.MessageProducer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AsyncController.class)
class AsyncControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageProducer producer;

    @Test
    void publishShouldAssignIdSendMessageAndReturnCreated() throws Exception {
        AsyncTaskMessage body = new AsyncTaskMessage();
        body.setTaskType("REPORT");
        body.setPayload("{\"foo\":\"bar\"}");

        mockMvc.perform(post("/api/v1/async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/v1/async/")))
                .andExpect(jsonPath("$.id", not(blankOrNullString())))
                .andExpect(jsonPath("$.taskType").value("REPORT"))
                .andExpect(jsonPath("$.payload").value("{\"foo\":\"bar\"}"));

        ArgumentCaptor<AsyncTaskMessage> messageCaptor = ArgumentCaptor.forClass(AsyncTaskMessage.class);
        verify(producer).sendAsyncTask(messageCaptor.capture());
        AsyncTaskMessage sent = messageCaptor.getValue();
        assertThat(sent.getId()).isNotBlank();
        assertThat(sent.getTaskType()).isEqualTo("REPORT");
        assertThat(sent.getPayload()).isEqualTo("{\"foo\":\"bar\"}");
    }

    @Test
    void publishShouldKeepProvidedId() throws Exception {
        AsyncTaskMessage body = new AsyncTaskMessage();
        body.setId("fixed-id");
        body.setTaskType("EXPORT");

        mockMvc.perform(post("/api/v1/async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/async/fixed-id"))
                .andExpect(jsonPath("$.id").value("fixed-id"))
                .andExpect(jsonPath("$.taskType").value("EXPORT"));

        ArgumentCaptor<AsyncTaskMessage> messageCaptor = ArgumentCaptor.forClass(AsyncTaskMessage.class);
        verify(producer).sendAsyncTask(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getId()).isEqualTo("fixed-id");
    }
}
