package com.move.Bunjang.controller;

import com.move.Bunjang.configuration.SwaggerAnnotation;
import com.move.Bunjang.controller.request.LoginRequestDto;
import com.move.Bunjang.controller.request.MemberRequestDto;
import com.move.Bunjang.exception.PrivateResponseBody;
import com.move.Bunjang.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;

  //회원 가입 API
  @PostMapping(value = "/bunjang/signup")
  public ResponseEntity<PrivateResponseBody> signup(@RequestBody MemberRequestDto requestDto) {
    return memberService.createMember(requestDto);
  }

  //로그인 API
  @PostMapping(value = "/bunjang/login")
  public ResponseEntity<PrivateResponseBody> login(@RequestBody LoginRequestDto requestDto,
                                                   HttpServletResponse response) {
    return memberService.login(requestDto, response);
  }

  //로그아웃 API
  @SwaggerAnnotation
  @PostMapping(value = "/bunjang/logout")
  public ResponseEntity<PrivateResponseBody> logout(HttpServletRequest request) {
    return memberService.logout(request);
  }
}
