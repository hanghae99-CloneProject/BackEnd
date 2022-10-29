package com.move.Bunjang.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class PostRequestDto {
    private String title;
    private int category;
    private String local;
    private String state;
    private String trade;
    private int price;
    private String content;
    private String tag;
    private int amount;
}
