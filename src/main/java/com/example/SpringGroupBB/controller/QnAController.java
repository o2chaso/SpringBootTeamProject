package com.example.SpringGroupBB.controller;

import com.example.SpringGroupBB.common.Pagination;
import com.example.SpringGroupBB.constant.Progress;
import com.example.SpringGroupBB.dto.PageDTO;
import com.example.SpringGroupBB.dto.QnADTO;
import com.example.SpringGroupBB.entity.QnA;
import com.example.SpringGroupBB.repository.MemberRepository;
import com.example.SpringGroupBB.service.MemberService;
import com.example.SpringGroupBB.service.QnAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QnAController {
  private final QnAService qnaService;
  private final MemberService memberService;
  private final Pagination pagination;
  private final MemberRepository memberRepository;

  // 문의목록.
  @GetMapping("/qnaList")
  public String qnaListGet(Model model, PageDTO pageDTO) {
    pageDTO.setSection("qna");
    pageDTO = pagination.pagination(pageDTO);

    model.addAttribute("pageDTO", pageDTO);
    model.addAttribute("userCsrf", true);
    return "qna/qnaList";
  }

  // 문의작성 폼.
  @GetMapping("/qnaInput")
  public String qnaInputGet(Authentication authentication, Model model) {
    model.addAttribute("dto", memberRepository.findByEmail(authentication.getName()));
    model.addAttribute("userCsrf", true);
    return "qna/qnaInput";
  }
  // 문의작성 폼 유효성검사 실시간 피드백.
  @ResponseBody
  @PostMapping("qnaValid")
  public String[] qnaValide(@Valid QnADTO dto, BindingResult bindingResult) {
    // QnADTO 형식에 맞지 않으면.
    if(bindingResult.hasErrors()) {
      // 검출된 오류(QnADTO 형식에 맞지 않는)의 전체 개수만큼의 크기를 가진 String배열 생성.
      String[] error = new String[bindingResult.getAllErrors().size()];
      // 검출된 오류의 개수만큼 for문을 돌린다.
      for(int i=0; i<bindingResult.getAllErrors().size(); i++) {
        // 생성한 배열에 오류메시지를 담는다.
        error[i] = bindingResult.getAllErrors().get(i).getDefaultMessage();
      }
      return error;
    }
    // 에러 없으면 공백 배열을 리턴.
    else return new String[] {""};
  }
  // 문의 DB저장.
  @PostMapping("/qnaInput")
  public String qnaInputPost(RedirectAttributes rttr, QnADTO dto) {
    if(dto.getFromEmail().isEmpty() || dto.getDearEmail().isEmpty() || dto.getTitle().length()>20 || dto.getTitle().isEmpty() || dto.getContent().isEmpty()) {
      rttr.addFlashAttribute("message", "잘못된 접근입니다.");
      return "redirect:/";
    }
    try {
      qnaService.insertQnA(dto);
    } catch (Exception e) {
      rttr.addFlashAttribute("message", "1:1문의에 실패했습니다.\n잠시 후, 다시 시도해주세요.");
      return "redirect:/qna/qnaList";
    }
    rttr.addFlashAttribute("message", "문의가 정상적으로 등록되었습니다.");
    return "redirect:/qna/qnaList";
  }

  @GetMapping("/qnaAnswer")
  public String qnaAnswerGet(Model model, PageDTO pageDTO) {
    pageDTO.setSection("qnaAdmin");
    pageDTO = pagination.pagination(pageDTO);

    model.addAttribute("pageDTO", pageDTO);
    model.addAttribute("userCsrf", true);
    return "qna/qnaAnswer";
  }
  @ResponseBody
  @PostMapping("/qnaAnswer")
  public int qnaAnswerPost(Long id, String content) {
    try {
      qnaService.insertQnAAnswer(id, content);
    } catch (Exception e) {return 0;}
    return 1;
  }

  @GetMapping("/qnaNewWindow/{id}")
  public String qnaNewWindowGet(Authentication authentication, Model model, RedirectAttributes rttr,
                                @PathVariable Long id) {
    List<QnA> qnaList = qnaService.selectQnAParentId(id);

    /*
      주소를 통해 다른 채팅에 접근할 경우 홈화면으로 강제이동.
      1.parent_id가 없는 id일 경우.
      2.보내는 사람 혹은 받는 사람에 자신이 포함되지 않을 경우.
    */
    if(qnaList.isEmpty() ||
      (!qnaList.getFirst().getFromEmail().getEmail().equals(authentication.getName()) &&
       !qnaList.getFirst().getDearEmail().getEmail().equals(authentication.getName()))) {
      rttr.addFlashAttribute("message", "잘못된 접근입니다.");
      return "redirect:/";
    }

    // 기독체크(getLast가 1개일 경우 null에러를 뱉기 때문에 나눴다).
    // 보낸 채팅이 여러개일 경우.
    if(qnaList.size() > 1) {
      if(!qnaList.getLast().getFromEmail().getEmail().equals(authentication.getName())) qnaService.updateOpenSWOK(qnaList.getFirst());
      else qnaService.updateOpenSWNO(qnaList.getFirst());
    }
    // 보낸 채팅이 한 개일 경우.
    else if(qnaList.size() == 1) {
      if(!qnaList.getFirst().getFromEmail().getEmail().equals(authentication.getName()))  qnaService.updateOpenSWOK(qnaList.getFirst());
      else qnaService.updateOpenSWNO(qnaList.getFirst());
    }
    
    model.addAttribute("qnaList", qnaList);
    model.addAttribute("id", id);
    // 조치가 끝난 채팅일 경우 더이상 채팅을 보내지 못하도록, 자동 새로고침을 정지시키기 위해 문의현황을 보낸다.
    model.addAttribute("progress", qnaService.selectQnAId(id).getProgress());
    return "qna/qnaNewWindow";
  }
  @ResponseBody
  @PostMapping("/qnaNewWindow")
  public Long qnaNewWindowPost(Authentication authentication, Long id) {
    List<QnA> qnaList = qnaService.selectQnAParentId(id);
    // 채팅방 마지막 채팅을 보낸 사람이 내가 아니면 마지막 채팅의 id를 리턴.
    // 채팅이 존재하지 않으면 잘못된 접근(-1).
    if(qnaList.size() < 1) return -1L;
    else if(qnaList.size() > 1) {
      if(!qnaList.getLast().getFromEmail().getEmail().equals(authentication.getName())) return qnaList.getLast().getId();
      else return 0L;
    }
    else {
      if(!qnaList.getFirst().getFromEmail().getEmail().equals(authentication.getName())) return qnaList.getFirst().getId();
      else return 0L;
    }
  }

  @ResponseBody
  @PostMapping("/qnaProgressChange")
  public int qnaProgressChangePost(Long id, String setProgress) {
    Progress progress;
    if(setProgress.equals("RESOLVING")) progress = Progress.RESOLVING;
    else if(setProgress.equals("RESOLVED")) progress = Progress.RESOLVED;
    else if(setProgress.equals("UNRESOLVABLE")) progress = Progress.UNRESOLVABLE;
    else if(setProgress.equals("DELETE")) progress = Progress.DELETE;
    else return -1;

    try {
      if(progress == Progress.DELETE) qnaService.deleteQnA(id);
      else qnaService.updateProgress(id, progress);
    } catch (Exception e) {
      return 0;
    }
    return 1;
  }

  @GetMapping("/qnaDelete")
  public String qnaDeleteGet(Model model, PageDTO pageDTO) {
    pageDTO.setSection("qnaDelete");
    pageDTO = pagination.pagination(pageDTO);

    model.addAttribute("pageDTO", pageDTO);
    model.addAttribute("userCsrf", true);
    return "qna/qnaDelete";
  }
}
