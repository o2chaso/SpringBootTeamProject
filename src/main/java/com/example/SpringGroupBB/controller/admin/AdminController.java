package com.example.SpringGroupBB.controller.admin;

import com.example.SpringGroupBB.common.Pagination;
import com.example.SpringGroupBB.dto.PageDTO;
import com.example.SpringGroupBB.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor

public class AdminController {
  private final AdminService adminService;
  private Pagination pagination;

  @GetMapping("/member/memberList")
  public String memberListGet(Model model, PageDTO pageDTO) {
    pageDTO.setSection("member");
    pageDTO = pagination.pagination(pageDTO);

    model.addAttribute("pageDTO", pageDTO);
    return "admin/member/memberList";
  }
}
