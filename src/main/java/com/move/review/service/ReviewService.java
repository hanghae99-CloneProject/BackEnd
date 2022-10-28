package com.move.review.service;

import com.move.review.controller.response.CommentResponseDto;
import com.move.review.controller.response.ReviewResponseDto;
import com.move.review.domain.*;
import com.move.review.controller.request.ReviewRequestDto;
import com.move.review.controller.response.ResponseDto;
import com.move.review.exception.PrivateResponseBody;
import com.move.review.exception.StatusCode;
import com.move.review.jwt.TokenProvider;
import com.move.review.repository.CommentRepository;
import com.move.review.repository.HeartRepository;
import com.move.review.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final HeartRepository heartRepository;
    private final TokenProvider tokenProvider;

    // Review 생성
    @Transactional
    public ResponseEntity<?> createReview(ReviewRequestDto requestDto, HttpServletRequest request) {
//        if (null == request.getHeader("Refresh-Token")) {
//            return ResponseDto.fail("MEMBER_NOT_FOUND",
//                    "로그인이 필요합니다.");
//        }
//
//        if (null == request.getHeader("Authorization")) {
//            return ResponseDto.fail("MEMBER_NOT_FOUND",
//                    "로그인이 필요합니다.");
//        }
//
//        Member member = validateMember(request);
//        if (null == member) {
//            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
//        }
//
//        // 리뷰 저장
//        Review review = Review.builder()
//                .image(requestDto.getImage())
//                .movieTitle(requestDto.getMovieTitle())
//                .genre(requestDto.getGenre())
//                .rating(requestDto.getRating())
//                .reviewTitle(requestDto.getReviewTitle())
//                .reviewContent(requestDto.getReviewContent())
//                .memberName(member.getMemberName())
//                .member(member)
//                .build();
//        reviewRepository.save(review);

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , null), HttpStatus.OK);
    }

    //전체 Review 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllReview() {
        List<Review> reviewList = reviewRepository.findAllByOrderByModifiedAtDesc();

        List<ReviewResponseDto> reviewResponseDtos = new ArrayList<>();
        for (Review review : reviewList)  {
            Long commentNum = commentRepository.countByReview(review);
            Long heartNum = heartRepository.countByReview(review);
            reviewResponseDtos.add(
                    ReviewResponseDto.builder()
                            .reviewId(review.getReviewId())
                            .image(review.getImage())
                            .movieTitle(review.getMovieTitle())
                            .genre(review.getGenre())
                            .rating(review.getRating())
                            .reviewTitle(review.getReviewTitle())
                            .reviewContent(review.getReviewContent())
                            .memberName(review.getMemberName())
                            .heartNum(heartNum)
                            .commentNum(commentNum)
                            .build()
            );
        }
        return ResponseDto.success(reviewResponseDtos);
    }

    //Review 세부 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getReview(Long id, UserDetailsImpl userDetails) {
        Review review = isPresentPost(id);
        if (null == review) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        // 댓글 목록
        List<Comment> commentList = commentRepository.findAllByReview(review);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        // 좋아요 갯수
        Long heartNum = heartRepository.countByReview(review);
        // 댓글 갯수
        Long commentNum = commentRepository.countByReview(review);

        boolean heartYn = false;
        if(userDetails != null) {
            Member member = userDetails.getMember();
            // 하트 여부 조회
            Optional<Heart> heart = heartRepository.findByMemberAndReview(member, review);

            if(heart.isPresent()) {
                heartYn = true;
            }
        }

        for (Comment comment : commentList) {
            commentResponseDtoList.add(
                    CommentResponseDto.builder()
                            .commentId(comment.getCommentId())
                            .memberName(comment.getMember().getMemberName())
                            .comment(comment.getComment())
                            .build()
            );
        }
        ReviewResponseDto reviewList = ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .image(review.getImage())
                .movieTitle(review.getMovieTitle())
                .genre(review.getGenre())
                .rating(review.getRating())
                .reviewTitle(review.getReviewTitle())
                .reviewContent(review.getReviewContent())
                .memberName(review.getMember().getMemberName())
                .heartNum(heartNum)
                .commentNum(commentNum)
                .heartYn(heartYn)
                .commentResponseDtoList(commentResponseDtoList)
                .build();

        return ResponseDto.success(reviewList);
    }

    //Review 업데이트
    @Transactional
    public ResponseDto<Review> updateReview(Long id, ReviewRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Review review = isPresentPost(id);
        if (null == review) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        if (review.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        review.update(requestDto);
        return ResponseDto.success(review);
    }

    //Review 삭제
    @Transactional
    public ResponseDto<?> deleteReview(Long id, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Review review = isPresentPost(id);
        if (null == review) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        if (review.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
        return ResponseDto.success("삭제 성공");
    }

    @Transactional(readOnly = true)
    public Review isPresentPost(Long id) {
        Optional<Review> optionalPost = reviewRepository.findById(id);
        return optionalPost.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;}
            return tokenProvider.getMemberFromAuthentication();
        }
    }
