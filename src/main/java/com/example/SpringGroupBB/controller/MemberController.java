package com.example.SpringGroupBB.controller;

import com.example.SpringGroupBB.dto.MemberDTO;
import com.example.SpringGroupBB.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

  @GetMapping("/memberLoginOk")
  public String memberLoginOkGet(HttpSession session, Authentication authentication) {
    session.setAttribute("sMid", authentication.getName());
    return "redirect:/";
  }
  @GetMapping("/login/error")
  public String loginError(RedirectAttributes rttr) {
    rttr.addFlashAttribute("message", "로그인실패");
    return "redirect:/member/memberLogin";
  }

  @GetMapping("/memberJoin")
  public String memberJoin() {
    return "member/memberJoin";
  }
  @PostMapping("/memberJoin")
  public String memberPost(RedirectAttributes rttr, MemberDTO dto) {
    memberService.insertMember(dto);
    rttr.addFlashAttribute("message", "회원가입되었습니다.");
    return "redirect:/";
  }

  @GetMapping("/memberLogout")
  public String memberLogoutGet(RedirectAttributes rttr,
                                Authentication authentication,
                                HttpServletRequest request, HttpServletResponse response) {
    new SecurityContextLogoutHandler().logout(request, response, authentication);
    rttr.addFlashAttribute("message", "로그아웃되었습니다.");
    return "redirect:/";
  }
}
