package com.example.SpringGroupBB.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/technology")
public class TechnologyController {

  @GetMapping("/technologyPage")
  public String technologyPage(){
    return "/technology/technologyPage";
  }
}
