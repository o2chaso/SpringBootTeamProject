package com.example.SpringGroupBB.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {
  @GetMapping("/ceoMessage")
  public String ceoMessageGet() {
    return "company/ceoMessage";
  }
  @GetMapping("/management")
  public String managementGet() {
    return "company/management";
  }
  @GetMapping("/mapInfo")
  public String mapInfoGet(Model model) {
    model.addAttribute("userCsrf", true);
    return "company/mapInfo";
  }
}
