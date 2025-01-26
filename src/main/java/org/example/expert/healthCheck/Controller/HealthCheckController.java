package org.example.expert.healthCheck.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public String healthCheck() {
        return "OK"; // 서버가 정상적으로 작동하면 "OK"를 반환
    }
}