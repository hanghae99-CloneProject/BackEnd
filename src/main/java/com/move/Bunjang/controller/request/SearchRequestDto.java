package com.move.Bunjang.controller.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class SearchRequestDto {
    private String keyword;
    private String type;
    private int page;
}
