package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.common.Pagination;
import com.example.SpringGroupBB.common.ProjectProvide;
import com.example.SpringGroupBB.constant.UserDel;
import com.example.SpringGroupBB.dto.LoginHistoryDTO;
import com.example.SpringGroupBB.dto.MemberDTO;
import com.example.SpringGroupBB.dto.PageDTO;
import com.example.SpringGroupBB.entity.LoginHistory;
import com.example.SpringGroupBB.entity.Member;
import com.example.SpringGroupBB.repository.LoginHistoryRepository;
import com.example.SpringGroupBB.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final ProjectProvide projectProvide;
  private final LoginHistoryRepository loginHistoryRepository;
  private final Pagination pagination;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(email+"님의 회원 정보가 없습니다. "));

    boolean isActive = member.getUserDel() == UserDel.NO;
    return User.builder()
            .username(member.getEmail())
            .password(member.getPassword())
            .roles(member.getRole().toString())
            .disabled(!isActive)
            .build();
  }

  public void insertMember(MemberDTO dto) {
    Member member = Member.dtoToEntity(dto, passwordEncoder);

    memberRepository.findByEmail(member.getEmail())
            .ifPresent(m -> { throw new IllegalStateException("이미 존재하는 회원입니다."); });

    memberRepository.save(member);
  }

  public MemberDTO searchMember(Long id) {
    Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    return MemberDTO.entityToDto(member);
  }

  public Member searchMember(String email) {

    return memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(email+"님의 회원 정보가 없습니다. "));
  }

  public Member searchMember(Authentication authentication) {
    return searchMember(authentication.getName());
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

  public boolean updateMemberPassword(String email, String currentPassword, String newPassword) {
    Member member = searchMember(email);

    if(!passwordEncoder.matches(currentPassword, member.getPassword())) return false;
    else {
      member.setPassword(passwordEncoder.encode(newPassword));
      memberRepository.save(member);
      return true;
    }
  }

  public MemberDTO memberProfileUpdateGet(String email) {
    MemberDTO memberDTO = MemberDTO.entityToDto(searchMember(email));

    String[] tel = memberDTO.getTel().split("-");
    String[] address= memberDTO.getAddress().split("/");

    if(memberDTO.getTel() != null && memberDTO.getTel().contains("-")) {
      String[] tels = memberDTO.getTel().split("-");
      if(tels.length >= 3) {
        memberDTO.setTel2(tels[1]);
        memberDTO.setTel3(tels[2]);
      }
    }

    if(memberDTO.getAddress() != null && memberDTO.getAddress().contains("/")) {
      String[] adds = memberDTO.getAddress().split("/");
      if(adds.length >= 4) {
        memberDTO.setPostcode(adds[0].trim());
        memberDTO.setRoadAddress(adds[1].trim());
        memberDTO.setDetailAddress(adds[2].trim());
        memberDTO.setExtraAddress(adds[3].trim());
      }
    }
    return memberDTO;
  }

  public void updateMember(String email, MemberDTO memberDTO) {
    Member member = searchMember(email);

    member.setName(memberDTO.getName());
    member.setTel(memberDTO.getTel());
    member.setAddress(memberDTO.getAddress());
    member.setBirthday(memberDTO.getBirthday());
    memberRepository.save(member);
  }

  public String deleteMember(String email, String password) {
    Member member = searchMember(email);

    if (passwordEncoder.matches(password, member.getPassword())) {
      member.setUserDel(UserDel.OK);
      member.setDelDate(LocalDateTime.now().plusDays(30));
      memberRepository.save(member);
      return "OK";
    } else {
      return "NO";
    }
  }

  public void updateProfileImage(MultipartFile sFile, String email, String realPath) {
    Member member = searchMember(email);

    String oFileName = sFile.getOriginalFilename();
    String ext = oFileName.substring(oFileName.lastIndexOf(".") + 1).toLowerCase();

    if (!List.of("jpg", "jpeg", "png", "gif").contains(ext)) {
      throw new RuntimeException("허용되지 않는 확장자");
    }

    String sFileName = new SimpleDateFormat("yyyyMMddHHmmss")
            .format(new Date())+"_"+oFileName;

    try {
      projectProvide.writeFile(sFile, sFileName, realPath);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (member.getProfileImage() != null &&
            !member.getProfileImage().equals("noImage.jpg")) {

      File oldFile = new File(realPath, member.getProfileImage());
      if (oldFile.exists()) oldFile.delete();
    }
    member.setProfileImage(sFileName);
    memberRepository.save(member);
  }
  public String deleteMember(Long id) {
    Member member = memberRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("존재하지 않는 회원입니다."));
    if(member.getUserDel()== UserDel.OK) {
      memberRepository.deleteById(id);
      return "OK";
    }
    else return "NO";
  }

  public void insertLoginHistory(LoginHistory loginHistory) {
    loginHistoryRepository.save(loginHistory);
  }

  public PageDTO searchMemberHistory(PageDTO pageDTO) {
    return pageDTO = pagination.pagination(pageDTO);
  }

  public Map<String, Long> searchLoginDataForChart(PageDTO dto) {

    LocalDateTime start = dto.getStartDate() != null ? dto.getStartDate().atStartOfDay() : null;
    LocalDateTime end = dto.getEndDate() != null ? dto.getEndDate().atTime(23, 59, 59) : null;
    if (dto.getMemberId() == null) dto.setMemberId(0L);

    boolean hasMember = dto.getMemberId() != null && dto.getMemberId() != 0;
    boolean isOneDay = start.toLocalDate().equals(end.toLocalDate());

    List<LoginHistory> loginHistories = hasMember
            ? loginHistoryRepository.findByMember_IdAndCreateDateBetween(dto.getMemberId(), start, end)
            : loginHistoryRepository.findByCreateDateBetween(start, end);

    Map<String, Long> result = new HashMap<>();

    for(LoginHistory l :loginHistories) {
      String key = isOneDay
              ? String.format("%02d", l.getCreateDate().getHour())
              : l.getCreateDate().toLocalDate().toString();
      result.put(key, result.getOrDefault(key, 0L) + 1);
    }
    return result;
  }

  public List<MemberDTO> searchMemberIdAndEmailAnaName() {
    return memberRepository.searchMemberIdAndEmailAnaName();
  }

  public  List<LoginHistoryDTO> memberHistoryDetailGet(Long id, String date, int hour) {
    LocalDate localDate = LocalDate.parse(date);
    LocalDateTime start = localDate.atTime(hour, 0);
    LocalDateTime end = localDate.atTime(hour, 59, 59);

    List<LoginHistory>  list =
            id == 0
                    ? loginHistoryRepository.findByCreateDateBetween(start, end)
                    : loginHistoryRepository.findByMember_IdAndCreateDateBetween(id, start, end);
    return list.stream()
            .map(LoginHistoryDTO::entityToDto)
            .collect(Collectors.toList());
  }

  public String searchMemberEmailFind(String name, String tel) {

    Optional<Member> opMember = memberRepository.findByNameAndTel(name, tel);
    if(opMember.isPresent()) {
      String email = opMember.get().getEmail();
      int atIndex = email.indexOf("@");
      if(atIndex <= 2) return email;

      StringBuilder masked = new StringBuilder();
      for (int i = 0; i < atIndex; i++) {
        if (i % 2 == 0) { // 짝수 인덱스는 그대로, 홀수는 *
          masked.append(email.charAt(i));
        } else {
          masked.append("*");
        }
      }
      masked.append(email.substring(atIndex)); // @ 뒤는 그대로
      return masked.toString();
    }
      else return "";

  }

  public Optional<Long> getMemberPwdFind(String email, String name) {
    return memberRepository.findByEmailAndName(email,name).map(Member::getId);
  }

  public void setMemberPwdChange(Long id, String newPassword) {
    Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));;

      member.setPassword(passwordEncoder.encode(newPassword));
      memberRepository.save(member);
  }
}