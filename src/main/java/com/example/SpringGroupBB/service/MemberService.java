package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.common.ProjectProvide;
import com.example.SpringGroupBB.constant.UserDel;
import com.example.SpringGroupBB.dto.MemberDTO;
import com.example.SpringGroupBB.entity.Member;
import com.example.SpringGroupBB.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final ProjectProvide projectProvide;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<Member> opMember = Optional.ofNullable(memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(email+"님의 회원 정보가 없습니다. ")));

    boolean isActive = opMember.get().getUserDel() == UserDel.NO;
    return User.builder()
            .username(opMember.get().getEmail())
            .password(opMember.get().getPassword())
            .roles(opMember.get().getRole().toString())
            .disabled(!isActive)
            .build();
  }

  public void insertMember(MemberDTO dto) {
    Member member = Member.dtoToEntity(dto, passwordEncoder);
    Member memberRes = saveMember(member);
  }

  public Member saveMember(Member member) {
    // 회원 이메일 중복체크 메소드 호출
    validateMemberEmailDuplicationCheck(member.getEmail());
    return memberRepository.save(member);
  }

  private void validateMemberEmailDuplicationCheck(String email) {
    // 회원 이메일 중복체크
    Optional<Member> opMember = memberRepository.findByEmail(email);

    if(opMember.isPresent()) { throw new IllegalStateException("이미 존재하는 회원입니다."); }
  }

  public String sendMail(String email, HttpSession session) throws MessagingException {

    String emailKey = UUID.randomUUID().toString().substring(0, 8);
    session.setAttribute("sEmailKey", emailKey);

    projectProvide.mailSend(email, "이메일 인증키입니다.", "이메일 인증키 : "+emailKey);
    return "OK";
  }

  public String memberEmailCheckOk(String checkKey, HttpSession session) {
    String emailKey = (String) session.getAttribute("sEmailKey");
    if(emailKey.equals(checkKey)) {
      session.removeAttribute("sEmailKey");
      return "OK";
    }
    else {
      return "NO";
    }
  }

  public int updateMemberPassword(String email, String currentPassword, String newPassword) {
    Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));

    if(!passwordEncoder.matches(currentPassword, member.getPassword())) return 0;
    else {
      member.setPassword(passwordEncoder.encode(newPassword));
      memberRepository.save(member);
      return 1;
    }
  }

  public MemberDTO memberProfileUpdateGet(String email) {
    MemberDTO memberDTO = MemberDTO.entityToDto(memberRepository.findByEmail(email));

    String[] tel = memberDTO.getTel().split("-");
    String[] address= memberDTO.getAddress().split("/");

    memberDTO.setTel2(tel[1]);
    memberDTO.setTel3(tel[2]);
    memberDTO.setPostcode(address[0].trim());
    memberDTO.setRoadAddress(address[1].trim());
    memberDTO.setDetailAddress(address[2].trim());
    memberDTO.setExtraAddress(address[3].trim());
    return memberDTO;
  }

  public void memberUpdate(String email, MemberDTO memberDTO) {
    Member member = memberRepository.findByEmail(email)
            .orElseThrow(() ->  new IllegalStateException("존재하지 않는 회원입니다."));

    member.setName(memberDTO.getName());
    member.setTel(memberDTO.getTel());
    member.setAddress(memberDTO.getAddress());
    member.setBirthday(memberDTO.getBirthday());
    memberRepository.save(member);
  }

}
