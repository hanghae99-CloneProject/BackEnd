package com.move.Bunjang.controller;

import com.move.Bunjang.controller.response.PostResponseDto;
import com.move.Bunjang.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// SearchController 추가 2022-10 - 30
@RequiredArgsConstructor // 그냥 controller로 되어있어서 원래는 responsebody를 넣어야 하는데 안붙어 있어서 결과가 null로 찍혔다.
@RestController // trouble shooting에 써야지 RestController 어노테이션이 없어서 원래는 responsebody를 넣어야 하는데 안붙어 있어서 결과가 null로 찍혔다. 2022-10-31 오후 8시 30분
public class SearchController {
    private final PostService postService;

    @GetMapping("/bunjang/search")
    public Page<PostResponseDto> getPost(@RequestParam(required = false,value="keyword") String keyword,
                                         @RequestParam(required = false,value="type") String type,
                                         @RequestParam(required = false,value = "page") int page, Model model) {

       // @RequestBody(value="post") SearchRequestDto searchRequestDto;

        Page<PostResponseDto> postList = postService.getPost(keyword, type, page);

        model.addAttribute("totalPages", postList.getTotalPages());
        model.addAttribute("totalItems",postList.getTotalElements());
        model.addAttribute("keyword",keyword);
        model.addAttribute("type",type);
        model.addAttribute("current_page",page);

        int startIndex;
        int endIndex;
        long startCount = (page -1) * 30 + 1;
        long endCount = startCount + 30 - 1;

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        if(page/ 10 < 1) {
            startIndex = 1;
            System.out.println("startIndex = " + startIndex);

            endIndex = 10;
            if(endIndex >= postList.getTotalPages())
                endIndex = postList.getTotalPages();

        } else {
            if(page%10 ==0) {
                page-=1;
            }

            System.out.println("page =" + page);

            startIndex = page/10*10+1;
            endIndex = startIndex+9;

            if(endIndex>=postList.getTotalPages())
                endIndex = postList.getTotalPages();
        }
        model.addAttribute("startIndex",startIndex);
        model.addAttribute("endIndex",endIndex);

        model.addAttribute("postList", postList);
        model.addAttribute("keyword", keyword);


        return postService.getPost(keyword, type, page);
    }
}
