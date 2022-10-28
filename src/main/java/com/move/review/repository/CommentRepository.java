package com.move.review.repository;

import com.move.review.domain.Comment;
import com.move.review.domain.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByReview(Review review);
  Long countByReview(Review review);
}
