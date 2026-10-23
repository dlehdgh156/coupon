package com.coupon.service;

import com.coupon.domain.CouponEvent;
import com.coupon.repository.CouponEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponEventService {

    private final CouponEventRepository couponEventRepository;

    public CouponEvent create(String name, String description, int totalQuantity) {
        CouponEvent event = new CouponEvent(name, description, totalQuantity);
        return couponEventRepository.save(event);
    }

    public List<CouponEvent> search(String keyword) {
        return couponEventRepository.findByNameContaining(keyword);
    }

    public List<CouponEvent> searchByDescription(String keyword) {
        return couponEventRepository.findByDescriptionContaining(keyword);
    }

    public Iterable<CouponEvent> findAll() {
        return couponEventRepository.findAll();
    }
}
