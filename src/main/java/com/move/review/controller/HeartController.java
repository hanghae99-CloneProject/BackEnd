package com.move.review.controller;

import com.move.review.controller.response.ResponseDto;
import com.move.review.domain.UserDetailsImpl;
import com.move.review.repository.HeartRepository;
import com.move.review.service.HeartService;
import com.move.review.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/review")
public class HeartController {
    private final HeartService heartService;

    // 좋아요, 좋아요취소
    @PostMapping("/{reviewId}/heart")
    public ResponseDto<?> heart(@PathVariable Long reviewId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return heartService.heart(reviewId, userDetails);
    }
}
