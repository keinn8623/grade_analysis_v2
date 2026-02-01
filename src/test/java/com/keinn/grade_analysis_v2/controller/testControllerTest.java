package com.keinn.grade_analysis_v2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class testControllerTest {
    @Test
    void test() {

    }

    @Test
    void testConcurrentHttpAccess() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        HashMap<String, List<String>> stuInfo1 = new HashMap<>();
        stuInfo1.put("张三1", Arrays.asList("1", "2", "4"));

        HashMap<String, List<String>> stuInfo2 = new HashMap<>();
        stuInfo2.put("李四2", Arrays.asList("2", "3", "7"));
        Runnable requestTask = () -> {
            try {
                // 发送HTTP请求到test接口
                io.restassured.RestAssured.given()
                        .param("stuInfo", stuInfo1)
                        .when()
                        .get("api/test")  // 根据实际接口路径调整
                        .then()
                        .statusCode(200);
            } finally {
                latch.countDown();
            }
        };
        Runnable requestTask1 = () -> {
            try {
                // 发送HTTP请求到test接口
                io.restassured.RestAssured.given()
                        .param("stuInfo", stuInfo2)
                        .when()
                        .get("api/test")  // 根据实际接口路径调整
                        .then()
                        .statusCode(200);
            } finally {
                latch.countDown();
            }
        };

        executor.submit(requestTask);
        executor.submit(requestTask1);

        latch.await();
        executor.shutdown();
    }

}