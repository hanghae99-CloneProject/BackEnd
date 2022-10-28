package com.move.review.repository;

import com.move.review.domain.Review;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  List<Review> findAllByOrderByModifiedAtDesc();
}
