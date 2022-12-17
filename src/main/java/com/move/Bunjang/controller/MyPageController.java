package com.move.Bunjang.controller;

import com.move.Bunjang.controller.response.ResponseDto;
import com.move.Bunjang.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@Validated
@RequiredArgsConstructor
@RequestMapping("/bunjang")
@RestController
public class MyPageController {
    private final MyPageService myPageService;

    @RequestMapping(value = "/mypage", method = RequestMethod.GET)
    public ResponseDto<?> getMyPage(HttpServletRequest request) {
        return myPageService.getMyPage(request);
    }


}
