package com.move.Bunjang.controller.response;

import com.move.Bunjang.domain.Category;
import com.move.Bunjang.domain.Media;
import com.move.Bunjang.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
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
    private Media media;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Page<PostResponseDto> toDtoList(Page<Post> postList) {
        Page<PostResponseDto> ResponsePostList = postList.map(m -> PostResponseDto.builder()
                //String value = (String) map.get("value"); -> class com.move.Bunjang.domain.Post cannot be cast to class java.util.Optional 오류 발생
                //String value = String.valueOf(map.get("value")); -> valueOf 써서
                // 캐스팅 변환이 아닌 String 클래스의 valueOf(Object) 로

                .id(m.getId())
                .title(m.getTitle())
                .author(m.getAuthor())
                .category(m.getCategory())
                .local(m.getLocal())
                .state(m.getState())
                .trade(m.getTrade())
                .price(m.getPrice())
                .content(m.getContent())
                .tag(m.getTag())
                .amount(m.getAmount())
                .createdAt(m.getCreatedAt())
                .modifiedAt(m.getModifiedAt())
                .build()
        );

        for(PostResponseDto postResponseDto : ResponsePostList){
            System.out.println("확인2222222 : " + postResponseDto.content);
            System.out.println("확인2222222 : " + postResponseDto.title);
            System.out.println("확인2222222 : " + postResponseDto.author);
            System.out.println();
        }

        return ResponsePostList;
    }
}
