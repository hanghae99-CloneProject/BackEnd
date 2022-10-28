package com.move.review.service;

import com.move.review.controller.request.IdCheckRequestDto;
import com.move.review.controller.response.MemberResponseDto;
import com.move.review.domain.Member;
import com.move.review.controller.request.LoginRequestDto;
import com.move.review.controller.request.MemberRequestDto;
import com.move.review.controller.response.ResponseDto;
import com.move.review.controller.request.TokenDto;
import com.move.review.jwt.TokenProvider;
import com.move.review.repository.MemberRepository;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createMember(MemberRequestDto requestDto) {

    if (null != isPresentMember(requestDto.getMemberName())) {
      return ResponseDto.fail("DUPLICATED_NICKNAME",
              "중복된 아이디가 있습니다.");
    }

    if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
      return ResponseDto.fail("PASSWORDS_NOT_MATCHED",
          "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    }

    Member member = Member.builder()
            .memberName(requestDto.getMemberName())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                    .build();
    memberRepository.save(member);
    return ResponseDto.success("회원가입 성공");
  }

  @Transactional
  public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
    Member member = isPresentMember(requestDto.getMemberName());
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }

    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      return ResponseDto.fail("INVALID_MEMBER", "사용자를 찾을 수 없습니다.");
    }

    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    tokenToHeaders(tokenDto, response);

    return ResponseDto.success(
        MemberResponseDto.builder()
            .memberName(member.getMemberName())
            .build()
    );
  }


  public ResponseDto<?> logout(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }
    Member member = tokenProvider.getMemberFromAuthentication();
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }

    tokenProvider.deleteRefreshToken(member);

    return ResponseDto.success("로그아웃 성공");
  }

  @Transactional(readOnly = true)
  public Member isPresentMember(String memberName) {
    Optional<Member> optionalMember = memberRepository.findByMemberName(memberName);
    return optionalMember.orElse(null);
  }

  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
    response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

    public ResponseDto<?> idCheck(IdCheckRequestDto requestDto) {
      if (null != isPresentMember(requestDto.getMemberName())) {
        return ResponseDto.fail("DUPLICATED_NICKNAME",
                "중복된 아이디가 있습니다.");
      }

      return ResponseDto.success("사용가능한 아이디 입니다.");
    }
}
