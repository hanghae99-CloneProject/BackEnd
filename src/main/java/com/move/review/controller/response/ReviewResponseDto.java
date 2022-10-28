package com.move.review.controller.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
  private Long reviewId;
  private String image;
  private String movieTitle;
  private String genre;
  private int rating;
  private String reviewTitle;
  private String reviewContent;
  private String memberName;
  private Long heartNum;
  private Long commentNum;
  private boolean heartYn = false;
  private List<CommentResponseDto> commentResponseDtoList;
}
