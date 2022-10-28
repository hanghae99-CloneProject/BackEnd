package com.move.review.repository;

import com.move.review.domain.Heart;
import com.move.review.domain.Member;
import com.move.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    Optional<Heart> findByMemberAndReview(Member member, Review review);
    Long countByReview(Review review);

    @Query(value = "select review_id from heart group by review_id having count(*) = (select max(heart_max) from ( select review_id, count(review_id) as heart_max from heart group by review_id) as result)", nativeQuery = true)
    Long heartMaxCount();

}
