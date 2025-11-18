package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.dto.MemberDTO;
import com.example.SpringGroupBB.entity.Member;
import com.example.SpringGroupBB.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
  @Autowired
  PasswordEncoder passwordEncoder;

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String mid) throws UsernameNotFoundException {
    Member member = memberRepository.findByMid(mid).orElse(null);
    return User.builder()
            .username(member.getMid())
            .password(member.getPassword())
            .roles(member.getRole().toString())
            .build();
  }

  public MemberDTO selectMemberMid(String mid) {
    return MemberDTO.entityToDTO(memberRepository.findByMid(mid));
  }

  public void insertMember(MemberDTO dto) {
    memberRepository.save(Member.dtoToEntity(passwordEncoder, dto));
  }
}
