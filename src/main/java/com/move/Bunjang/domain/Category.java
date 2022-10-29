package com.move.Bunjang.domain;

// 카테고리 enum으로 변경 2022 - 10 -29 오후 10시26분 수정
public enum Category {

    여성의류( 1),
    남성의류(2),
    신발(3),
    가방(4),
    시계쥬얼리(5),
    패션액세서리(6),
    디지털가전(7);

    private int num;
//    private String parts;

    Category(int num ) {
        this.num = num ;
//        this.parts = parts;
    }
    public static String partsValue(int num) {
        switch (num) {
            case 1:
                return "여성의류";
            case 2:
                return "남성의류";
            case 3:
                return "신발";
            case 4:
                return "가방";
            case 5:
                return "시계쥬얼리";
            case 6:
                return "패션액세서리";
            case 7:
                return "디지털가전";
        }
        return null;
    }


}
