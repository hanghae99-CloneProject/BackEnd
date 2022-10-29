package com.move.review.service;

import com.move.review.controller.request.PostRequestDto;
import com.move.review.controller.response.PostResponseDto;
import com.move.review.controller.response.ResponseDto;
import com.move.review.domain.Media;
import com.move.review.domain.Member;
import com.move.review.domain.Post;
import com.move.review.exception.PrivateException;
import com.move.review.exception.PrivateResponseBody;
import com.move.review.exception.StatusCode;
import com.move.review.jwt.TokenProvider;
import com.move.review.repository.MediaRepository;
import com.move.review.repository.PostRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import static com.move.review.domain.QMedia.media;
import static com.move.review.domain.QMember.member;
import static com.move.review.domain.QPost.post;


@RequiredArgsConstructor
@Service
public class PostService {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;
    private final TokenProvider tokenProvider;
    private final MediaRepository mediaRepository;
    private final PostRepository postRepository;
    private final ImageUpload imageUpload;


    // 회원관리 기능이 정상적으로 합쳐진다면 해제
//    public Member authorizeToken(HttpServletRequest request){
//         // 회원 관리 기능과 합쳐진다면 해제
//        if(request.getHeader("Authorization") == null){
//            throw new PrivateException(StatusCode.LOGIN_EXPIRED_JWT_TOKEN);
//        }
//
//        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token"))){
//            throw new PrivateException(StatusCode.LOGIN_EXPIRED_JWT_TOKEN);
//        }
//
//        Member member = tokenProvider.getMemberFromAuthentication();
//
//        return member;
//    }


    // 게시글 작성
    public ResponseEntity<PostResponseDto> writePost(
            List<MultipartFile> multipartFiles,
            PostRequestDto postRequestDto,
            HttpServletRequest request) {

        // 회원관리 기능이 정상적으로 합쳐진다면 해제
        // authorizeToken(request);

        // 구현 동작을 테스트하기 위해 임의 멤버를 사용
        Member test_member = jpaQueryFactory
                .selectFrom(member)
                .where(member.id.eq(1L))
                .fetchOne();

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .author(test_member.getEmail())
                .category(postRequestDto.getCategory())
                .local(postRequestDto.getLocal())
                .state(postRequestDto.getState())
                .trade(postRequestDto.getTrade())
                .price(postRequestDto.getPrice())
                .content(postRequestDto.getContent())
                .tag(postRequestDto.getTag())
                .amount(postRequestDto.getAmount())
                .member(test_member)
                .build();

        postRepository.save(post);

        if (multipartFiles == null) {
            return ResponseEntity.ok(
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
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
//                            .whoCreated(post.getWhoCreate())
//                            .whoUpdated(post.getWhoUpdate())
                            .build()
            );
        }

        List<Media> medias = imageUpload.fileUpload(multipartFiles, post);

        return ResponseEntity.ok(
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
                        .medias(medias)
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
//                        .whoCreated(post.getWhoCreate())
//                        .whoUpdated(post.getWhoUpdate())
                        .build()
        );
    }



