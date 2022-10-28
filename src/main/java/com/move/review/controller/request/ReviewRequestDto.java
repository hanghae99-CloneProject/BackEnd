package com.move.review.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
  private String image;
  private String movieTitle;
  private String genre;
  private int rating;
  private String reviewTitle;
  private String reviewContent;
}
