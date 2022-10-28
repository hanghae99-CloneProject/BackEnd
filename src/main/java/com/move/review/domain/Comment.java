package com.move.review.domain;

import com.move.review.controller.request.CommentRequestDto;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long commentId;

  @JoinColumn(name = "memberId", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @JoinColumn(name = "reviewId", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Review review;

  @Column(nullable = false)
  private String comment;

  @Column(nullable = false)
  private String memberName;

  public void update(CommentRequestDto commentRequestDto) {
    this.comment = commentRequestDto.getComment();
  }

  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }
}
