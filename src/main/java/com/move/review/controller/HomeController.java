package com.move.review.controller;

import com.move.review.controller.request.MemberRequestDto;
import com.move.review.controller.response.ResponseDto;
import com.move.review.domain.UserDetailsImpl;
import com.move.review.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class HomeController {
    private final HomeService homeService;

    @GetMapping(value = "/top-heart")
    public Long getMain() {
        return homeService.getMain();
    }
}
