package com.example.SpringGroupBB.controller;

import com.example.SpringGroupBB.common.Pagination;
import com.example.SpringGroupBB.constant.Role;
import com.example.SpringGroupBB.dto.BoardDTO;
import com.example.SpringGroupBB.dto.ComplaintDTO;
import com.example.SpringGroupBB.dto.PageDTO;
import com.example.SpringGroupBB.entity.Board;
import com.example.SpringGroupBB.entity.BoardReply;
import com.example.SpringGroupBB.entity.Member;
import com.example.SpringGroupBB.repository.BoardReplyRepository;
import com.example.SpringGroupBB.repository.BoardRepository;
import com.example.SpringGroupBB.repository.MemberRepository;
import com.example.SpringGroupBB.service.AdminService;
import com.example.SpringGroupBB.service.BoardReplyService;
import com.example.SpringGroupBB.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

  private final BoardService boardService;
  private final BoardRepository boardRepository;
  private final BoardReplyRepository boardReplyRepository;
  private final MemberRepository memberRepository;
  private final Pagination pagination;
  private final AdminService adminService;
  private final BoardReplyService boardReplyService;

  @GetMapping("/boardList")
  public String guestListGet(Model model, PageDTO pageVO, @RequestParam(required = false) String searchString) {
    pageVO.setSection("board");
    pageVO = pagination.pagination(pageVO);

    model.addAttribute("pageVO", pageVO);
    model.addAttribute("isSearch", searchString != null && !searchString.isEmpty());
    model.addAttribute("boardList", boardService.getBoardListWithNoticeFirst());

    Map<Long,Boolean> expiredMap = boardService.getNoticeExpiredMap(boardService.getBoardListWithNoticeFirst());
    model.addAttribute("expiredMap", expiredMap);
    return "board/boardList";
  }

  @GetMapping("/boardInput")
  public String boardInputGet(HttpSession session, Authentication authentication) {

    return "board/boardInput";
  }

  @PostMapping("/boardInput")
  public String boardInputPost(BoardDTO dto, HttpServletRequest request,
                               Authentication authentication,
                               RedirectAttributes rttr,
                               Member member) {
    // 유저 이름 위조 감지.
    String email = authentication.getName();
    member = memberRepository.findByEmail(email).get();
    if(!dto.getName().equals(member.getName())) {
      rttr.addFlashAttribute("message", "비정상적인 접근이 감지되었습니다.");
      return "redirect:/";
    }

    if (dto.getTitle() == null || dto.getTitle().trim().equals("") ||
            dto.getContent() == null || dto.getContent().trim().equals("")) {
      rttr.addFlashAttribute("message", "제목과 내용은 필수 입력입니다.");
      return "redirect:/board/boardInput";
    }

    if (dto.getNoticeSw() == null) dto.setNoticeSw("NO");
    dto.setHostIp(request.getRemoteAddr());
    Board board = Board.dtoToEntity(dto, member);
    Board board_ = boardService.setBoardInput(board);
    if (board_ != null) {
      rttr.addFlashAttribute("message", "게시판 등록 성공");
    } else {
      rttr.addFlashAttribute("message", "게시판 등록 실패");
    }
    return "redirect:/board/boardList";
  }

  @GetMapping("/boardContent")
  public String boardContentGet(Model model, Long id, PageDTO pageVO, HttpSession session,
                                RedirectAttributes rttr,
                                Authentication authentication) {
    // CSRF Token  처리(AJax에서 post처리시)
    model.addAttribute("userCsrf", true);

    // 글 조회수 증가처리(중복방지)
    List<String> contentNum = (List<String>) session.getAttribute("sDuplicate");
    if(contentNum == null) contentNum = new ArrayList<>();
    String imsiNum = "board" + id;
    if(!contentNum.contains(imsiNum)) {
      boardService.setBoardReadNumPlus(id);
      contentNum.add(imsiNum);
    }
    session.setAttribute("sDuplicate", contentNum);

    // 이전글/다음글 가져오기
    Board preVO = boardService.getPreNextSearch(id, "pre");
    Board nextVO = boardService.getPreNextSearch(id, "next");
    model.addAttribute("preVO", preVO);
    model.addAttribute("nextVO", nextVO);

    // 원본글 가져오기
    Board board = boardService.getBoardContent(id);
    // board가 null일 경우의 로직 추가
    if(board == null) {
      rttr.addFlashAttribute("message", "게시글을 찾을 수 없습니다.");
      return "redirect:/board/boardList";
    }
    model.addAttribute("board", board);

    // 본인 인증하기
    pageVO.setOwner(authentication != null && authentication.getName().equals(board.getMember().getEmail()));

    model.addAttribute("pageVO", pageVO);

    // 현재 게시글의 관련 댓글 가져오기
    List<BoardReply> replyVos = boardService.getBoardReply(id);

    model.addAttribute("replyVos", replyVos);

    model.addAttribute("newLine", System.lineSeparator());

    return "board/boardContent";
  }

  // 댓글 입력
  @ResponseBody
  @PostMapping("/boardReplyInput")
  public int boardReplyInputPost(HttpServletRequest request,
                                 Authentication authentication,
                                 @RequestParam Long boardId,
                                 @RequestParam String name,
                                 @RequestParam String content) {
//    Board board = boardRepository.findById(boardId).get();
    Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalStateException("원본글 없음"));
    Member member = memberRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalStateException("회원 없음"));
    BoardReply boardReply = BoardReply.builder()
            .board(board)   // board테이블에서 boardId검색
            .member(member) // member테이블에서 email검색
            .name(name)
            .content(content)
            .hostIp(request.getRemoteAddr())
            .build();

    boardReplyRepository.save(boardReply);
    return 1;
  }

  @ResponseBody
  @GetMapping("/boardReplyDelete")
  public void boardReplyDeleteGet(Long id) {
    boardReplyRepository.deleteById(id);
  }

  // 댓글 수정
  @ResponseBody
  @PostMapping("/boardReplyUpdateCheckOk")
  public void boardReplyUpdateCheckOkPost(
          @RequestParam Long id,
          @RequestParam String content) {
    BoardReply boardReply = boardReplyRepository.findById(id).orElseThrow();
    boardReply.setContent(content);
    boardReplyRepository.save(boardReply);
  }
  /*
  @GetMapping("/boardDelete")
  public String boardDeleteGet(Long id, int pag,
                               HttpServletRequest request,
                               RedirectAttributes rttr) {

    // db의 자료삭제처리(댓글이 있다면, 댓글 삭제후 원본글을 삭제처리해야함)
    Optional<BoardReply> boardReply = boardReplyRepository.findByBoardId(id);
    if (boardReply.isPresent()) {
      rttr.addFlashAttribute("message", "게시글에 댓글이 존재합니다. \n\n댓글을 먼저 삭제해 주세요.");
      return "redirect:/board/boardContent?id="+id+"&pag="+pag;
    }
    else {
      // 댓글이 없으면, 1.이미지 삭제처리
      Board board = boardRepository.findById(id).orElseThrow();
      String realPath = request.getServletContext().getRealPath("/ckeditorUpload/");
      boardService.setBoardImageDelete(board.getContent(), realPath);

      // 2. db에서 게시글 삭제처리
      boardRepository.deleteById(id);

      rttr.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
      return "redirect:/board/boardList";
    }
  }

  board → boardReply 1:N 양방향 연관관계에서 게시글 삭제 시 댓글이 존재하면 외래키 제약 때문에 삭제 불가
  Optional<BoardReply>로 댓글 1개만 조회하기 때문에 댓글이 여러 개여도 정확히 판단되지 않음
  관리자, 작성자 여부를 체크하지 않아 댓글이 1개라도 존재하면 삭제가 차단 됨
  */
  // 게시글 삭제 로직 수정(이동준)
  @GetMapping("/boardDelete")
  public String boardDeleteGet(Long id, int pag,
                               HttpServletRequest request,
                               RedirectAttributes rttr,
                               Authentication authentication) {
    // 삭제할 게시글 조회
    Board board = boardRepository.findById(id).orElseThrow();

    // 로그인 사용자 정보 가져오기
    Member loginMember = memberRepository.findByEmail(authentication.getName()).orElseThrow();
    // 관리자 여부 판단
    boolean isAdmin = loginMember.getRole() == Role.ADMIN;
    // 게시글 작성자 판단(원본글 주인)
    boolean isWriter = loginMember.getEmail().equals(board.getMember().getEmail());

    // 관리자 또는 해당글 작성자일 경우 강제 삭제
    if(isAdmin || isWriter) {
      if(!board.getComplaint().equals("NO")) {
        rttr.addFlashAttribute("message", "신고접수된 게시물은 삭제할 수 없습니다.");
        return "redirect:/board/boardList";
      }

      // 댓글 전체 삭제(board와 boardReply 2개의 DB를 조회함으로 Transactional 필요)
      boardService.ReplyDeleteByBoardId(id);
      // 게시글 이미지 삭제
      String realPath = request.getServletContext().getRealPath("/ckeditorUpload");
      boardService.setBoardImageDelete(board.getContent(), realPath);

      // 게시글 삭제
      boardRepository.deleteById(id);

      rttr.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
      return "redirect:/board/boardList";
    }
    // 기존 로직
    Optional<BoardReply> boardReply = boardReplyRepository.findByBoardId(id);
    if (boardReply.isPresent()) {
      rttr.addFlashAttribute("message", "게시글에 댓글이 존재합니다. \n\n댓글을 먼저 삭제해 주세요.");
      return "redirect:/board/boardContent?id="+id+"&pag="+pag;
    }
    else {
      // 댓글이 없으면, 1.이미지 삭제처리
      board = boardRepository.findById(id).orElseThrow();
      String realPath = request.getServletContext().getRealPath("/ckeditorUpload/");
      boardService.setBoardImageDelete(board.getContent(), realPath);

      // 2. db에서 게시글 삭제처리
      boardRepository.deleteById(id);

      rttr.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
      return "redirect:/board/boardList";
    }
  }

  // 좋아요/싫어요 중복불허
  @ResponseBody
  @PostMapping("/boardGoodCheckPlusMinus")
  public int boardGoodCheckPlusMinusGet(HttpSession session ,Long id, int goodCnt) {
    int res = 0;

    // 세션에서 각 글의 좋아요/싫어요 상태 가져오기
    Map<Long, Integer> goodStatus = (Map<Long, Integer>) session.getAttribute("sGoodStatus");
    if(goodStatus == null) goodStatus = new HashMap<>();

    // 현재 글의 상태 확인 (1: 좋아요, -1: 싫어요, 0: 클릭안함)
    int currentStatus = goodStatus.getOrDefault(id, 0);

    // 같은 버튼을 누른 경우 (취소)
    if(currentStatus == goodCnt) {
      boardRepository.setBoardGoodNumPlusMinus(id, -goodCnt); // 반대값으로 취소
      goodStatus.put(id, 0); // 상태 초기화
      res = 0; // 성공
    }
    // 다른 버튼을 누른 경우 (좋아요 ↔ 싫어요 전환)
    else if(currentStatus != 0 && currentStatus != goodCnt) {
      int cancelCnt = -currentStatus; // 기존 상태 취소
      boardRepository.setBoardGoodNumPlusMinus(id, cancelCnt);
      boardRepository.setBoardGoodNumPlusMinus(id, goodCnt); // 새 상태 적용

      goodStatus.put(id, goodCnt);
      res = 0; // 성공
    }
    // 처음 클릭하는 경우
    else if(currentStatus == 0) {
      boardRepository.setBoardGoodNumPlusMinus(id, goodCnt);
      goodStatus.put(id, goodCnt);
      res = 0; // 성공
    }

    session.setAttribute("sGoodStatus", goodStatus);
    return res;
  }

  @GetMapping("/boardUpdate")
  public String boardUpdateGet(@RequestParam("id") Long id,
                               @RequestParam(value = "pag", defaultValue = "1") int pag,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                               Model model, RedirectAttributes rttr,
                               Authentication authentication) {

    Board board = boardService.getBoardContent(id);
    if(board == null) {
      rttr.addFlashAttribute("message", "게시글을 찾을 수 없습니다.");
      return "redirect:/board/boardList";
    }

    if(!authentication.getName().equals(board.getMember().getEmail())) {
      throw new IllegalStateException("본인만 수정할 수 있습니다.");
    }

    BoardDTO dto = BoardDTO.entityToDto(Optional.of(board));

    if(dto.getContent() != null && dto.getContent().contains("src=\"/")) {
      boardService.imgBackup(dto.getContent());
    }

    model.addAttribute("dto", dto);
    model.addAttribute("pag",pag);
    model.addAttribute("pageSize", pageSize);

    return "board/boardUpdate";
  }

  @PostMapping("/boardUpdate")
  public String boardUpdatePost(@ModelAttribute BoardDTO dto,
                                @RequestParam(value = "pag", defaultValue = "1") int pag,
                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                Authentication authentication,
                                RedirectAttributes rttr,
                                HttpServletRequest request) {

    // 1. 기존 게시글 Entity 조회
    Board board = boardService.getBoardContent(dto.getId());
    if(board == null) {
      throw new IllegalStateException("게시글을 찾을 수 없습니다.");
    }

    // 2. 본인 인증
    if(!authentication.getName().equals(board.getMember().getEmail())) {
      throw new IllegalStateException("본인만 수정할 수 있습니다.");
    }

    // 3. 수정 가능한 필드만 변경
    board.setTitle(dto.getTitle());
    board.setContent(dto.getContent());
    board.setOpenSw(dto.getOpenSw());
    board.setNoticeSw(dto.getNoticeSw() == null ? "NO" : dto.getNoticeSw());
    board.setHostIp(request.getRemoteAddr());

    // 4. DB 저장 (JPA save를 사용하면 update!)
    Board savedBoard = boardService.setBoardInput(board);

    // 5. 메시지 및 리다이렉트
    String message = (savedBoard != null) ? "수정이 완료되었습니다." : "수정이 실패했습니다.";
    rttr.addFlashAttribute("message", message);

    return "redirect:/board/boardList";
  }
  // 신고글 처리하기
  @ResponseBody
  @PostMapping("/boardComplaintInput")
  public int boardComplaintInputPost(@ModelAttribute ComplaintDTO dto){
    int res = 0;
    res = adminService.setBoardComplaintInput(dto);
    if(res != 0) adminService.setBoardTableComplaintOk(dto.getPartId());
    return res;
  }

  // 검색용 boardContent
  @GetMapping("/boardSearchContent/{id}")
  public String boardSearchContentGet(Model model, @PathVariable Long id) {
    model.addAttribute("board", boardService.selectSearchID(id));
    model.addAttribute("replyList", boardReplyRepository.findByBoardIdOrderById(id));
    return "search/boardSearchContent";
  }

}
