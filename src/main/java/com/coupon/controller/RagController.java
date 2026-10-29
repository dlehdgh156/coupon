package com.coupon.controller;

import com.coupon.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class RagController {

    private final RagService ragService;

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = ragService.chat(question);
        return ResponseEntity.ok(Map.of("answer", answer));
    }
}
