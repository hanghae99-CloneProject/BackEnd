package com.move.review.domain;

import io.jsonwebtoken.lang.Assert;

// 카테고리 enum으로 변경 2022 - 10 -29 오후 10시26분 수정
public enum Category {

    여성의류( 1, "여성의류"),
    남성의류(2,"남성의류"),
    신발(3, "신발"),
    가방(4, "가방"),
    시계쥬얼리(5,"시계쥬얼리"),
    패션액세서리(6,"패션액세서리"),
    디지털가전(7,"디지털가전");

    private int num;
    private String parts;

    Category(int num , String parts) {
        this.num = num ;
        this.parts = parts;
    }


}
