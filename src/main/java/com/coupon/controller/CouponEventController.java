package com.coupon.controller;

import com.coupon.domain.CouponEvent;
import com.coupon.service.CouponEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class CouponEventController {

    private final CouponEventService couponEventService;

    @PostMapping
    public ResponseEntity<CouponEvent> create(@RequestParam String name,
                                               @RequestParam String description,
                                               @RequestParam int totalQuantity) {
        CouponEvent event = couponEventService.create(name, description, totalQuantity);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CouponEvent>> search(@RequestParam String keyword) {
        List<CouponEvent> results = couponEventService.search(keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping
    public ResponseEntity<Iterable<CouponEvent>> findAll() {
        return ResponseEntity.ok(couponEventService.findAll());
    }
}
