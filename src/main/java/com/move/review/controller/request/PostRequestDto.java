package com.move.review.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class PostRequestDto {
    private String title;
    private String category;
    private String local;
    private String state;
    private String trade;
    private int price;
    private String content;
    private String tag;
    private int amount;
}
