package com.move.Bunjang.repository;

import com.move.Bunjang.domain.Member;
import com.move.Bunjang.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByModifiedAtDesc();

    List<Post> findAllByMember(Member member);


    // 검색 추가 2022- 10- 31

    // AGAINST MATCH -> LIKE 로 수정
    //SELECT * FROM post WHERE content like '%jordan3%';
    @Query(value = "SELECT * FROM post WHERE title like concat('%', :title, '%')", nativeQuery = true)
    Page<Post> findByTitleLike(String title, Pageable pageable);



    Page<Post> findAll(Pageable pageable);


}
