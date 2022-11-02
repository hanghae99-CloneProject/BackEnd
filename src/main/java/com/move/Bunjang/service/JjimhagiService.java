package com.move.Bunjang.service;

import com.move.Bunjang.controller.request.JjimhagiRequestDto;
import com.move.Bunjang.controller.response.ResponseDto;
import com.move.Bunjang.domain.Jjimhagi;
import com.move.Bunjang.domain.Member;
import com.move.Bunjang.domain.Post;
import com.move.Bunjang.exception.PrivateException;
import com.move.Bunjang.exception.StatusCode;
import com.move.Bunjang.jwt.TokenProvider;
import com.move.Bunjang.repository.JjimhagiRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;

import static com.move.Bunjang.domain.QPost.post;

@Service
@RequiredArgsConstructor
public class JjimhagiService {
    private final PostService postService;
    private final JjimhagiRepository jjimhagiRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> createJjimhagi(JjimhagiRequestDto requestDto , HttpServletRequest request) {

        // Refresh-Token으로 유효성 검사
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        // 엑세스 토큰으로 유효성 검사
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        // 멤버 null 값 유효성 검사
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        // 현 유저가 작성하여 수정할 수 있는 게시글 정보 저장
        Post presentPost = jpaQueryFactory
                .selectFrom(post)
                .where(post.member.eq(member)) // 본인이 작성하여 게시글을 수정할 수 있는지 확인할 수 있는 조건
                .fetchOne();

        // 포스트 null 값 유효성 검사
        // POST가 NULL 이면 작성한 게시글이 없어서 수정할 수 없다.
        if (presentPost == null) {
            throw new PrivateException(StatusCode.POST_ERROR); // POST_ERROR : "게시글 작성이 필요합니다."
        }

        // 찜하기 취소
        Jjimhagi findJjimhagi = jjimhagiRepository.findByPost_IdAndMember_MemberId(presentPost.getId(), member.getMemberId());
        if(null != findJjimhagi ) {
            jjimhagiRepository.delete(findJjimhagi);
            return ResponseDto.success("찜하기 취소");
        }

        Jjimhagi jjimhagi = Jjimhagi.builder()
                .member(member)
                .post(presentPost)
                .build();
        jjimhagiRepository.save(jjimhagi);
        return ResponseDto.success("찜하기 완료");
    }

    //멤버 유효성 검사
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}