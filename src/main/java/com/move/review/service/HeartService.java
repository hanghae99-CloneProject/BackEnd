package com.move.review.service;

import com.move.review.controller.response.ResponseDto;
import com.move.review.domain.Heart;
import com.move.review.domain.Member;
import com.move.review.domain.Review;
import com.move.review.domain.UserDetailsImpl;
import com.move.review.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final HeartRepository heartRepository;
    private final ReviewService reviewService;

    // 좋아요, 좋아요취소
    public ResponseDto<?> heart (Long reviewId, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        Review review = reviewService.isPresentPost(reviewId);

        // memberId, postId 존재여부 확인
        Optional<Heart> heart = heartRepository.findByMemberAndReview(member, review);

        // 1. 좋아요 기록이 있으면 삭제 ( = 좋아요 취소)
        if(heart.isPresent()) {
            heartRepository.deleteById(heart.get().getHeartId());
            return ResponseDto.success(false);
        } else {
            // 2.좋아요 기록이 없으면 저장한다.
            Heart heartList = Heart.builder()
                    .review(review)
                    .member(member)
                    .build();
            heartRepository.save(heartList);
            return ResponseDto.success(true);
        }
    }
}
