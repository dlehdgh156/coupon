package com.coupon.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "coupon_events")
@Getter
@NoArgsConstructor
public class CouponEvent {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Integer)
    private int totalQuantity;

    public CouponEvent(String name, String description, int totalQuantity) {
        this.name = name;
        this.description = description;
        this.totalQuantity = totalQuantity;
    }
}
