package com.example.SpringGroupBB.controller;


import com.example.SpringGroupBB.common.Pagination;
import com.example.SpringGroupBB.dto.LoginHistoryDTO;
import com.example.SpringGroupBB.dto.MemberDTO;
import com.example.SpringGroupBB.dto.PageDTO;
import com.example.SpringGroupBB.dto.ProductDTO;
import com.example.SpringGroupBB.entity.Complaint;
import com.example.SpringGroupBB.repository.ComplaintRepository;
import com.example.SpringGroupBB.service.AdminService;
import com.example.SpringGroupBB.service.MemberService;
import com.example.SpringGroupBB.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private final AdminService adminService;
  private final Pagination pagination;
  private final ComplaintRepository complaintRepository;
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

  @GetMapping("/adminDashBoard")
  public String adminDashBoardGet() {
    return "admin/adminDashBoard";
  }

  @GetMapping("/menu")
  public String adminMenuGet() {
    return "admin/adminMenu";
  }

  @GetMapping("/member/memberHistory")
  public String memberHistoryGet(Model model, PageDTO pageDTO) {
    String range = pageDTO.getDateRange();
    if (range != null && ! range.isBlank() && range.contains("~")) {
      String[] parts = pageDTO.getDateRange().split("~");
      pageDTO.setStartDate(LocalDate.parse(parts[0].trim()));
      pageDTO.setEndDate(LocalDate.parse(parts[1].trim()));
    }
    pageDTO.setSection("History");
    pageDTO = memberService.searchMemberHistory(pageDTO);
    model.addAttribute("pageDTO", pageDTO);

    return "admin/member/memberHistory";
  }

  @GetMapping("/member/memberHistoryGraph")
  public String memberGraphGet(Model model, PageDTO dto) throws JsonProcessingException {
    String range = dto.getDateRange();
    if (range != null && ! range.isBlank() && range.contains("~")) {
      String[] parts = range.split("~");
      dto.setStartDate(LocalDate.parse(parts[0].trim()));
      dto.setEndDate(LocalDate.parse(parts[1].trim()));
    }
    else {
      dto.setEndDate(LocalDate.now());
      dto.setStartDate(dto.getEndDate().minusDays(7));
    }
    Map<String, Long> loginCountByDate = memberService.searchLoginDataForChart(dto);
    List<MemberDTO> mDTOList = memberService.searchMemberIdAndEmailAnaName();

    model.addAttribute("loginCountByDate", new ObjectMapper().writeValueAsString(loginCountByDate));
    model.addAttribute("pageDTO", dto);
    model.addAttribute("mDTOList", mDTOList);
    model.addAttribute("userCsrf", true);

    return "admin/member/memberHistoryGraph";
  }

  @ResponseBody
  @GetMapping("/member/memberHistoryDetail/{id}")
  public List<LoginHistoryDTO> memberHistoryDetailGet(Model model, @PathVariable Long id, String date, int hour) {
    return memberService.memberHistoryDetailGet(id, date, hour);
  }

  // 신고 리스트 보기
  @GetMapping("/complaint/complaintList")
  public String complaintListGet(Model model, PageDTO pageDTO) {
    pageDTO.setSection("complaint");
    pageDTO = pagination.pagination(pageDTO);

    int page = pageDTO.getPag()-1;
    int pageSize = pageDTO.getPageSize();
    String progressFilter = pageDTO.getPart();

    PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc("id")));

    Page<Complaint> complaintPage;
    if(progressFilter == null || progressFilter.isEmpty()) {
      complaintPage = complaintRepository.findAll(pageable);
    } else {
      complaintPage = complaintRepository.findByProgress(progressFilter, pageable);
    }
    List<Complaint> complaintList = complaintPage.getContent();

    model.addAttribute("pageDTO", pageDTO);
    model.addAttribute("complaintList", complaintList);
    model.addAttribute("complaintPage", complaintPage);
    model.addAttribute("complaints",complaintPage.getContent());
    model.addAttribute("userCsrf", true);
    return "admin/complaint/complaintList";
  }

  // 신고 상세 내역 보기
  @GetMapping("/complaint/complaintContent")
  public String complaintContentGet(Model model,
                                    @RequestParam("partId") int partId) {
    Complaint complaint = adminService.getComplaintSearch(partId);
    model.addAttribute("complaint", complaint);
    return "admin/complaint/complaintContent";
  }


  // 신고내역자료 '취소(S)/감추기(H)/삭제(D)'
  @ResponseBody
  @PostMapping("/complaint/complaintProcess")
  public int complaintProcessPost(@RequestParam String complaintSw,
                                  @RequestParam Long id,
                                  @RequestParam Long partId,
                                  @RequestParam String part) {
    int res = 0;
    String progress;

    switch(complaintSw) {
      case "D":
        res = adminService.setComplaintDelete(partId, part);
        progress = "처리완료(D)";
        break;
      case "H":
        res = adminService.setComplaintProcess(partId, "HI");
        progress = "처리중(H)";
        break;
      default:
        res = adminService.setComplaintProcess(partId, "NO");
        progress = "처리완료(S)";
    }

    if(res != 0) adminService.setComplaintProcessOk(id, progress);
    return res;
  }



}
