package com.move.Bunjang.controller;

import com.move.Bunjang.controller.request.PostRequestDto;
import com.move.Bunjang.controller.response.PostResponseDto;
import com.move.Bunjang.exception.PrivateResponseBody;
import com.move.Bunjang.exception.StatusCode;
import com.move.Bunjang.service.ImageUpload;
import com.move.Bunjang.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/bunjang")
@Controller
public class PostController {

    private final PostService postService;
    private final ImageUpload imageUpload;

    // 게시글 작성 (미디어 포함)
    @ResponseBody
    @PostMapping(value = "/posts", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PrivateResponseBody> writePost(
            @RequestBody PostRequestDto postRequestDto, // 게시글 작성을 위한 기입 정보들
            HttpServletRequest request) throws IOException { // 현재 로그인한 유저의 인증 정보를 확인하기 위한 HttpServletRequest

        // 게시글 작성 내용 확인
        log.info("작성 요청 게시글 제목 : {}", postRequestDto.getTitle());
        log.info("작성 요청 게시글 카테고리 : {}", postRequestDto.getCategory());
        log.info("작성 요청 게시글 지역 : {}", postRequestDto.getLocal());
        log.info("작성 요청 게시글 상태 : {}", postRequestDto.getState());
        log.info("작성 요청 게시글 교환여부 : {}", postRequestDto.getTrade());
        log.info("작성 요청 게시글 가격 : {}", postRequestDto.getPrice());
        log.info("작성 요청 게시글 내용 : {}", postRequestDto.getContent());
        log.info("작성 요청 게시글 태그 : {}", postRequestDto.getTag());
        log.info("작성 요청 게시글 수량 : {}", postRequestDto.getAmount());
        log.info("요청 헤더 : {}", request);

        return postService.writePost(postRequestDto, request);
    }

    // 게시글 수정 (미디어 포함)
    @ResponseBody
    @PutMapping(value = "/posts/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PrivateResponseBody> updatePost(
            @PathVariable Long postId, // 수정하고자 하는 게시글의 고유 ID
            @RequestBody PostRequestDto postRequestDto, // 게시글 작성을 위한 기입 정보들
            HttpServletRequest request) throws IOException { // 현재 로그인한 유저의 인증 정보를 확인하기 위한 HttpServletRequest

        // 수정 정보 확인
//        log.info("업로드 요청 미디어 파일들 존재 확인 : {}", multipartFiles);
        log.info("작성 요청 게시글 제목 : {}", postRequestDto.getTitle());
        log.info("작성 요청 게시글 카테고리 : {}", postRequestDto.getCategory());
        log.info("작성 요청 게시글 지역 : {}", postRequestDto.getLocal());
        log.info("작성 요청 게시글 상태 : {}", postRequestDto.getState());
        log.info("작성 요청 게시글 교환여부 : {}", postRequestDto.getTrade());
        log.info("작성 요청 게시글 가격 : {}", postRequestDto.getPrice());
        log.info("작성 요청 게시글 내용 : {}", postRequestDto.getContent());
        log.info("작성 요청 게시글 태그 : {}", postRequestDto.getTag());
        log.info("작성 요청 게시글 수량 : {}", postRequestDto.getAmount());
        log.info("요청 헤더 : {}", request);

        return postService.updatePost(postId, postRequestDto, request);
    }

    // 게시글 삭제
    @ResponseBody
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<PrivateResponseBody> deletePost(
            @PathVariable Long postId, // 삭제하고자 하는 게시글의 고유 ID
            HttpServletRequest request) { // 현재 로그인한 유저의 인증 정보를 확인하기 위한 HttpServletRequest

        return postService.deletePost(postId, request);
    }


    // 특정 게시글 1개 상세 조회
    @ResponseBody
    @GetMapping("/posts/get/{postId}")
    public ResponseEntity<PrivateResponseBody> getPost(
            @PathVariable Long postId) { // 조회하고자 하는 게시글의 고유 ID

        return postService.getPost(postId);
    }


    // 게시글 목록 조회
    @ResponseBody
    @GetMapping("/posts/get")
    public ResponseEntity<PrivateResponseBody> getAllPost(
            @PageableDefault(page =0, size = 10 ,sort ="createdAt",direction = Sort.Direction.DESC) Pageable pageable) { // 페이징 처리를 위한 인자값

        return postService.getAllPost(pageable);
    }


    // 이미지 업로드
    @ResponseBody
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PrivateResponseBody> mediaUpload(
            @RequestPart(value = "file") MultipartFile multipartFiles){ // 처음 등록된 이미지, 업데이트할 이미지

        return new ResponseEntity<>(new PrivateResponseBody(
                StatusCode.OK,imageUpload.fileUpload(multipartFiles)), HttpStatus.OK);
    }


    // 장바구니
    @ResponseBody
    @PostMapping("/posts/collect/{postId}")
    public ResponseEntity<PrivateResponseBody> collectPost(
            @PathVariable Long postId, // 해당 게시글 고유 ID
            HttpServletRequest request){ // 현재 로그인한 유저의 인증 정보를 확인하기 위한 HttpServletRequest

        return postService.collectPost(postId, request);
    }

    // 장바구니에 담긴 게시글들 조회
    @ResponseBody
    @GetMapping("/posts/collect")
    public ResponseEntity<PrivateResponseBody> viewMyCollect(
            HttpServletRequest request){ // 현재 로그인한 유저의 인증 정보를 확인하기 위한 HttpServletRequest

        return postService.viewMyCollect(request);
    }

}
