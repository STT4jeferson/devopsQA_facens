package com.lab4.calculator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GradeCalculatorApplicationTests {

    @Test
    void contextLoads() {
        GradeCalculatorApplication.main(new String[]{"--server.port=0"});
    }
}
