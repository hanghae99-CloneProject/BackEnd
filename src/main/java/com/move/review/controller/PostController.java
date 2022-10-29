package com.move.review.controller;

import com.move.review.controller.request.PostRequestDto;
import com.move.review.controller.response.PostResponseDto;
import com.move.review.exception.PrivateResponseBody;
import com.move.review.exception.StatusCode;
import com.move.review.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/bunjang")
@Controller
public class PostController {

    private final PostService postService;

    // 게시글 작성 (미디어 포함)
    @ResponseBody
    @PostMapping(value = "/posts", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostResponseDto> writePost(
            @RequestPart(value = "media", required = false) List<MultipartFile> multipartFiles,
            @RequestPart(value = "post") PostRequestDto postRequestDto, // 게시글 작성을 위한 기입 정보들
            HttpServletRequest request) { // 현재 로그인한 유저의 인증 정보를 확인하기 위한 HttpServletRequest

        log.info("업로드 요청 미디어 파일들 존재 확인 : {}", multipartFiles);
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

//        return new ResponseEntity<>(
//                new PrivateResponseBody(StatusCode.OK, postService.writePost(multipartFiles, postRequestDto, request)), HttpStatus.OK);
        return postService.writePost(multipartFiles, postRequestDto, request);
    }

    // 게시글 수정 (미디어 포함)
    @ResponseBody
    @PutMapping(value = "/posts/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @RequestPart(value = "media", required = false) List<MultipartFile> multipartFiles,
            @RequestPart(value = "post") PostRequestDto postRequestDto, // 게시글 작성을 위한 기입 정보들
            HttpServletRequest request) {

        log.info("업로드 요청 미디어 파일들 존재 확인 : {}", multipartFiles);
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


//        return new ResponseEntity<>(
//                new PrivateResponseBody(StatusCode.OK, postService.updatePost(postId, multipartFiles, postRequestDto, request)), HttpStatus.OK);
        return postService.updatePost(postId, multipartFiles, postRequestDto, request);
    }

    // 게시글 삭제
    @ResponseBody
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            HttpServletRequest request) {

        return postService.deletePost(postId, request);
    }

    // 특정 게시글 1개 상세 조회
    @ResponseBody
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponseDto> getPost(
            @PathVariable Long postId) {

        return postService.getPost(postId);
    }


    // 게시글 목록 조회
    @ResponseBody
    @GetMapping("/posts")
    public ResponseEntity<ArrayList<HashMap<String, String>>> getAllPost() {

        return postService.getAllPost();
    }


}
