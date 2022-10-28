package com.move.review.controller;

import com.move.review.configuration.SwaggerAnnotation;
import com.move.review.controller.request.IdCheckRequestDto;
import com.move.review.controller.request.LoginRequestDto;
import com.move.review.controller.request.MemberRequestDto;
import com.move.review.controller.response.ResponseDto;
import com.move.review.service.MemberService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;

  @PostMapping(value = "/api/member/signup")
  public ResponseDto<?> signup(@RequestBody MemberRequestDto requestDto) {
    return memberService.createMember(requestDto);
  }

  @PostMapping(value = "/api/member/login")
  public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto,
      HttpServletResponse response
  ) {
    return memberService.login(requestDto, response);
  }

  @PostMapping(value = "/api/member/id-check")
  public ResponseDto<?> idCheck(@RequestBody IdCheckRequestDto requestDto){
    return memberService.idCheck(requestDto);
  }

  @SwaggerAnnotation
  @PostMapping(value = "/api/member/logout")
  public ResponseDto<?> logout(HttpServletRequest request) {
    return memberService.logout(request);
  }
}
