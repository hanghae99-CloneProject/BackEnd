package com.move.review.domain;

import com.move.review.controller.request.ReviewRequestDto;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reviewId;

  @JoinColumn(name = "memberId", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  // 영화 포스터
  @Column(nullable = false)
  private String image;

  // 영화 제목
  @Column(nullable = false)
  private String movieTitle;

  // 장르
  @Column(nullable = false)
  private String genre;

  // 별점
  @Column(nullable = false)
  private int rating;

  // 리뷰 제목
  @Column(nullable = false)
  private String reviewTitle;

  // 리뷰 내용
  @Column(nullable = false)
  private String reviewContent;

  // 작성자
  @Column(nullable = false)
  private String memberName;

  // 댓글
  @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  // 좋아요
  @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Heart> hearts = new ArrayList<>();

  public void update(ReviewRequestDto reviewRequestDto) {
    this.reviewTitle = reviewRequestDto.getReviewTitle();
    this.reviewContent = reviewRequestDto.getReviewContent();
  }

  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }

}