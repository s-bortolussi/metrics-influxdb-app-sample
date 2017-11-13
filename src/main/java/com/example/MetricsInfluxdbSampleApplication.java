package com.example;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class MetricsInfluxdbSampleApplication {

    @Value("${cloud.application.instance_id:app_01}")
    private String appInstanceId;

    public static void main(String[] args) {
        SpringApplication.run(MetricsInfluxdbSampleApplication.class, args);
    }

}

@RestController
class GreetingController {

    @GetMapping("/greeting")
    @Timed(quantiles = {0.5, 0.85, 0.95, 0.99})
    public String greeting(@RequestParam(value = "name", defaultValue = "world !") String name) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        latch.await(1, TimeUnit.SECONDS);
        if (new Random().nextInt(100) > 90) {
            throw new Exception("Error (random) while handling request.");
        }
        return String.format("hello %s", name);
    }

}
