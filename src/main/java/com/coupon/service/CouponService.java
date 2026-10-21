package com.coupon.service;

import com.coupon.domain.Coupon;
import com.coupon.domain.CouponIssue;
import com.coupon.repository.CouponIssueRepository;
import com.coupon.repository.CouponRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public CouponService(CouponRepository couponRepository,RedisTemplate<String,String> redisTemplate, CouponIssueRepository couponIssueRepository) {
        this.couponRepository = couponRepository;
        this.couponIssueRepository = couponIssueRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void issue(Long couponId, Long userId) {
        String stockKey = "coupon:stock:" + couponId;
        String issueKey = "coupon:issued:" + couponId;
        Coupon coupon = couponRepository.findById(couponId).orElse(null);

        // 1. 중복 발급 체크 (Redis Set)
        Boolean alreadyIssued = redisTemplate.opsForSet().isMember(issueKey,
                userId.toString());
        if (Boolean.TRUE.equals(alreadyIssued)) {
            throw new IllegalStateException("이미 발급된 쿠폰입니다.");
        }

        // 2. 발급 기록 (Redis Set에 추가)
        redisTemplate.opsForSet().add(issueKey, userId.toString());

        // 3. 재고 차감 (Redis DECR - 원자적)
        Long remain = redisTemplate.opsForValue().decrement(stockKey);
        if (remain == null || remain < 0) {
            redisTemplate.opsForValue().increment(stockKey); // 롤백
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }

        // 4. db재고 차감
        couponRepository.decreaseStock(couponId);

        // 5. DB 저장
        couponIssueRepository.save(new CouponIssue(couponId, userId));
    }



}