    // 게시글 수정
    @Transactional
    public ResponseEntity<PostResponseDto> updatePost(
            Long postId,
            List<MultipartFile> multipartFiles,
            PostRequestDto postRequestDto,
            HttpServletRequest request) {

        // 회원 관리 기능과 합쳐진다면 해제
//        if(request.getHeader("Authorization") == null){
//            return ResponseDto.fail("NOT_ALLOWED_TOKEN", "권한이 없는 유저입니다.");
//        }
//
//        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token"))){
//            return ResponseDto.fail("NOT_ALLOWED_TOKEN", "권한이 없는 유저입니다.");
//        }
//
//        Member member = tokenProvider.getMemberFromAuthentication();

        // 구현 동작을 테스트하기 위해 임의 멤버를 사용
        Member test_member = jpaQueryFactory
                .selectFrom(member)
                .where(member.id.eq(1L))
                .fetchOne();

        Post post1 = jpaQueryFactory
                .selectFrom(post)
                .where(post.member.eq(test_member).and(post.id.eq(postId)))
                .fetchOne();

        if (post1 == null) {
            throw new PrivateException(StatusCode.NOT_MATCH_POST);
        }

        jpaQueryFactory
                .update(post)
                .set(post.title, postRequestDto.getTitle())
                .set(post.category, postRequestDto.getCategory())
                .set(post.local, postRequestDto.getLocal())
                .set(post.state, postRequestDto.getState())
                .set(post.trade, postRequestDto.getTrade())
                .set(post.price, postRequestDto.getPrice())
                .set(post.content, postRequestDto.getContent())
                .set(post.tag, postRequestDto.getTag())
                .set(post.amount, postRequestDto.getAmount())
                .where(post.id.eq(postId))
                .execute();

        em.flush();
        em.clear();

        List<Media> medias = jpaQueryFactory
                .selectFrom(media)
                .where(media.post.eq(post1))
                .fetch();

        // 수정할 미디어 파일이 존재하지 않고 현재 게시글에 미디어 파일이 존재할 경우는 넘김
        // 수정할 미디어 파일이 존재하지 않고 현재 게시글에 미디어 파일이 존재하지 않을 경우에도 넘김

        // 수정할 미디어 파일이 존재하고 현재 게시글에 미디어 파일이 존재할 경우에 해당 미디어 파일 삭제 후 수정하고자 하는 미디어 파일로 다시 저장(업데이트)
        if (multipartFiles != null && !medias.isEmpty()) {
            // 등록되어있는 미디어 파일들을 조회
            for (Media each_media : medias) {
                // DB에서 삭제
                jpaQueryFactory
                        .delete(media)
                        .where(media.id.eq(each_media.getId()))
                        .execute();
                // S3에서도 삭제
                imageUpload.deleteFile(each_media.getMediaName());
            }

            // 다시 S3와 DB 모두 저장하고 리스트화
            medias = imageUpload.fileUpload(multipartFiles, post1);

            // 수정할 미디어 파일이 존재하고 현재 게시글에 미디어 파일이 존재하지 않을 경우에 수정하고자 하는 미디어 파일로 새로 저장(업데이트)
        } else if (multipartFiles != null && medias.isEmpty()) {
            // S3와 DB 모두 저장하고 리스트화
            medias = imageUpload.fileUpload(multipartFiles, post1);
        }

        // [위와 같은 방식으로 업데이트를 구성한 이유]
        // 만약, 미디어 파일을 오직 하나만을 허용하여 등록 및 수정을 할 수가 있다면 단순하게 기존의 방법처럼 jpa를 사용하여 update 메소드를 만들어주고 업데이트 시키면 된다.
        // 하지만, 업로드하는 미디어 파일이 하나 이상일 가능성이 있기 떄문에 수정을 하려고 한다면 예외의 경우가 있을 수 있다.
        // 예를 들어, 현재 등록되어 있는 미디어 파일이 1개 이고, 수정 업로드 하고자하는 미디어 파일이 2개 이상일 경우,
        // 현존하는 1개의 미디어파일을 수정 파일 중 어떤 파일로 업데이트 시켜줘야하는지 알 수 없으며, 남은 1개를 다시 등록 시켜주는 코드를 짜야한다.
        // 그래서 작업을 줄이기 위해, 미디어 파일이 현재 존재하고, 수정하고자하는 파일 또한 존재한다면,
        // 현재 존재하는 미디어 파일들을 삭제하고, 다시 수정하고자 하는 파일들을 한번에 등록시켜주는 것이 좋다고 생각하여 위와 같이 구성했다.

        return ResponseEntity.ok(
                PostResponseDto.builder()
                        .id(post1.getId())
                        .title(post1.getTitle())
                        .author(post1.getAuthor())
                        .category(post1.getCategory())
                        .local(post1.getLocal())
                        .state(post1.getState())
                        .trade(post1.getTrade())
                        .price(post1.getPrice())
                        .content(post1.getContent())
                        .tag(post1.getTag())
                        .amount(post1.getAmount())
                        .medias(medias)
                        .createdAt(post1.getCreatedAt())
                        .modifiedAt(post1.getModifiedAt())
                        .build()
        );
    }


