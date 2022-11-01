package com.move.Bunjang.service;

import com.move.Bunjang.controller.request.LoginRequestDto;
import com.move.Bunjang.controller.request.MemberRequestDto;
import com.move.Bunjang.controller.request.TokenDto;
import com.move.Bunjang.controller.response.MemberResponseDto;
import com.move.Bunjang.domain.Member;
import com.move.Bunjang.exception.PrivateResponseBody;
import com.move.Bunjang.exception.StatusCode;
import com.move.Bunjang.jwt.TokenProvider;
import com.move.Bunjang.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

  //회원가입
  @Transactional
  public ResponseEntity<PrivateResponseBody> createMember(MemberRequestDto requestDto) {
    // 아이디 중복 확인
    if (null != isPresentMember(requestDto.getEmail())) {
      return new ResponseEntity<>(new PrivateResponseBody
              (StatusCode.DUPLICATED_NICKNAME,null),HttpStatus.OK);
    }

    // 비밀번호 중복 확인
    if (!requestDto.getPw().equals(requestDto.getPwConfirm())) {
      return new ResponseEntity<>(new PrivateResponseBody
              (StatusCode.DUPLICATED_PASSWORD,null),HttpStatus.BAD_REQUEST);
    }

    // 회원 정보 저장
    Member member = Member.builder()
            .email(requestDto.getEmail())
                .pw(passwordEncoder.encode(requestDto.getPw()))
                    .build();
    memberRepository.save(member);

    // Message 및 Status를 Return
    return new ResponseEntity<>(new PrivateResponseBody
            (StatusCode.OK,"회원가입 성공"),HttpStatus.OK);
  }

  //로그인
  @Transactional
  public ResponseEntity<PrivateResponseBody> login(LoginRequestDto requestDto, HttpServletResponse response) {
    Member member = isPresentMember(requestDto.getEmail());

    // DB에 Email 확인
    if (null == member) {
      return new ResponseEntity<>(new PrivateResponseBody
              (StatusCode.LOGIN_MEMBER_ID_FAIL,null),HttpStatus.BAD_REQUEST);
    }

    // DB에 PW 확인
    if (!member.validatePassword(passwordEncoder, requestDto.getPw())) {
      return new ResponseEntity<>(new PrivateResponseBody
              (StatusCode.LOGIN_PASSWORD_FAIL,null),HttpStatus.BAD_REQUEST);
    }

    //토큰 지급
    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    tokenToHeaders(tokenDto, response);

    // Message 및 Status를 Return
    return new ResponseEntity<>(new PrivateResponseBody
            (StatusCode.OK,MemberResponseDto.builder()
                    .email(member.getEmail())
                    .build()),HttpStatus.OK);
  }

  //로그아웃
  public ResponseEntity<PrivateResponseBody> logout(HttpServletRequest request) {

    // 토큰 확인
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return new ResponseEntity<>(new PrivateResponseBody
              (StatusCode.LOGIN_WRONG_FORM_JWT_TOKEN,null),HttpStatus.BAD_REQUEST);
    }
    Member member = tokenProvider.getMemberFromAuthentication();

    // 회원 확인
    if (null == member) {
      return new ResponseEntity<>(new PrivateResponseBody
              (StatusCode.LOGIN_MEMBER_ID_FAIL,null),HttpStatus.NOT_FOUND);
    }

    tokenProvider.deleteRefreshToken(member);

    // Message 및 Status를 Return
    return new ResponseEntity<>(new PrivateResponseBody
            (StatusCode.OK,"로그아웃"),HttpStatus.OK);
  }

  //Email 확인
  @Transactional(readOnly = true)
  public Member isPresentMember(String email) {
    Optional<Member> optionalMember = memberRepository.findByEmail(email);
    return optionalMember.orElse(null);
  }

  //토큰 지급
  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
    response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

}
