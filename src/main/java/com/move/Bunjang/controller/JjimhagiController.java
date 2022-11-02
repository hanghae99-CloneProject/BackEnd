package com.move.Bunjang.controller;


import com.move.Bunjang.controller.request.JjimhagiRequestDto;
import com.move.Bunjang.controller.response.ResponseDto;
import com.move.Bunjang.service.JjimhagiService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Validated
@RequiredArgsConstructor
@RestController
public class JjimhagiController {
    private final JjimhagiService jjimhagiService;

    @RequestMapping(value= "/bunjang/jjimhagi")
    public ResponseDto<?> createJjimhagi(@RequestBody JjimhagiRequestDto requestDto, HttpServletRequest request) {
        return jjimhagiService.createJjimhagi(requestDto, request);
    }
}
