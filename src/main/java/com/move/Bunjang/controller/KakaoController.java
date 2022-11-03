package com.move.Bunjang.controller;

import com.move.Bunjang.controller.request.KakaoAccount;
import com.move.Bunjang.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class KakaoController {

    private final KakaoService kakaoService;

     // https://kauth.kakao.com/oauth/authorize?response_type=code&client_id={cff95e14da03921467b0bb57efd46440}&redirect_uri={http://localhost:8080/oauth/kakao}
     // 위의 결과로 code를 받아와서, 해당 코드를 통해 카카오 인증 서버에서 accessToken/refreshToken을 받아온다.

    @GetMapping("/bunjang/kLoginCallback")
    public KakaoAccount getKakaoAccount(@RequestParam("code") String code) {
        log.debug("code = {}", code);
        return kakaoService.getInfo(code).getKakaoAccount();
    }
}
