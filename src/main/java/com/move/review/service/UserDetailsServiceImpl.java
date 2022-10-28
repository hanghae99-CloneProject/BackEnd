package com.move.review.service;

import com.move.review.domain.Member;
import com.move.review.domain.UserDetailsImpl;
import com.move.review.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String memberName) throws UsernameNotFoundException {
    Member member = memberRepository.findByMemberName(memberName)
            .orElseThrow(() -> new UsernameNotFoundException("Can't find " + memberName));

    return new UserDetailsImpl(member);
  }
}