    // 게시글 삭제
    @Transactional
    public ResponseEntity<String> deletePost(Long postId, HttpServletRequest request) {
        // 회원 관리 기능과 합쳐진다면 해제
//        if(request.getHeader("Authorization") == null){
//            return ResponseDto.fail("NOT_ALLOWED_TOKEN", "권한이 없는 유저입니다.");
//        }
//
//        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token"))){
//            return ResponseDto.fail("NOT_ALLOWED_TOKEN", "권한이 없는 유저입니다.");
//        }
//
//        Member member = tokenProvider.getMemberFromAuthentication();

        // 구현 동작을 테스트하기 위해 임의 멤버를 사용
        Member test_member = jpaQueryFactory
                .selectFrom(member)
                .where(member.id.eq(1L))
                .fetchOne();

        Post post1 = jpaQueryFactory
                .selectFrom(post)
                .where(post.id.eq(postId).and(post.member.eq(test_member)))
                .fetchOne();

        if (post1 == null) {
            throw new PrivateException(StatusCode.NOT_MATCH_POST);
        }

        List<Media> medias = jpaQueryFactory
                .selectFrom(media)
                .where(media.post.eq(post1))
                .fetch();

        System.out.println("삭제 메소드 진입 확인");

        if (!medias.isEmpty()) {
            for (Media exist_media : medias) {
                // 여전히 cascade가 반영이 되지 않아 부모가 삭제되어도 삭제되지 않는다..
                // media 쪽을 먼저 삭제하도록 우선 구현.
                jpaQueryFactory.delete(media)
                        .where(media.post.eq(post1))
                        .execute();

                imageUpload.deleteFile(exist_media.getMediaName());
            }
        }

        jpaQueryFactory.delete(post)
                .where(post.id.eq(postId))
                .execute();


        return ResponseEntity.ok("삭제 성공");
    }


    // 특정 게시글 1개 상세 조회
    public ResponseEntity<PostResponseDto> getPost(Long postId) {
        Post view_post = jpaQueryFactory
                .selectFrom(post)
                .where(post.id.eq(postId))
                .fetchOne();

        List<Media> medias = jpaQueryFactory
                .selectFrom(media)
                .where(media.post.eq(view_post))
                .fetch();

        return ResponseEntity.ok(
                PostResponseDto.builder()
                        .id(view_post.getId())
                        .title(view_post.getTitle())
                        .author(view_post.getAuthor())
                        .category(view_post.getCategory())
                        .local(view_post.getLocal())
                        .state(view_post.getState())
                        .trade(view_post.getTrade())
                        .price(view_post.getPrice())
                        .content(view_post.getContent())
                        .tag(view_post.getTag())
                        .amount(view_post.getAmount())
                        .medias(medias)
                        .createdAt(view_post.getCreatedAt())
                        .modifiedAt(view_post.getModifiedAt())
                        .build()
        );

    }


    // 게시글 목록 조히
    public ResponseEntity<ArrayList<HashMap<String, String>>> getAllPost(){
        List<Post> Allposts = jpaQueryFactory
                .selectFrom(post)
                .fetch();

        ArrayList<HashMap<String,String>> posts_map = new ArrayList<HashMap<String, String>>();

        for(Post one_post : Allposts){
            HashMap<String, String> posts = new HashMap<String, String>();

            posts.put("title", one_post.getTitle());
            posts.put("price", Integer.toString(one_post.getPrice()));
            posts.put("local", one_post.getLocal());

            List<Media> each_post = jpaQueryFactory
                    .selectFrom(media)
                    .where(media.post.eq(one_post))
                    .fetch();

            posts.put("mediaName", each_post.get(0).getMediaName());
            posts.put("mediaUrl", each_post.get(0).getMediaUrl());
            posts_map.add(posts);
        }

        return ResponseEntity.ok(posts_map);

    }
}
