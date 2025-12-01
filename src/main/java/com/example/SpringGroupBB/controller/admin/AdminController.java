package com.example.SpringGroupBB.controller.admin;

import com.example.SpringGroupBB.common.Pagination;
import com.example.SpringGroupBB.dto.MemberDTO;
import com.example.SpringGroupBB.dto.PageDTO;
import com.example.SpringGroupBB.dto.ProductDTO;
import com.example.SpringGroupBB.service.MemberService;
import com.example.SpringGroupBB.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final Pagination pagination;
  private final MemberService memberService;
  private final ProductService productService;

  @GetMapping("/member/memberList")
  public String memberListGet(Model model, PageDTO pageDTO) {
    pageDTO.setSection("member");
    pageDTO = pagination.pagination(pageDTO);

    model.addAttribute("pageDTO", pageDTO);
    model.addAttribute("userCsrf", true);
    return "admin/member/memberList";
  }

  @ResponseBody
  @PostMapping("/member/memberDelete/{id}")
  public String memberDeletePost(@PathVariable Long id) {
    return memberService.deleteMember(id);
  }

  @ResponseBody
  @GetMapping("/member/detail/{id}")
  public MemberDTO memberDetailPost(@PathVariable Long id) {
    return memberService.searchMember(id);
  }


  @GetMapping("/product/productList")
  public String productListGet(Model model, PageDTO pageDTO) {

    pageDTO.setSection("product");
    pageDTO = pagination.pagination(pageDTO);

    model.addAttribute("userCsrf", true);
    model.addAttribute("pageDTO", pageDTO);
    return "admin/product/productList";
  }

  @GetMapping("/product/productInput")
  public String productInputGet(Model model) {
    model.addAttribute("productDTO", new ProductDTO());

    return "admin/product/productInput";
  }

  @PostMapping("/product/productInput")
  public String productInputPost(RedirectAttributes rttr,
                                  MultipartFile sFile,
                                  @Valid ProductDTO productDTO,
                                  BindingResult bindingResult,
                                  HttpServletRequest request) {

    if(bindingResult.hasErrors()) {
      return "admin/product/productInput";
    }
    else if(sFile == null || sFile.isEmpty()) {
      rttr.addFlashAttribute("message", "파일 업로드할 파일을 선택하세요");
      return "redirect:/admin/product/productInput";
    }
    String realPath = request.getServletContext().getRealPath("/admin/product/");

    productService.insertProduct(sFile, realPath, productDTO);
    rttr.addFlashAttribute("message","센서가 등록되었습니다. ");
    return "redirect:/admin/product/productList";
  }

  @ResponseBody
  @GetMapping("/product/detail/{id}")
  public ProductDTO productDetailGet(@PathVariable Long id) {
    return productService.searchProduct(id);
  }

  @ResponseBody
  @PostMapping("/product/productDelete/{id}")
  public String productDeletePost(@PathVariable Long id) {
    return productService.deleteProduct(id);
  }

  @GetMapping("/product/productUpdate/{id}")
  public String productUpdateGet(Model model, @PathVariable Long id) {
    ProductDTO productDTO = productService.searchProduct(id);
    System.out.println("productDTO :" + productDTO);
    model.addAttribute("userCsrf", true);
    model.addAttribute("productDTO", productDTO);
    return "admin/product/productUpdate";
  }

  @PostMapping("/product/productUpdate/{id}")
  public String productUpdatePost(RedirectAttributes rttr, HttpServletRequest request,
                                  @Valid ProductDTO productDTO,
                                  BindingResult bindingResult) {
    if(bindingResult.hasErrors()) {
      return "admin/product/productUpdate";
    }

    String realPath = request.getServletContext().getRealPath("/admin/product");
    boolean res = productService.updateProduct(productDTO, realPath);
    if(res) {
      rttr.addFlashAttribute("message", "센서 수정 완료되었습니다");
      return "redirect:/admin/product/productList";
    }
    else {
      rttr.addFlashAttribute("message","센서 수정 실패입니다 다시 확인해주세요.");
      return "redirect:admin/product/productUpdate/"+productDTO.getId();
    }
  }
}
