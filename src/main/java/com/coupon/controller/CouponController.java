package com.coupon.controller;

import com.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/{couponId}/issue")
    public ResponseEntity issue(@PathVariable Long couponId, @RequestParam Long userId) {
        couponService.issue(couponId,userId);

        return ResponseEntity.ok("쿠폰 발급 완료");
    }


}
