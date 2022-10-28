package com.move.review.service;

import com.move.review.controller.response.ResponseDto;
import com.move.review.controller.response.CommentResponseDto;
import com.move.review.domain.Comment;
import com.move.review.domain.Member;
import com.move.review.domain.Review;
import com.move.review.controller.request.CommentRequestDto;
import com.move.review.domain.UserDetailsImpl;
import com.move.review.jwt.TokenProvider;
import com.move.review.repository.CommentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TokenProvider tokenProvider;
    private final ReviewService reviewService;

    // 댓글 작성
    @Transactional
    public ResponseDto<?> createComment(CommentRequestDto requestDto, HttpServletRequest request) {
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

        Review review = reviewService.isPresentPost(requestDto.getReviewId());
        if (null == review) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Comment comment = Comment.builder()
                .member(member)
                .review(review)
                .memberName(member.getMemberName())
                .comment(requestDto.getComment())
                .build();
        commentRepository.save(comment);

        CommentResponseDto commentList = CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .memberName(comment.getMember().getMemberName())
                .comment(comment.getComment())
                .build();

        return ResponseDto.success(commentList);
    }

    // 댓글 수정
    @Transactional
    public ResponseDto<?> updateComment(Long commentId, CommentRequestDto requestDto, HttpServletRequest request) {
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

        Review review = reviewService.isPresentPost(requestDto.getReviewId());
        if (null == review) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Comment comment = isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (comment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        comment.update(requestDto);
        CommentResponseDto commentList = CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .memberName(comment.getMember().getMemberName())
                .comment(comment.getComment())
                .build();

        return ResponseDto.success(commentList);
    }

    // 댓글 삭제
    @Transactional
    public ResponseDto<?> deleteComment(Long commentId, HttpServletRequest request) {
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

        Comment comment = isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (comment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        commentRepository.delete(comment);
        return ResponseDto.success("success");
    }

    // 댓글 존재 여부 확인
    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }

    // 토큰 확인
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
