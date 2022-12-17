package com.move.Bunjang.service;

import com.move.Bunjang.controller.response.MypageResponseDto;
import com.move.Bunjang.controller.response.PostResponseDto;
import com.move.Bunjang.controller.response.ResponseDto;
import com.move.Bunjang.domain.Media;
import com.move.Bunjang.domain.Member;
import com.move.Bunjang.domain.Post;
import com.move.Bunjang.jwt.TokenProvider;
import com.move.Bunjang.repository.PostRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.move.Bunjang.domain.QMedia.media;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public ResponseDto<?> getMyPage(HttpServletRequest request) {

        // 리프레시 토큰을 이용한 null 값 체크
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        // accessToken 이용한 null 값 체크
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        //member null 값 체크
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        //내가 작성한 게시글
        List<Post> myPostList = postRepository.findAllByMember(member);
        List<PostResponseDto> myPostResponseDtoList = new ArrayList<>();

        for (Post post : myPostList) {
            Media exist_media = jpaQueryFactory
                    .selectFrom(media)
                    .where(media.post_id.eq(post.getId()))
                    .fetchOne();

            myPostResponseDtoList.add(
                    PostResponseDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .author(post.getAuthor())
                            .category(post.getCategory())
                            .local(post.getLocal())
                            .state(post.getState())
                            .trade(post.getTrade())
                            .price(post.getPrice())
                            .content(post.getContent())
                            .tag(post.getTag())
                            .amount(post.getAmount())
                            .media(exist_media)
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(
                MypageResponseDto.builder()
                        .mypagePosts(myPostResponseDtoList)
                        .build()
        );
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}






