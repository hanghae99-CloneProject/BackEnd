package com.move.Bunjang.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.Bunjang.controller.request.PostRequestDto;
import com.move.Bunjang.controller.response.PostResponseDto;
import com.move.Bunjang.domain.Category;
import com.move.Bunjang.domain.Media;
import com.move.Bunjang.domain.Member;
import com.move.Bunjang.domain.Post;
import com.move.Bunjang.exception.PrivateException;
import com.move.Bunjang.exception.PrivateResponseBody;
import com.move.Bunjang.exception.StatusCode;
import com.move.Bunjang.jwt.TokenProvider;
import com.move.Bunjang.repository.MediaRepository;
import com.move.Bunjang.repository.PostRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import java.util.*;

import static com.move.Bunjang.domain.QMember.member;
import static com.move.Bunjang.domain.QMedia.media;
import static com.move.Bunjang.domain.QPost.post;


@RequiredArgsConstructor
@Service
public class PostService {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;
    private final TokenProvider tokenProvider;
    private final MediaRepository mediaRepository;
    private final PostRepository postRepository;
    private final ImageUpload imageUpload;


    // 인증 정보 검증 부분을 한 곳으로 모아놓음
    public Member authorizeToken(HttpServletRequest request) {

        // Access 토큰 유효성 확인
        if (request.getHeader("Authorization") == null) {
            throw new PrivateException(StatusCode.LOGIN_EXPIRED_JWT_TOKEN);
        }

        // Refresh 토큰 유요성 확인
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            throw new PrivateException(StatusCode.LOGIN_EXPIRED_JWT_TOKEN);
        }

        // Access, Refresh 토큰 유효성 검증이 완료되었을 경우 인증된 유저 정보 저장
        Member member = tokenProvider.getMemberFromAuthentication();

