package com.coupon.service;

import com.coupon.domain.CouponEvent;
import com.coupon.repository.CouponEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {

    private final CouponEventRepository couponEventRepository;

    @Value("${openai.api-key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com")
            .build();

    public String chat(String question) {
        // 1. ES에서 관련 쿠폰 검색
        List<CouponEvent> results = couponEventRepository.findByNameContaining(question);
        if (results.isEmpty()) {
            results = couponEventRepository.findByDescriptionContaining(question);
        }

        // 2. 검색 결과를 컨텍스트로 만들기
        String context;
        if (results.isEmpty()) {
            Iterable<CouponEvent> all = couponEventRepository.findAll();
            StringBuilder sb = new StringBuilder();
            all.forEach(e -> sb.append(formatEvent(e)));
            context = sb.toString();
        } else {
            context = results.stream()
                    .map(this::formatEvent)
                    .collect(Collectors.joining());
        }

        // 3. OpenAI API 호출
        String systemPrompt = String.format("""
                당신은 쿠폰 이벤트 안내 도우미입니다.
                아래 쿠폰 이벤트 정보를 바탕으로 사용자의 질문에 친절하게 답변해주세요.
                정보에 없는 내용은 추측하지 마세요.

                [쿠폰 이벤트 정보]
                %s
                """, context);

        return callOpenAI(systemPrompt, question);
    }

    private String formatEvent(CouponEvent event) {
        return String.format("- 쿠폰명: %s, 설명: %s, 총 수량: %d장\n",
                event.getName(), event.getDescription(), event.getTotalQuantity());
    }

    private String callOpenAI(String systemPrompt, String userMessage) {
        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "max_tokens", 1024,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                )
        );

        Map response = webClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}
