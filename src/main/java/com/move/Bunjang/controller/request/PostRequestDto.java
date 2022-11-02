package com.move.Bunjang.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class PostRequestDto {
    private String title;
    private String category; // 변경
    private String local;
    private String state;
    private String trade;
    private String price; // 변경
    private String content;
    private String tag;
    private String amount; // 변경
    private String img_id; // 이미지의 아이디값
    private String jjimhagiCount; // 찜하기 카운트 2022-11-02 doosan add

}
