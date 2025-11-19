package com.example.SpringGroupBB.controller;

import com.example.SpringGroupBB.dto.MemberDTO;
import com.example.SpringGroupBB.entity.MemberEntity;
import com.example.SpringGroupBB.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping ("/member")
@RequiredArgsConstructor
public class MemberController {
  private final MemberService memberService;
  @Autowired
  PasswordEncoder passwordEncoder;

  /* Login */
  @GetMapping("/memberLogin")
  public String memberLoginGet() {
    return "member/memberLogin";
  }

  /* Login OK */
  @GetMapping("/memberLoginOk")
  public String memberLoginOkGet(HttpServletRequest request, HttpServletResponse response,
                                 RedirectAttributes rttr, Authentication authentication,
                                 HttpSession session) {
    String email = authentication.getName();
    System.out.println("로그인된 이메일" + email);

    // HTTP Session에 필요한 정보 저장
    Optional<MemberEntity> opMember = memberService.selectMemberEmail(email);

    return "redirect:/member/memberMain";
  }

  /* LogOut */
  @GetMapping("/memberLogout")
  public String memberLogoutGet(Authentication authentication, RedirectAttributes rttr,
                                HttpServletRequest request, HttpServletResponse response,
                                HttpSession session) {
    if(authentication != null) {
      rttr.addFlashAttribute("message", session.getAttribute("sName").toString() + "님 로그아웃 되었습니다.");
      session.invalidate();
      new SecurityContextLogoutHandler().logout(request, response, authentication);
    }
    return "redirect:/member/memberLogin";
  }

  @GetMapping("/memberJoin")
  public String memberJoinGet(Model model) {
    model.addAttribute("memberDTO", new MemberDTO());
    return "member/memberJoin";
  }

  @PostMapping("/memberJoin")
  public String memberJoinPost(@Valid MemberDTO dto,
                               BindingResult bindingResult, RedirectAttributes rttr) {
    if(bindingResult.hasErrors()) {return "member/memberJoin";}
    try{
      MemberEntity memberRes =  memberService.saveMember(dto);
      rttr.addFlashAttribute("message", "회원에 가입 되었습니다.");

      return "redirect:/member/memberLogin";
    } catch (IllegalStateException e) {
      rttr.addFlashAttribute("message", e.getMessage());

      return "redirect:/member/memberJoin";
    }
  }

  @GetMapping("/memberMain")
  public String memberMainGet(Model model, Authentication authentication) {
    model.addAttribute("userCsrf", true);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return "member/memberMain";
  }

}
