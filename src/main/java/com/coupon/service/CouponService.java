package com.coupon.service;

import com.coupon.domain.CouponIssue;
import com.coupon.repository.CouponIssueRepository;
import com.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void issue(Long couponId, Long userId) {
        String stockKey = "coupon:stock:" + couponId;
        String issueKey = "coupon:issued:" + couponId;

        // Lua Script 실행
        RedisScript<Long> script = RedisScript.of(new
                ClassPathResource("coupon_issue.lua"), Long.class);
        Long result = redisTemplate.execute(script, List.of(stockKey,
                issueKey), userId.toString());

        if (result == -1) {
            throw new IllegalStateException("이미 발급된 쿠폰입니다.");
        }
        if (result == -2) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }

        // DB 재고 차감 + 발급 저장
        couponRepository.decreaseStock(couponId);
        couponIssueRepository.save(new CouponIssue(couponId, userId));
    }
}