        // 인증된 유저 정보 반환
        return member;
    }


    // 게시글 작성
    @Transactional
    public ResponseEntity<PrivateResponseBody> writePost(
//            List<MultipartFile> multipartFiles,
            PostRequestDto postRequestDto,
            HttpServletRequest request) throws IOException {

        // 인증된 유저 정보 획득
        Member member = authorizeToken(request);

        // 구현 동작을 테스트하기 위해 임의 멤버를 사용
//        Member test_member = jpaQueryFactory
//                .selectFrom(member)
//                .where(member.memberId.eq(1L))
//                .fetchOne();

        // 게시글 업로드 시 기입하는 위치 정보를 공통화하기 위해 kakao 지역 API 사용 (시,구 ,동 까지 출력)
        String address_name = getAddressData(postRequestDto.getLocal());
        System.out.println("게시글 작성 시 카카오 API 반영 지역 : " + address_name);

        // 게시글 작성
        Post post = Post.builder()
                .title(postRequestDto.getTitle()) // 게시글 제목
                .author(member.getEmail()) // 게시글을 작성하는 현재 로그인한 유저 이메일
                .category(Category.partsValue(Integer.parseInt(postRequestDto.getCategory()))) // int 타입으로 값을 전달받으면 그에 해당하는 enum 타입 카테고리 저장
                .local(address_name) // 판매하고자 하는 지역 (Kakao API 로 추출한 지역 주소)
                .state(postRequestDto.getState()) // 신상품 혹은 중고상품 상태
                .trade(postRequestDto.getTrade()) // 교환가능 혹은 교환불가능
                .price(Integer.parseInt(postRequestDto.getPrice())) // 가격 정보
                .content(postRequestDto.getContent()) // 게시글 내용 (판매 정보)
                .tag(postRequestDto.getTag()) // 연관태그 (태그를 기입하게 되면 태그 키워드에 연관된 등록 상품들이 리스트업)
                .amount(Integer.parseInt(postRequestDto.getAmount())) // 판매 제품 수량
                .member(member) // 판매자 고유 ID (구분값)
                .jjimhagiCount((long) Integer.parseInt(postRequestDto.getJjimhagiCount())) // 찜하기카운트 추가 2022-11-02 doosan add
                .build();

        // 게시글 작성 정보 저장
        postRepository.save(post);

        // 이미지 업로드 API 를 거쳐 등록된 media 파일을 고유 Id를 통해 불러온다.
        Media exist_media = jpaQueryFactory
                .selectFrom(media)
                .where(media.id.eq(Long.parseLong(postRequestDto.getImg_id())))
                .fetchOne();

        // 불러온 media 파일을 post_id 칼럼에 작성 게시글의 고유 id 를 부여하여 연결해준다.
        jpaQueryFactory
                .update(media)
                .set(media.post_id, post.getId())
                .where(media.id.eq(Long.parseLong(postRequestDto.getImg_id())))
                .execute();

        em.flush();

        // media 파일을 등록하지 않을경우 제외하고 출력
        if (postRequestDto.getImg_id() == null) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
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
                            .jjimhagiCount(post.getJjimhagiCount()) // 찜하기 카운트 추가 2022-11-02  doosan add
                            .build()), HttpStatus.OK
            );
        }

        // 미디어 파일 업로드는 interface화 시켜 따로 생성하여 이용하였다.
        // 게시글 작성 이외에도 미디어 파일을 업로드할 일이 있을 경우를 대비.

        // 작성 완료된 게시글 정보 전체 출력
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
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
                        .build()), HttpStatus.OK
        );

    }

    // 게시글 수정
    @Transactional
    public ResponseEntity<PrivateResponseBody> updatePost(
            Long postId,
            PostRequestDto postRequestDto,
            HttpServletRequest request) throws IOException {

        // 인증된 유저 정보 획득
        Member member = authorizeToken(request);

        // 구현 동작을 테스트하기 위해 임의 멤버를 사용
//        Member test_member = jpaQueryFactory
//                .selectFrom(member)
//                .where(member.memberId.eq(1L))
//                .fetchOne();

        // 현 유저가 작성하여 수정할 수 있는 게시글 정보 저장
        Post myPost = jpaQueryFactory
                .selectFrom(post)
                .where(post.member.eq(member).and(post.id.eq(postId))) // 본인이 작성하여 게시글을 수정할 수 있는지 확인할 수 있는 조건
                .fetchOne();

        // myPost 가 null 이라면 자신이 작성한 게시글이 아니어서 수정할 수 없다는 뜻.
        if (myPost == null) {
            // 작성한 게시글이 아니라서 수정할 수 없다는 custom 에러 출력
            throw new PrivateException(StatusCode.NOT_MATCH_POST);
        }

        // 현재 작성된 게시글 체크 2022-11-02 doosan 추가
        Post presentPost = jpaQueryFactory
                .selectFrom(post)
                .where(post.member.eq(member).and(post.id.eq(postId)))
                .fetchOne();

        // POST가 NULL 이면 작성한 게스글이 없어서 수정할 수 없다. 2022-11-02 doosan 추가
        if (presentPost == null) {
            throw new PrivateException(StatusCode.POST_ERROR);
        }

        // 게시글 업로드 시 기입하는 위치 정보를 공통화하기 위해 kakao 지역 API 사용 (시,구 ,동 까지 출력)
        String address_name = getAddressData(postRequestDto.getLocal());
        System.out.println("게시글 수정 시 카카오 API 반영 지역 : " + address_name);

        // 미디어 정보를 제외한 나머지 게시글 내용을 수정하여 업데이트
        jpaQueryFactory
                .update(post)
                .set(post.title, postRequestDto.getTitle()) // 제목 수정
                .set(post.category, Category.partsValue(Integer.parseInt(postRequestDto.getCategory()))) // 카테고리 수정
                .set(post.local, address_name) // 지역 주소 수정
                .set(post.state, postRequestDto.getState()) // 상태 수정
                .set(post.trade, postRequestDto.getTrade()) // 교환 가능 여부 수정
                .set(post.price, Integer.parseInt(postRequestDto.getPrice())) // 가격 수정
                .set(post.content, postRequestDto.getContent()) // 내용 수정
                .set(post.tag, postRequestDto.getTag()) // 연관 태그 수정
                .set(post.amount, Integer.parseInt(postRequestDto.getAmount())) // 수량 수정
                .set(post.jjimhagiCount,Long.parseLong(postRequestDto.getJjimhagiCount())) // 추가 수정 필요
                .where(post.id.eq(postId)) // 선택한 게시글 고유 ID에 해당하는 게시글을 수정한다는 조건
                .execute();

        em.flush();
        em.clear();

        // 현재 게시글에 존재하는 등록된 미디어 파일들 리스트업
        Media exist_media = jpaQueryFactory
                .selectFrom(media)
                .where(media.post_id.eq(myPost.getId()))
                .fetchOne();

        // 업데이트 하기 위한 미디어 데이터
        Media update_media = jpaQueryFactory
                .selectFrom(media)
                .where(media.id.eq(Long.parseLong(postRequestDto.getImg_id())))
                .fetchOne();

        // 수정할 media 파일이 존재하지 않고 현재 게시글에 media 파일이 존재할 경우는 넘김
        // 수정할 media 파일이 존재하지 않고 현재 게시글에 media 파일이 존재하지 않을 경우에도 넘김

        // 수정할 media 파일이 존재하고, 기존에 등록된 media 파일이 존재할 경우 수정
        if(update_media != null && exist_media != null){
            // 기존 DB에 존재하는 미디어 파일에 update_media 데이터로 업데이트
            jpaQueryFactory
                    .update(media)
                    .set(media.mediaName, update_media.getMediaName()) // DB에 저장된 수정할 media 파일의 파일이름으로 업데이트
                    .set(media.mediaUrl, update_media.getMediaUrl()) // DB에 저장된 수정할 media 파일의 파일주소로 업데이트
                    .where(media.post_id.eq(exist_media.getPost_id())) // 기등록된 media 파일
                    .execute();

            // 위에서 기등록된 media 파일에 업데이트를 완료하였으므로,
            // 업데이트 하기 위한 새로운 데이터를 DB에서 삭제처리
            jpaQueryFactory
                    .delete(media)
                    .where(media.id.eq(update_media.getId()))
                    .execute();

            // S3에서도 업데이트 하기 위한 새로운 데이터를 기존 미디어 데이터에 업데이트한 후 삭제처리
            imageUpload.deleteFile(update_media.getMediaName());

        // 수정할 media 파일이 존재하고, 기존에 등록된 media 파일이 존재하지 않을 경우
        // 수정할 media 파일을 게시글에 새로 매핑시켜 묶어준다
        }else if(update_media != null && exist_media == null){
            jpaQueryFactory.update(media)
                    .set(media.post_id, myPost.getId())
                    .where(media.id.eq(update_media.getId())) // 수정할 media 파일의 post_id 속성에 해당 게시글 고유 id를 부여하여 묶어줌
                    .execute();

            em.flush();

            // 최종적으로 exist_media가 출력되어져야하기 떄문에 update_media를 반영시켜줌
            exist_media = update_media;
        }

        // <설계 수정 전>
        // [위와 같은 방식으로 업데이트를 구성한 이유] - ##설계 방향성이 수정됨으로 인해 아래 설명 내용은 적용되지 않음
        // 만약, 미디어 파일을 오직 하나만을 허용하여 등록 및 수정을 할 수가 있다면 단순하게 기존의 방법처럼 jpa를 사용하여 update 메소드를 만들어주고 업데이트 시키면 된다.
        // 하지만, 업로드하는 미디어 파일이 하나 이상일 가능성이 있기 떄문에 수정을 하려고 한다면 예외의 경우가 있을 수 있다.
        // 예를 들어, 현재 등록되어 있는 미디어 파일이 1개 이고, 수정 업로드 하고자하는 미디어 파일이 2개 이상일 경우,
        // 현존하는 1개의 미디어파일을 수정 파일 중 어떤 파일로 업데이트 시켜줘야하는지 알 수 없으며, 남은 1개를 다시 등록 시켜주는 코드를 짜야한다.
        // 그래서 작업을 줄이기 위해, 미디어 파일이 현재 존재하고, 수정하고자하는 파일 또한 존재한다면,
        // 현재 존재하는 미디어 파일들을 삭제하고, 다시 수정하고자 하는 파일들을 한번에 등록시켜주는 것이 좋다고 생각하여 위와 같이 구성했다.

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // <설계 수정 후>
        // 이미지 업로드 API과 게시글 작성 API를 두 개로 따로 나눠둠에 따라 운영 코드가 변경되었다.
        // 먼저, 기존에 게시글들 등록하면서 같이 업로드된 media 정보를 불러온다.
        // 수정하고자 하는 media 파일 또한 업로드 API가 동작됨에 따라 DB 및 S3 에 저장되어있고, 불러온다.
        // 만약, 수정할 media 파일이 존재하지 않고 현재 게시글에 미디어 파일이 존재할 경우는 그대로 넘긴다
        // 수정할 media 파일이 존재하지 않고 현재 게시글에 미디어 파일이 존재하지 않을 경우에도 넘긴다.
        // 수정할 media 파일과 게시글에 등록된 media 파일이 존재한다면 기존에 등록된 media 파일의 내용을 수정할 media 파일의 정보로 업데이트한다.
        // 변경이 되었으면, DB에 들어가있는 수정할 media 파일 정보를 삭제, s3에서도 삭제한다.
        // 수정할 media 파일은 존재하고, 기존에 등록된 media 파일이 존재하지 않는다면,
        // 수정할 media 파일의 post_id 칼럼에 해당 게시글의 고유 id를 부여하여 매핑한다. (게시글과 묶어줌)

        // [BAD CODE]
        // 갑작스런 구조가 바뀌었기 떄문에 시간관계상 처음부터 다시 깔끔한 코드 구현은 어려웠고 수정 전의 코드를 거의 재활용하여 가지고 있는 환경 내에서 최대한 리뉴얼했다.
        // 수정과정이 개인적으로 더럽고 깔끔하지 않고 이런 방식으로 하면 안되겠다는 생각이 드나 우선은 이런 식으로 변경하였다.
        // 또한, 기존 방식에서는 여러 media 파일들을 반영할 수 있게끔 설계를 했었으나 시간 관계상 하나의 media 파일들로만 운영된다고 가정하고 수정하였다.


        // 수정 완료된 게시글 정보 전체 출력
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                PostResponseDto.builder()
                        .id(myPost.getId())
                        .title(myPost.getTitle())
                        .author(myPost.getAuthor())
                        .category(myPost.getCategory())
                        .local(myPost.getLocal())
                        .state(myPost.getState())
                        .trade(myPost.getTrade())
                        .price(myPost.getPrice())
                        .content(myPost.getContent())
                        .tag(myPost.getTag())
                        .amount(myPost.getAmount())
                        .media(exist_media)
                        .createdAt(myPost.getCreatedAt())
                        .modifiedAt(myPost.getModifiedAt())
                        .build()), HttpStatus.OK
        );

    }


    // 게시글 삭제
    @Transactional
    public ResponseEntity<PrivateResponseBody> deletePost(Long postId, HttpServletRequest request) {

        // 인증된 유저 정보 획득
        Member auth_member = authorizeToken(request);

        // 구현 동작을 테스트하기 위해 임의 멤버를 사용
//        Member test_member = jpaQueryFactory
//                .selectFrom(member)
//                .where(member.memberId.eq(1L))
//                .fetchOne();

        // 게시글 정보
        Post myPost = jpaQueryFactory
                .selectFrom(post)
                .where(post.id.eq(postId).and(post.member.eq(auth_member))) // 게시글 중 선택한 게시글 ID와 같고, 현 유저가 작성한 글이 맞는지 확인하는 조건
                .fetchOne();

        // myPost 가 null 이라면 자신이 작성한 게시글이 아니어서 삭제할 수 없다는 뜻.
        if (myPost == null) {
            // 작성한 게시글이 아니라서 수정할 수 없다는 custom 에러 출력
            throw new PrivateException(StatusCode.NOT_MATCH_POST);
        }

        // 현재 게시글에 존재하는 등록된 미디어 파일들 리스트업 (수정 후 한개의 media 파일로만 운영되고 있음)
        List<Media> medias = jpaQueryFactory
                .selectFrom(media)
                .where(media.post_id.eq(myPost.getId()))
                .fetch();

        System.out.println("삭제 메소드 진입 확인");

        // 게시글에 미디어 파일이 존재한다면 별도의 작업을 통해 삭제 진행
        if (!medias.isEmpty()) {
            // 존재하는 미디어파일들을 하나씩 조회
            for (Media exist_media : medias) {
                // media 삭제.
                jpaQueryFactory.delete(media)
                        .where(media.post_id.eq(myPost.getId()))
                        .execute();

                // s3 쪽도 미디어파일 이름을 기준으로 삭제되도록 구현.
                imageUpload.deleteFile(exist_media.getMediaName());
            }
        }

        // 게시글 삭제
        jpaQueryFactory.delete(post)
                .where(post.id.eq(postId))
                .execute();

        // 삭제 성공 시 문구 출력
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                "삭제 성공"), HttpStatus.OK
        );
    }


    // 특정 게시글 1개 상세 조회
    public ResponseEntity<PrivateResponseBody> getPost(Long postId) {

        // 조회하고자하는 게시글 불러오기
        Post view_post = jpaQueryFactory
                .selectFrom(post)
                .where(post.id.eq(postId)) // 선택한 게시글의 고유 ID 서칭
                .fetchOne();

        // 게시글에 미디어파일들이 존재한다면 리스트업하여 저장 / 없다면 단순히 null로 저장
        Media exist_media = jpaQueryFactory
                .selectFrom(media)
                .where(media.post_id.eq(view_post.getId()))
                .fetchOne();

        // 조회하고자하는 게시글 정보들 전체 출력
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
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
                        .media(exist_media)
                        .createdAt(view_post.getCreatedAt())
                        .modifiedAt(view_post.getModifiedAt())
                        .build()), HttpStatus.OK
        );

    }


    // 게시글 목록 조회
    public ResponseEntity<PrivateResponseBody> getAllPost(Pageable pageable) {
        // 단순한 페이징 처리를 위해 quertDSL을 사용하지 않고 일반적인 JPA 사용
        // 모든 게시글을 페이징 처리하여 전체 페이지로 리스트업
        Page<Post> Allposts = postRepository.findAll(pageable);

        // 페이징 처리된 리스트업한 게시글들을 담아서 최종적으로 출력하기 위한 리스트 생성
        ArrayList<HashMap<String, String>> posts_map = new ArrayList<HashMap<String, String>>();

        // 불러온 전체 게시글 하나씩 조회
        for (Post one_post : Allposts) {
            // 전체목록 조회 시 일부의 정보들만 보여지게끔 하기 위해 추가적인 DTO를 생성하지 않고 HashMap 사용
            HashMap<String, String> posts = new HashMap<String, String>();

            // 제목 출력 정보
            posts.put("title", one_post.getTitle());
            // 가격 출력 정보
            // (가격 정보 이외의 정보들이 String 타입이기 때문에 HashMap을 키값과 value 값을 String 타입으로 지정하여 생성해주었고,
            // 가격만 int 타입이기 때문에 string타입으로 변환하여 넣어주었다.
            posts.put("price", Integer.toString(one_post.getPrice()));
            // 지역 출력 정보
            posts.put("local", one_post.getLocal());

            // 사진 출력 정보
            // 게시글에 해당하는 미디어 파일을 전부 다 가져온다.
            List<Media> each_post = jpaQueryFactory
                    .selectFrom(media)
                    .where(media.post_id.eq(one_post.getId()))
                    .fetch();

            // 가져온 미디어 파일들 중에 목록에서 보여지게될 썸네일 이미지는 등록한 미디어파일들 중 첫번째로 등록한 사진으로 한다.
            // 첫번쨰 사진 이름
            posts.put("mediaName", each_post.get(0).getMediaName());
            // 첫번쨰 사진 주소
            posts.put("mediaUrl", each_post.get(0).getMediaUrl());

            // 출력할 값들을 posts_map에 저장
            posts_map.add(posts);
        }

        // 일부 정보들만 보여지는 게시글 리스트 출력
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK, posts_map), HttpStatus.OK);

    }


    // 카카오 지도 API를 활용한 일정한 주소 양식 출력
    public String getAddressData(String roadFullAddr) throws IOException {
        // 카카오 API 키
        String apiKey = "1a123391d0bfd1a3c591465b76664655";
        //카카오 API 주소
        String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
        // json 형식의 주소를 String 으로 받는 결과 데이터
        String jsonString = null;
        // 최종 지역 주소 변수
        String address_name = null;

        try {
            // 입력 받은 주소값을 UTF-8로 인코딩하여 다시 저장
            roadFullAddr = URLEncoder.encode(roadFullAddr, "UTF-8");

            // API URL과 쿼리로 입력 받은 주소 합쳐 날릴 url
            String addr = apiUrl + "?query=" + roadFullAddr;

            // url 주소 등록
            URL url = new URL(addr);
            URLConnection conn = url.openConnection();
            // 카카오 API 를 사용하기 위해 Access 권한을 커넥션에 설정
            conn.setRequestProperty("Authorization", "KakaoAK " + apiKey);

            BufferedReader rd = null;
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuffer docJson = new StringBuffer();

            String line;

            while ((line = rd.readLine()) != null) {
                docJson.append(line);
            }

            // 입력받은 키워드로 뽑혀져 나온 json 데이터를 String으로 저장
            jsonString = docJson.toString();

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
            };
            Map<String, Object> jsonMap = mapper.readValue(jsonString, typeRef);

            @SuppressWarnings("unchecked")
            List<Map<String, String>> docList = (List<Map<String, String>>) jsonMap.get("documents");

            Map<String, String> adList = (Map<String, String>) docList.get(0);
            address_name = adList.get("address_name");
            System.out.println("주소 이름 : " + address_name);

            rd.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address_name;
    }

    // 검색 추가 2022-10-30 doosan add

    @Transactional // 수정 필요
    public Page<PostResponseDto> getPost(String keyword, String type, int page) {
        Pageable pageable = PageRequest.of(page - 1, 30);
        Page<Post> postList;
        Page<Post> postList2;
        Page<Post> postList3;


        postList = postRepository.findByTitleLike(keyword, pageable);
        System.out.println("데이터 확인 임시 : " + postList);

        Page<PostResponseDto> postResponseDtoList = new PostResponseDto().toDtoList(postList);
        for (PostResponseDto postResponseDto : postResponseDtoList) {
            System.out.println("확인~~~~~ : " + postResponseDto.toString());
        }
        return postResponseDtoList;
    }
}