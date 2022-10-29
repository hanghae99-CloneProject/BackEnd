package com.move.review.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Post extends Timestamped{
    // 고유 아이디
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    // 게시글 제목
    @Column(nullable = false)
    private String title;

    // 작성자
    @Column(nullable = false)
    private String author;

    // 카테고리 수정 - 2022-10-29 오후 10시 13분
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String category;

    // 지역
    @Column(nullable = false)
    private String local;

    // 중고 혹은 신상품
    @Column(nullable = false)
    private String state;

    // 교환불가 혹은 교환가능
    @Column(nullable = false)
    private String trade;

    // 제품 가격
    @Column(nullable = false)
    private int price;

    // 게시글 내용
    @Column(nullable = false)
    private String content;

    // 태그
    @Column(nullable = false)
    private String tag;

    // 수량
    @Column(nullable = false)
    private int amount;

    // 게시글 작성 시 같이 업로드할 미디어 파일들 (존재하지 않을 수도 있음)
    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Media> medias;

    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;





}
