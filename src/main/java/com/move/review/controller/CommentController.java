package com.move.review.controller;

import com.move.review.controller.response.ResponseDto;
import com.move.review.controller.request.CommentRequestDto;
import com.move.review.domain.UserDetailsImpl;
import com.move.review.service.CommentService;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class CommentController {

  private final CommentService commentService;

  // 댓글 작성
  @PostMapping(value = "/comments")
  public ResponseDto<?> createComment(@RequestBody CommentRequestDto requestDto, HttpServletRequest request){
    return commentService.createComment(requestDto, request);
  }

  // 댓글 수정
  @PutMapping(value = "/comments/{commentId}")
  public ResponseDto<?> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDto requestDto,
                                      HttpServletRequest request) {
    return commentService.updateComment(commentId, requestDto, request);
  }

  // 댓글 삭제
  @DeleteMapping(value = "/comment/{commentId}")
  public ResponseDto<?> deleteComment(@PathVariable Long commentId,
                                      @AuthenticationPrincipal HttpServletRequest request) {
    return commentService.deleteComment(commentId, request);
  }
}
