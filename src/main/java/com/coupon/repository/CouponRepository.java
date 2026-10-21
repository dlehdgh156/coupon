package com.coupon.repository;

import com.coupon.domain.Coupon;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Modifying
    @Query("UPDATE Coupon c SET c.remainQuantity = c.remainQuantity - 1 WHERE c.id= :id AND c.remainQuantity > 0")
    int decreaseStock(@Param("id") Long id);

}
