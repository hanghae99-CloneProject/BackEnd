package com.move.review.configuration;

import com.move.review.domain.Member;
import com.move.review.jwt.TokenProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.move.review.domain.QMember.member;

@Configuration
public class AuditConfig implements AuditorAware<String> {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    private TokenProvider tokenProvider;

    @Override
    public Optional<String> getCurrentAuditor(){
        // 인증된 유저정보를 가져온다.
        //Member member = tokenProvider.getMemberFromAuthentication();

        // 로그인 기능 합치게 된다면 request를 이용해서 현재 유저의 정보를 추출한 뒤에 넘겨주도록 한다.
        // 임의의 유저 정보로 테스트
        Member test_member = jpaQueryFactory.selectFrom(member).where(member.id.eq(1L)).fetchOne();

        // 지금은 email 로 했지만 실제로 진행할떄는 id를 넣도록 한다.
        return Optional.of(test_member.getEmail());
    }

}