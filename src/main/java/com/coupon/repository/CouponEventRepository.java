package com.coupon.repository;

import com.coupon.domain.CouponEvent;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CouponEventRepository extends ElasticsearchRepository<CouponEvent, String> {

    List<CouponEvent> findByNameContaining(String keyword);

    List<CouponEvent> findByDescriptionContaining(String keyword);
}
