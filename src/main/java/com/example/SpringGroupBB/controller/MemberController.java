package com.example.SpringGroupBB.controller;

import com.example.SpringGroupBB.common.ProjectProvide;
import com.example.SpringGroupBB.constant.UserDel;
import com.example.SpringGroupBB.dto.MemberDTO;
import com.example.SpringGroupBB.entity.Member;
import com.example.SpringGroupBB.repository.MemberRepository;
import com.example.SpringGroupBB.service.MemberService;
import com.example.SpringGroupBB.validation.CreateGroup;
import com.example.SpringGroupBB.validation.UpdateGroup;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  /* Login */
  @GetMapping("/memberLogin")
  public String memberLoginGet() { return "member/memberLogin"; }

  @GetMapping("/login/error")
  public String loginErrorGet(RedirectAttributes rttr) {
    rttr.addFlashAttribute("message", "아이디와 비밀번호를 확인해주세요");
    return "redirect:/member/memberLogin";
  }

  @GetMapping("/memberLogout")
  public String memberLogoutGet(Authentication authentication,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes rttr,
                                Model model,
                                HttpSession session) {
    String name = session.getAttribute("sName").toString();
    if(authentication != null) {
      session.invalidate();
      new SecurityContextLogoutHandler().logout(request, response, authentication);
    }
    String message = (String) model.asMap().get("message");
    if (message != null) {
      return "member/memberLogin";
    }

    rttr.addFlashAttribute("message",name + "님 로그아웃되었습니다.");
    return "redirect:/member/memberLogin";
  }

  @GetMapping("/memberMain")
  public String memberMain() { return "member/memberMain"; }

  @GetMapping("/memberLoginOk")
  public String memberLoginOKGet(RedirectAttributes rttr,
                                  Authentication authentication,
                                  HttpSession session
                                  ) {
    String email = authentication.getName();
    Optional<Member> opMember = memberRepository.findByEmail(email);

    String strLevel = opMember.get().getRole().toString();
    if(strLevel.equals("ADMIN")) strLevel = "관리자";
    else if(strLevel.equals("USER")) strLevel = "정회원";

    session.setAttribute("sName", opMember.get().getName());
    session.setAttribute("strLevel", strLevel);

    rttr.addFlashAttribute("message", opMember.get().getName() + "님 로그인 되셨습니다.");

    return "redirect:/member/memberMain";
  }


  /* Join*/
  @GetMapping("/memberJoin")
  public String memberJoinGet(Model model) {
    model.addAttribute("memberDTO", new MemberDTO());
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
      if(emailSw == 1) model.addAttribute("emailSw", 1);
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
  public String memberEmailCheckPost(String email, HttpSession session) throws MessagingException {
    return memberService.sendMail(email, session);
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
  public void memberEamilCheckNoPost(HttpSession session) throws MessagingException {
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
    int res = memberService.updateMemberPassword(email, currentPassword, newPassword);

    if(res != 1) {
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
    memberService.memberUpdate(email, memberDTO);
    rttr.addFlashAttribute("message", "회원정보가 수정되었습니다");
    return"redirect:/member/memberMain";
  }

  @GetMapping("/memberDelete")
  public String memberDeleteGet(Model model) {
    model.addAttribute("userCsrf", true);
    return "member/memberDelete";
  }

  @ResponseBody
  @PostMapping("/memberPwdCheck")
  public String memberPwdCheckPost(Authentication authentication, String password) {
    String email = authentication.getName();
    Member member = memberRepository.findByEmail(email).get();
    if (passwordEncoder.matches(password, member.getPassword())) {
      member.setUserDel(UserDel.OK);
      memberRepository.save(member);
      return "OK";
    } else {
      return "NO";
    }
  }
}
