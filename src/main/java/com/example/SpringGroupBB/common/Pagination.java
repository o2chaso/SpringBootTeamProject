package com.example.SpringGroupBB.common;

import com.example.SpringGroupBB.constant.Progress;
import com.example.SpringGroupBB.dto.LoginHistoryDTO;
import com.example.SpringGroupBB.dto.PageDTO;
import com.example.SpringGroupBB.entity.*;
import com.example.SpringGroupBB.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Pagination {
  private final QnARepository qnaRepository;
  private final MemberRepository memberRepository;
  private final BoardRepository boardRepository;
  private final ProductRepository productRepository;
  private final LoginHistoryRepository loginHistoryRepository;
  private final ComplaintRepository complaintRepository;

  public PageDTO pagination(PageDTO dto) {	// 각각의 변수로 받으면 초기값처리를 spring이 자동할수 있으나, 객체로 받으면 개별 문자/객체 자료에는 null이 들어오기에 따로 초기화 작업처리해야함.
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    int pag = dto.getPag();
    int pageSize = dto.getPageSize() == 0 ? 10 : dto.getPageSize();
    int startIndexNo = pag * pageSize;
    String part = dto.getPart() == null ? "" : dto.getPart();

    int totRecCnt = 0, totPage = 0;

    PageRequest pageable = PageRequest.of(pag, pageSize, Sort.by("id").descending());

    if(dto.getSection().equals("member")) {
      Page<Member> page;
      if(dto.getSearchStr() != null) {
        if(dto.getSearchStr().equals("name")) page = memberRepository.findByNameContaining(dto.getSearchString(), pageable);
        else if(dto.getSearchStr().equals("email")) page = memberRepository.findByEmailContaining(dto.getSearchString(), pageable);
        else page = memberRepository.findAll(pageable);
      }
      else page = memberRepository.findAll(pageable);
      dto.setMemberList(page.getContent());
      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();
    }
    else if(dto.getSection().equals("product")) {
      Page<Product> page;

      if(dto.getSearch() != null && !dto.getSearch().isEmpty()) {
        if(dto.getSearch().equals("sensorName")) page = productRepository.findBySensorNameContaining(dto.getSearchString(), pageable);
        else page = productRepository.findByManufacturerContaining(dto.getSearchString(), pageable);
      }
      else page = productRepository.findAll(pageable);
      dto.setProductList(page.getContent());

      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();
    }

    pageable = PageRequest.of(pag, pageSize, Sort.by(Sort.Order.desc("noticeSw"), Sort.Order.desc("id")));
    if(dto.getSection().equals("board")) {
      Page<Board> page;

      if (dto.getSearch() != null && !dto.getSearch().isEmpty()) {
        if(dto.getSearch().equals("title")) page = boardRepository.findByTitleContaining(dto.getSearchString(), pageable);
        else if(dto.getSearch().equals("name")) page = boardRepository.findByNameContaining(dto.getSearchString(), pageable);
        else page = boardRepository.findByContentContaining(dto.getSearchString(), pageable);
      }
      else page = boardRepository.findAll(pageable);

      List<Board> boardList = page.getContent();

      boardList.forEach((board) -> {
        board.setHourDiff(Duration.between(board.getWDate(), LocalDateTime.now()).toHours());
        board.setDateDiff(LocalDateTime.now().getDayOfMonth() - board.getWDate().getDayOfMonth());
        board.setReplyCnt(board.getBoardReplies().size());
      });

      dto.setBoardList(boardList);

      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();
    }

    // 신고 리스트 페이징 처리
    else if(dto.getSection().equals("complaint")){
      pageable = PageRequest.of(pag, pageSize, Sort.by(Sort.Order.desc("id")));

      Page<Complaint> page;
      String progressFilter = dto.getPart();

      if(progressFilter == null || progressFilter.isEmpty()) {
        page = complaintRepository.findAll(pageable);
      } else {
        page = complaintRepository.findByProgress(progressFilter, pageable);
      }

      dto.setComplaintList(page.getContent());

      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();

    }


    // 일반 문의.
    else if(dto.getSection().equals("qna")) {
      // 일반회원의 경우 처리중인 것을 처음에 보여준다.
      if(dto.getProgress() == null) dto.setProgress("RESOLVING");

      // 마지막 갱신일 기준으로 내림차순.
      pageable = PageRequest.of(pag, pageSize, Sort.by("lastDate").descending());

      // 문의현황.
      Progress progress;
      if(dto.getProgress().equals("RESOLVING")) progress = Progress.RESOLVING;
      else if(dto.getProgress().equals("RESOLVED")) progress = Progress.RESOLVED;
      else if(dto.getProgress().equals("UNRESOLVABLE")) progress = Progress.UNRESOLVABLE;
      else progress = null;

      Page<QnA> page;
      Member fromEmail;
      // 자신의 문의를 현황 별로 검색.
      if(progress != null) {
        fromEmail = memberRepository.findByEmail(authentication.getName()).orElse(null);
        page = qnaRepository.findByFromEmailAndProgressOrderByOpenSWDescLastDateDesc(fromEmail, progress, pageable);
      }
      // 문의현황 없으면 답변(ANSWER)를 제외한 자신의 문의 목록 검색.
      else {
        fromEmail = memberRepository.findByEmail(authentication.getName()).orElse(null);
        page = qnaRepository.findByFromEmailAndProgressNotOrderByOpenSWDescLastDateDesc(fromEmail, Progress.ANSWER, pageable);
      }

      // Page객체를 List객체로 변환.
      dto.setQnaList(page.getContent());

      // 전체 개수와 전체 페이지 수.
      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();
    }
    // 관리자 문의.
    else if(dto.getSection().equals("qnaAdmin")) {
      // 관리자의 경우 전체 문의를 처음에 보여준다.
      if(dto.getProgress() == null) dto.setProgress("");

      // 마지막 갱신일 기준으로 내림차순.
      pageable = PageRequest.of(pag, pageSize, Sort.by("lastDate").descending());

      // 문의현황.
      Progress progress;
      if(dto.getProgress().equals("RESOLVING")) progress = Progress.RESOLVING;
      else if(dto.getProgress().equals("RESOLVED")) progress = Progress.RESOLVED;
      else if(dto.getProgress().equals("UNRESOLVABLE")) progress = Progress.UNRESOLVABLE;
      else progress = null;

      Page<QnA> page;
      // 문의 현황별로 검색.
      if(progress != null) {
        page = qnaRepository.findByProgressOrderByOpenSWAscLastDateDesc(progress, pageable);
        // Page객체 List객체로 변환.
        dto.setQnaList(page.getContent());
        // 전체 개수와 전체 페이지 수.
        totRecCnt = (int) page.getTotalElements();
        totPage = page.getTotalPages();
      }
      // 답변(ANSWER)을 제외한 문의 목록 검색.
      else {
        dto.setQnaList(qnaRepository.selectQnAAdminProgress(startIndexNo, pageSize));
        totRecCnt = qnaRepository.selectQnATotRecCnt();
        totPage = (int)Math.ceil(totRecCnt / (double)pageSize);
      }
    }
    // 문의삭제.
    else if(dto.getSection().equals("qnaDelete")) {
      Page<QnA> page;
      // 마지막 갱신일 기준으로 내림차순.
      pageable = PageRequest.of(pag, pageSize, Sort.by("lastDate").descending());

      // 문의현황이 처리완료, 처리불가인 항목만 보여준다.
      if(dto.getProgress() == null || dto.getProgress().equals("")) {
        Progress progress1 = Progress.RESOLVED;
        Progress progress2 = Progress.UNRESOLVABLE;

        page = qnaRepository.findByProgressOrProgressOrderByOpenSWAscLastDateDesc(progress1, progress2, pageable);
      }
      // 관리자가 보려고하는 문의현황 항목을 보여준다.
      else {
        Progress progress;
        if(dto.getProgress().equals("RESOLVED")) progress = Progress.RESOLVED;
        else if(dto.getProgress().equals("UNRESOLVABLE")) progress = Progress.UNRESOLVABLE;
        else progress = null;
        page = qnaRepository.findByProgressOrderByOpenSWAscLastDateDesc(progress, pageable);
      }

      // Page객체 List객체로 변환.
      dto.setQnaList(page.getContent());

      // 전체 개수와 전체 페이지 수.
      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();
    }
    else if(dto.getSection().equals("History")) {
      pageable = PageRequest.of(pag, pageSize, Sort.by("id").descending());

      LocalDateTime startDateTime = dto.getStartDate() != null ? dto.getStartDate().atStartOfDay() : null;
      LocalDateTime endDateTime = dto.getEndDate() != null ? dto.getEndDate().atTime(23, 59, 59) : null;
      Page<LoginHistory> page;
      switch (dto.getSearchStr() != null ? dto.getSearchStr() : "") {
        case "name":
          page = (startDateTime != null && endDateTime != null)
                  ? loginHistoryRepository.findByMember_NameContainingAndCreateDateBetween(dto.getSearchString(), startDateTime, endDateTime, pageable)
                  : loginHistoryRepository.findByMember_NameContaining(dto.getSearchString(), pageable);
          break;

        case "email":
          page = (startDateTime != null && endDateTime != null)
                  ? loginHistoryRepository.findByMember_EmailContainingAndCreateDateBetween(dto.getSearchString(), startDateTime, endDateTime, pageable)
                  : loginHistoryRepository.findByMember_EmailContaining(dto.getSearchString(), pageable);
          break;

        default:
          page = (startDateTime != null && endDateTime != null)
                  ? loginHistoryRepository.findByCreateDateBetween(startDateTime, endDateTime, pageable)
                  : loginHistoryRepository.findAll(pageable);
          break;
      }
      dto.setLoginHistoryList(page.map(LoginHistoryDTO::entityToDto).getContent());
      totRecCnt = (int) page.getTotalElements();
      totPage = page.getTotalPages();
    }

    int curScrStartNo = totRecCnt - startIndexNo;

    int blockSize = 3;
    int curBlock = pag / blockSize;
    int lastBlock = (totPage - 1) / blockSize;
    dto.setPag(pag+1);
    dto.setPageSize(pageSize);
    dto.setTotRecCnt(totRecCnt);
    dto.setTotPage(totPage);
    dto.setStartIndexNo(startIndexNo);
    dto.setCurScrStartNo(curScrStartNo);
    dto.setBlockSize(blockSize);
    dto.setCurBlock(curBlock);
    dto.setLastBlock(lastBlock);

    if(dto.getSearch() != null) {
      if(dto.getSearch().equals("title")) dto.setSearch("글제목");
      else if(dto.getSearch().equals("name")) dto.setSearch("글쓴이");
      else if(dto.getSearch().equals("content")) dto.setSearch("글내용");
    }
    dto.setSearch(dto.getSearch());
    dto.setSearchStr(dto.getSearchStr());

    dto.setPart(part);
    dto.setBoardFlag(dto.getBoardFlag());

    return dto;
  }
}
