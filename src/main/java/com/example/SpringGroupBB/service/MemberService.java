package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.dto.MemberDTO;
import com.example.SpringGroupBB.entity.MemberEntity;
import com.example.SpringGroupBB.repository.MemberRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
  @Autowired
  PasswordEncoder passwordEncoder;

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    MemberEntity member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("해당사용자가 없습니다."));

    return User.builder()
            .username(member.getEmail())
            .password(member.getPassword())
            .authorities("USER")
            .build();
  }

  public Optional<MemberEntity> selectMemberEmail(String email) {
    return memberRepository.findByEmail(email);
  }

  // 회원 이메일 중복 체크
  public MemberEntity saveMember(@Valid MemberDTO dto) {
    validateMemberEmailDuplicationCheck(dto.getEmail());
    MemberEntity member = MemberEntity.dtoToEntity(dto, passwordEncoder);

    return memberRepository.save(member);
  }

  // Email Search
  private void validateMemberEmailDuplicationCheck(@NotEmpty(message = "메일은 필수 입력입니다.") @Email(message = "이메일 형식을 확인해주세요.") String email) {
    Optional<MemberEntity> opMember = memberRepository.findByEmail(email);
    if(opMember.isPresent()) {throw new IllegalStateException("중복된 이메일입니다.");}
  }

}
