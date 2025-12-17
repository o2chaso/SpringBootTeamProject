package com.example.SpringGroupBB.controller;

import com.example.SpringGroupBB.custom.CustomOAuth2User;
import com.example.SpringGroupBB.dto.MemberDTO;
import com.example.SpringGroupBB.entity.LoginHistory;
import com.example.SpringGroupBB.entity.Member;
import com.example.SpringGroupBB.service.BoardService;
import com.example.SpringGroupBB.service.MemberService;
import com.example.SpringGroupBB.validation.CreateGroup;
import com.example.SpringGroupBB.validation.UpdateGroup;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final BoardService boardService;

  /* Login */
  @GetMapping("/memberLogin")
  public String memberLoginGet() { return "member/memberLogin"; }

  @GetMapping("/memberLogout")
  public String memberLogoutGet(Authentication authentication,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes rttr,
                                Model model,
                                HttpSession session) {

    String name = session.getAttribute("sName").toString();

    session.invalidate();
    new SecurityContextLogoutHandler().logout(request, response, authentication);

    //카카오 쿠키 삭제
    Cookie cookie = new Cookie("oauth2_auth_request", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);

    String message = (String) model.asMap().get("message");
    if (message != null) {
      rttr.addFlashAttribute("message", message);
      return "redirect:/member/memberLogin";
    }

    rttr.addFlashAttribute("message",name + "님 로그아웃되었습니다.");
    return "redirect:/member/memberLogin";
  }

  @GetMapping("/login/error")
  public String loginErrorGet(RedirectAttributes rttr ,HttpSession session) {
    Object errorMsg = session.getAttribute("sErrorMsg");
    String message;

    if(errorMsg != null) {
      message = errorMsg.toString();
      session.removeAttribute("sErrorMsg");
    }
    else {
      message = "아이디와 비밀번호를 확인해주세요";
    }
    rttr.addFlashAttribute("message", message);
    return "redirect:/member/memberLogin";
  }

  @GetMapping("/memberLoginOk")
  public String memberLoginOKGet(RedirectAttributes rttr,
                                  HttpServletRequest request,
                                  Authentication authentication,
                                  HttpSession session ) {

    Member member = memberService.searchMember(authentication);

    String strLevel = member.getRole().getKorean();

    if (authentication instanceof OAuth2AuthenticationToken) {
      session.setAttribute("loginMethod", "kakao");
    }
    else if (authentication instanceof UsernamePasswordAuthenticationToken) {
      session.setAttribute("loginMethod", "db");
    }
    String loginMethod = (String) session.getAttribute("loginMethod");

    LoginHistory loginHistory = LoginHistory.builder()
            .member(member)
            .ip(request.getRemoteAddr())
            .loginMethod(loginMethod)
            .createDate(LocalDateTime.now())
            .build();

    memberService.insertLoginHistory(loginHistory);

    session.setAttribute("sName", member.getName());
    session.setAttribute("strLevel", strLevel);
    session.setAttribute("sEmail", member.getEmail());

    rttr.addFlashAttribute("message", member.getName() + "님 로그인 되셨습니다.");
    return "redirect:/sensor/sensorLayout";
  }

  @GetMapping("/kakaoLogout")
  public String kakaoLogoutGet() {
    String clintId = "a108bb7f193bda9633e8a027477d1939";
    String logoutRedirectUri = "http://49.142.157.252/SpringGroupBB/member/memberLogout";

    String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout?client_id="+clintId+
            "&logout_redirect_uri="+logoutRedirectUri;

    return "redirect:" + kakaoLogoutUrl;
  }

  @GetMapping("/memberMain")
  public String memberMain(Model model, Authentication authentication) {
    Member member = memberService.searchMember(authentication);
    int boardCnt = boardService.searchEmailBoardCnt(member.getEmail());
    int boardReplyCnt = boardService.searchEmailBoardReplyCnt(member.getEmail());

    model.addAttribute("userCsrf", true);
    model.addAttribute("boardCnt", boardCnt);
    model.addAttribute("boardReplyCnt", boardReplyCnt);
    model.addAttribute("member", member);

    return "member/memberMain";
  }

  /* Join*/
  @GetMapping("/memberJoin")
  public String memberJoinGet(Model model, MemberDTO memberDTO) {

    model.addAttribute("userCsrf", true);
    return "member/memberJoin";
  }

  @PostMapping("/memberJoin")
  public String memberJoinPost(RedirectAttributes rttr,
                               Model model,
                               @Validated(CreateGroup.class) MemberDTO dto,
                               BindingResult bindingResult,
                               @RequestParam(name ="emailSw", defaultValue = "0", required = false) int emailSw) {

    if(bindingResult.hasErrors()) {
      if(emailSw == 1) model.addAttribute("emailSw", emailSw);
      model.addAttribute("userCsrf", true);
      return "member/memberJoin";
    }
    try {
      memberService.insertMember(dto);
      rttr.addFlashAttribute("message", "회원 가입되셨습니다.");
      return "redirect:/member/memberLogin";

    } catch (IllegalStateException e) {
      rttr.addFlashAttribute("message", "회원가입 실패입니다. "+e.getMessage());
      return "redirect:/member/memberJoin";
    }
  }

  // 이메일 인증
  @ResponseBody
  @PostMapping("/memberEmailCheck")
  public String memberEmailCheckPost(String email, int flag, HttpSession session) throws MessagingException {
    return memberService.sendMail(email, flag,  session);
  }

  // 이메일 인증 키 인증
  @ResponseBody
  @PostMapping("/memberEmailCheckOk")
  public String memberEmailCheckOkPost(String checkKey, HttpSession session) {
    return memberService.memberEmailCheckOk(checkKey, session);
  }

  // 이메일 인증 시간 초과
  @ResponseBody
  @PostMapping("/memberEmailCheckNo")
  public void memberEmailCheckNoPost(HttpSession session) throws MessagingException {
    session.removeAttribute("sEmailKey");
  }

  // 비밀번호변경
  @GetMapping("/memberPasswordChange")
  public String memberPasswordChangeGet(Model model) {
    model.addAttribute("userCsrf", true);
    return "member/memberPasswordChange";
  }

  @PostMapping("/memberPasswordChange")
  public String memberPasswordChangePost(RedirectAttributes rttr,
                                         String newPassword,
                                         String currentPassword,
                                         Authentication authentication) {
    String email = authentication.getName();
    boolean res = memberService.updateMemberPassword(email, currentPassword, newPassword);

    if(!res) {
      rttr.addFlashAttribute("message","비밀번호를 정확하게 입력해 주세요.");
      return "redirect:/member/memberPasswordChange";
    }
    else {
      rttr.addFlashAttribute("message", "비밀번호가 변경되셨습니다. 로그아웃 되었습니다 다시 로그인 해주세요.");
      return "redirect:/member/memberLogout";
    }
  }

  @GetMapping("/memberProfileUpdate")
  public String memberProfileUpdateGet(Model model,
                                       Authentication authentication) {
    String email = authentication.getName();
    MemberDTO memberDTO = memberService.memberProfileUpdateGet(email);

    model.addAttribute("memberDTO", memberDTO);
    return "/member/memberProfileUpdate";
  }

  @PostMapping("/memberProfileUpdate")
  public String memberProfileUpdatePost(Authentication authentication,
                                        RedirectAttributes rttr,
                                        @Validated(UpdateGroup.class) MemberDTO memberDTO,
                                        BindingResult bindingResult) {
    if(bindingResult.hasErrors()) {
      return "/member/memberProfileUpdate";
    }

    String email = authentication.getName();
    memberService.updateMember(email, memberDTO);
    rttr.addFlashAttribute("message", "회원정보가 수정되었습니다");
    return"redirect:/member/memberMain";
  }

  @GetMapping("/memberDelete")
  public String memberDeleteGet(Model model) {
    model.addAttribute("userCsrf", true);
    return "member/memberDelete";
  }

  @ResponseBody
  @PostMapping("/memberDelete")
  public String memberPwdCheckPost(Authentication authentication, String password) {
    String email = authentication.getName();
    return memberService.deleteMember(email, password);
  }

  @GetMapping("/kakaoJoin")
  public String kakaoJoinGet(Model model, HttpSession session, HttpServletRequest servletRequest, MemberDTO memberDTO) throws JsonProcessingException {
    String email = session.getAttribute("sKakaoEmail").toString();
    String nickName = session.getAttribute("sKakaoNickname").toString();

    memberDTO.setEmail(email);
    memberDTO.setName(nickName);

    model.addAttribute("MemberDTO", memberDTO);

    String message = (String) session.getAttribute("sMessage");
    if (message != null) {
      model.addAttribute("message", message);
      session.removeAttribute("sMessage");
    }

    return "member/kakaoJoin";
  }

  @PostMapping("/kakaoJoin")
  public String kakaoJoinPost(RedirectAttributes rttr,
                              @Validated(CreateGroup.class) MemberDTO memberDTO,
                              BindingResult bindingResult,
                              HttpSession session) {
    if(bindingResult.hasErrors()) {
      return "member/kakaoJoin";
    }
    try {
      memberService.insertMember(memberDTO);
      CustomOAuth2User customUser = new CustomOAuth2User(
              null,
              memberDTO.getEmail(),
              memberDTO.getName()
      );

      session.removeAttribute("sKakaoEmail");
      session.removeAttribute("sKakaoNickname");

      rttr.addFlashAttribute("message", "회원 가입되셨습니다. 다시 로그인해주세요");
      return "redirect:/member/memberLogin";

    } catch (IllegalStateException e) {
      rttr.addFlashAttribute("message", "회원가입 실패입니다. "+e.getMessage());
      return "redirect:/member/kakaoJoin";
    }
  }

  // 프로필 파일 저장
  @PostMapping("/updateProfileImage")
  public String updateProfileImagePost(MultipartFile sFile, HttpServletRequest request,
                                       Authentication authentication,
                                       RedirectAttributes rttr) {
    String email = authentication.getName();
    if(sFile == null || sFile.isEmpty()) {
      rttr.addFlashAttribute("파일 업로드할 파일을 선택하세요");
      return "redirect:/member/memberMain";
    }

    String realPath = request.getServletContext().getRealPath("/member/profile/");

    memberService.updateProfileImage(sFile, email, realPath);
    rttr.addFlashAttribute("message", "프로필 사진이 변경되었습니다.");
    return "redirect:/member/memberMain";
  }

  @GetMapping("/memberMidFind")
  public String memberMidFindGet() {
    return "member/memberMidFind";
  }

  @PostMapping("/memberMidFind")
  public String memberMidFindPost(RedirectAttributes rttr, Model model, String name, String tel, LocalDate birthday) {

    List<String> emails= memberService.searchMemberEmailFind(name, tel, birthday);

    if(emails.isEmpty()) {
      rttr.addFlashAttribute("message", "회원정보가 없습니다.");
      return "redirect:/member/memberMidFind";
    }

    model.addAttribute("emails", emails);
    return "member/memberMidFindResult";
  }
  @GetMapping("/memberPwdFind")
  public String memberPwdFindGet(Model model) {

    model.addAttribute("userCsrf", true);
    return "member/memberPwdFind";
  }

  @PostMapping("/memberPwdFind")
  public String memberPwdFindPost(RedirectAttributes rttr, Model model, String email, String name) {
    Optional<Long> memberId = memberService.getMemberPwdFind(email, name);
    if(memberId.isEmpty()) {
      rttr.addFlashAttribute("message", "회원정보가 없습니다.");
      return "redirect:/member/memberPwdFind";
    }
    System.out.println("memberId : " + memberId.get());
    rttr.addFlashAttribute("message", "비밀번호를 변경해주세요.");
    rttr.addFlashAttribute("id", memberId.get());

    return "redirect:/member/memberPwdChange";
  }

  @GetMapping("/memberPwdChange")
  public String memberPwdChange() {
    return "member/memberPwdChange";
  }

  @PostMapping("/memberPwdChange")
  public String memberPwdChange(RedirectAttributes rttr, Long id, String newPassword, String confirmPassword) {
    System.out.println("id1 : "+id);
    if(!newPassword.equals(confirmPassword)) {
      rttr.addFlashAttribute("message", "비밀번호가 다릅니다. 다시 확인해주세요");
      return "redirect:/member/memberPwdChange";
    }
    try {
      memberService.setMemberPwdChange(id, newPassword);
    } catch (IllegalArgumentException e) {
      rttr.addFlashAttribute("message", e.getMessage());
      return "redirect:/member/memberPwdChange";
    }
    rttr.addFlashAttribute("message", "비밀번호가 변경되었습니다. 로그인 해주세요");
    return "redirect:/member/memberLogin";
  }
}
