package com.move.review.controller;

import com.move.review.configuration.SwaggerAnnotation;
import com.move.review.controller.request.ReviewRequestDto;
import com.move.review.controller.response.ResponseDto;
import com.move.review.domain.UserDetailsImpl;
import com.move.review.service.ReviewService;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReviewController {

  private final ReviewService reviewService;

  //Review 생성

  @SwaggerAnnotation
  @PostMapping(value = "/api/reviews")
  public ResponseEntity<?> createReview(@RequestBody ReviewRequestDto requestDto,
                                        HttpServletRequest request) {
    return reviewService.createReview(requestDto, request);
  }

  //전체 Review 조회
  @SwaggerAnnotation
  @GetMapping(value = "/api/reviews")
  public ResponseDto<?> getAllReview() {
    return reviewService.getAllReview();
  }

  //Review 상세 조회
  @SwaggerAnnotation
  @GetMapping(value = "/api/reviews/{reviewId}")
  public ResponseDto<?> getReview(@PathVariable Long reviewId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return reviewService.getReview(reviewId, userDetails);
  }

  //Review 업데이트
  @SwaggerAnnotation
  @PutMapping(value = "/api/reviews/{reviewId}")
  public ResponseDto<?> updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequestDto reviewRequestDto,
      HttpServletRequest request) {
    return reviewService.updateReview(reviewId, reviewRequestDto, request);
  }

  //Review 삭제
  @SwaggerAnnotation
  @DeleteMapping(value = "/api/reviews/{reviewId}")
  public ResponseDto<?> deleteReview(@PathVariable Long reviewId,
      HttpServletRequest request) {
    return reviewService.deleteReview(reviewId, request);
  }

}
