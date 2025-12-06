package com.lab4.calculator.web;

import com.lab4.calculator.messaging.AsyncTaskMessage;
import com.lab4.calculator.messaging.MessageProducer;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/async")
@RequiredArgsConstructor
public class AsyncController {

    private final MessageProducer producer;

    @PostMapping
    public ResponseEntity<AsyncTaskMessage> publish(@RequestBody AsyncTaskMessage message) {
        if (message.getId() == null || message.getId().isBlank()) {
            message.setId(UUID.randomUUID().toString());
        }
        producer.sendAsyncTask(message);
        return ResponseEntity.created(URI.create("/api/v1/async/" + message.getId())).body(message);
    }
}
