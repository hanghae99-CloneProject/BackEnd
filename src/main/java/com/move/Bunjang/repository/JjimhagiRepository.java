package com.move.Bunjang.repository;
import com.move.Bunjang.domain.Jjimhagi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JjimhagiRepository extends JpaRepository<Jjimhagi, Long> {
    Jjimhagi findByPostIdAndMemberId(Long postId, Long memberId);
}
