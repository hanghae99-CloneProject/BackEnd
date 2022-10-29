package com.move.Bunjang.controller.response;

import com.move.Bunjang.domain.Category;
import com.move.Bunjang.domain.Media;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String author;
    private Category category;
    private String local;
    private String state;
    private String trade;
    private int price;
    private String content;
    private String tag;
    private int amount;
    private List<Media> medias;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
//    private String whoCreated;
//    private String whoUpdated;
}