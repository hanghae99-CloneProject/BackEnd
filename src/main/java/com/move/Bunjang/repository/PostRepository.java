package com.move.Bunjang.repository;

import com.move.Bunjang.domain.Member;
import com.move.Bunjang.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByModifiedAtDesc();

    List<Post> findAllByMember(Member member);

}
